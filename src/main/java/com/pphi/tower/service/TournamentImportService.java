package com.pphi.tower.service;

import com.pphi.tower.config.AwsProperties;
import com.pphi.tower.repository.S3TournamentRepository;
import com.pphi.tower.repository.TournamentRepository;
import com.pphi.tower.repository.TournamentRepository.BattleConditionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TournamentImportService {

    private static final Logger log = LoggerFactory.getLogger(TournamentImportService.class);

    private static final Map<String, String> TIER_TO_LEAGUE = Map.of(
            "Legend",   "LEGENDS",
            "Champion", "CHAMPION",
            "Platinum", "PLATINUM",
            "Gold",     "GOLD",
            "Silver",   "SILVER"
            // Copper intentionally omitted — not in tournament.league schema
    );

    private final TournamentRepository tournamentRepo;
    private final AwsProperties aws;
    private final ObjectProvider<S3TournamentRepository> s3TournamentRepo;

    public TournamentImportService(TournamentRepository tournamentRepo,
                                   AwsProperties aws,
                                   ObjectProvider<S3TournamentRepository> s3TournamentRepo) {
        this.tournamentRepo = tournamentRepo;
        this.aws = aws;
        this.s3TournamentRepo = s3TournamentRepo;
    }

    public record ImportResult(
            String date,
            Map<String, Integer> conditionsPerLeague,
            List<String> warnings
    ) {
        public String summary() {
            if (conditionsPerLeague.isEmpty()) return "No conditions imported for " + date;
            StringBuilder sb = new StringBuilder("Imported ").append(date).append(" — ");
            conditionsPerLeague.forEach((league, count) ->
                    sb.append(league).append(": ").append(count).append(", "));
            sb.setLength(sb.length() - 2);
            return sb.toString();
        }
    }

    /** Parse + import to DB + upload to S3. Called from the upload endpoint. */
    public ImportResult importAndUpload(String date, String csvContent) throws IOException {
        validateTournamentDate(date);
        validateCsvContent(csvContent);

        ImportResult result = importCsv(date, csvContent);

        S3TournamentRepository s3 = s3TournamentRepo.getIfAvailable();
        if (s3 != null && aws.getS3Bucket() != null) {
            String hash = sha256Hex(date + "\n" + csvContent);
            String key = "tournaments/" + date + "/" + hash + ".csv";
            s3.put(aws.getS3Bucket(), key, csvContent);
            log.debug("Uploaded tournament CSV to s3://{}/{}", aws.getS3Bucket(), key);
        }

        return result;
    }

    /** Parse + import to DB only. Called from the sync path. */
    public ImportResult importCsv(String date, String csvContent) throws IOException {
        Map<String, Integer> conditionsPerLeague = new LinkedHashMap<>();
        List<String> warnings = new ArrayList<>();

        // Build name→id lookup from cached conditions
        Map<String, Long> nameToId = new LinkedHashMap<>();
        for (BattleConditionData c : tournamentRepo.getAllConditions()) {
            nameToId.put(c.name(), c.id());
        }

        // Parse CSV
        Map<Integer, String> colToLeague = new LinkedHashMap<>();   // col index → DB league
        Map<String, List<Long>> leagueToIds = new LinkedHashMap<>(); // DB league → condition ids

        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            String line;
            boolean headerParsed = false;

            while ((line = reader.readLine()) != null) {
                String[] cells = line.split(",", -1);

                if (!headerParsed) {
                    // Header row: blank, Legend, Champion, Platinum, Gold, Silver, Copper
                    for (int col = 1; col < cells.length; col++) {
                        String tierName = cells[col].trim();
                        String league = TIER_TO_LEAGUE.get(tierName);
                        if (league != null) {
                            colToLeague.put(col, league);
                            leagueToIds.put(league, new ArrayList<>());
                        } else if (!tierName.isEmpty()) {
                            warnings.add("Skipped tier column: " + tierName);
                        }
                    }
                    headerParsed = true;
                    continue;
                }

                // Data row: slot index, cond for col1, cond for col2, ...
                for (Map.Entry<Integer, String> entry : colToLeague.entrySet()) {
                    int col = entry.getKey();
                    if (col >= cells.length) continue;
                    String condName = cells[col].trim();
                    if (condName.isEmpty() || condName.equalsIgnoreCase("None")) continue;

                    Long id = nameToId.get(condName);
                    if (id == null) {
                        String warning = "Unknown condition '" + condName + "' — skipped";
                        if (!warnings.contains(warning)) {
                            warnings.add(warning);
                            log.warn("Tournament import: {}", warning);
                        }
                    } else {
                        List<Long> ids = leagueToIds.get(entry.getValue());
                        if (!ids.contains(id)) ids.add(id);
                    }
                }
            }
        }

        // Upsert each league that has at least one condition
        for (Map.Entry<String, List<Long>> entry : leagueToIds.entrySet()) {
            List<Long> ids = entry.getValue();
            if (!ids.isEmpty()) {
                tournamentRepo.save(date, entry.getKey(), ids);
                conditionsPerLeague.put(entry.getKey(), ids.size());
            }
        }

        return new ImportResult(date, conditionsPerLeague, warnings);
    }

    /**
     * Look up the CSV for a specific tournament date from S3 and import it.
     * Returns empty if S3 is not configured or no file exists for the given date.
     */
    public Optional<ImportResult> fetchDateFromS3(String date) throws IOException {
        validateTournamentDate(date);
        S3TournamentRepository s3 = s3TournamentRepo.getIfAvailable();
        if (s3 == null || aws.getS3Bucket() == null) return Optional.empty();

        List<String> keys = s3.listKeys(aws.getS3Bucket(), "tournaments/" + date + "/");
        if (keys.isEmpty()) return Optional.empty();

        String csvContent = s3.downloadAsString(aws.getS3Bucket(), keys.get(0));
        return Optional.of(importCsv(date, csvContent));
    }

    /** List all tournament CSV keys from S3 and re-import each. Idempotent. */
    public List<ImportResult> syncFromS3() throws IOException {
        S3TournamentRepository s3 = s3TournamentRepo.getIfAvailable();
        if (s3 == null || aws.getS3Bucket() == null) return List.of();

        List<String> keys = s3.listKeys(aws.getS3Bucket());
        List<ImportResult> results = new ArrayList<>();

        for (String key : keys) {
            // Key format: tournaments/<date>/<hash>.csv
            String[] parts = key.split("/");
            if (parts.length < 3) {
                log.warn("Skipping unexpected S3 key format: {}", key);
                continue;
            }
            String date = parts[1];
            String csvContent = s3.downloadAsString(aws.getS3Bucket(), key);
            results.add(importCsv(date, csvContent));
            log.info("Synced tournament CSV for date {} from s3://{}/{}", date, aws.getS3Bucket(), key);
        }

        return results;
    }

    private static void validateTournamentDate(String date) {
        if (date == null || date.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tournament date is required");
        }
        LocalDate parsed;
        try {
            parsed = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format — expected YYYY-MM-DD");
        }
        DayOfWeek day = parsed.getDayOfWeek();
        if (day != DayOfWeek.WEDNESDAY && day != DayOfWeek.SATURDAY) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tournament date must be a Wednesday or Saturday, got " + day.name().toLowerCase());
        }
    }

    private void validateCsvContent(String csvContent) {
        if (csvContent == null || csvContent.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CSV file is empty");
        }
        String firstLine = csvContent.lines().findFirst().orElse("");
        boolean hasKnownTier = TIER_TO_LEAGUE.keySet().stream().anyMatch(firstLine::contains);
        if (!hasKnownTier) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "CSV does not contain recognized tournament tier columns (Legend, Champion, Platinum, Gold, Silver)");
        }
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
