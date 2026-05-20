package com.pphi.tower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.config.AppConfig;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.model.battlehistory.BattleReport;
import com.pphi.tower.model.battlehistory.Currencies;
import com.pphi.tower.model.battlehistory.SectionHeader;
import com.pphi.tower.parser.BattleHistoryParser;
import com.pphi.tower.repository.RunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
public class ReportWatcherService {

    private static final Logger log = LoggerFactory.getLogger(ReportWatcherService.class);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.of("America/Los_Angeles"));

    private final AppConfig config;
    private final BattleHistoryParser parser;
    private final RunRepository repository;
    private final ObjectMapper objectMapper;

    public ReportWatcherService(AppConfig config,
                                BattleHistoryParser parser,
                                RunRepository repository,
                                ObjectMapper objectMapper) {
        this.config = config;
        this.parser = parser;
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void scanOnStartup() {
        Path reportsRoot = Path.of(config.getReportsPath());
        if (!Files.exists(reportsRoot)) {
            log.warn("Reports path does not exist: {}", reportsRoot.toAbsolutePath());
            return;
        }
        log.info("Scanning reports directory: {}", reportsRoot.toAbsolutePath());
        try (Stream<Path> paths = Files.walk(reportsRoot)) {
            paths.filter(p -> p.toString().endsWith(".txt"))
                 .filter(Files::isRegularFile)
                 .forEach(this::processFile);
        } catch (IOException e) {
            log.error("Failed to walk reports directory", e);
        }
    }

    private void processFile(Path file) {
        String folder   = file.getParent().getFileName().toString();
        String filename = file.getFileName().toString();

        if (repository.existsByFolderAndFilename(folder, filename)) {
            log.debug("Skipping already-indexed report: {}/{}", folder, filename);
            return;
        }

        try {
            BattleHistory history = parser.parse(file);
            BattleReport report   = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
            Currencies currencies = (Currencies) history.sectionMap().get(SectionHeader.CURRENCIES);

            if (report == null) {
                log.warn("No BattleReport section found in {}", file);
                return;
            }

            String battleDate = DATE_FMT.format(report.battleReportDate());
            String id = buildId(battleDate, report.tier(), report.wave());

            // Guard against duplicate IDs from different files (same date/tier/wave)
            if (repository.existsById(id)) {
                log.warn("Duplicate report ID {} from file {}/{} — skipping", id, folder, filename);
                return;
            }

            double cellsEarned = currencies != null
                    ? toRaw(currencies.cellsEarned())
                    : 0.0;
            double cellsPerHour = toRaw(report.cellsPerHour());
            double coinsPerHour = toRaw(report.coinsPerHour());

            String payload = objectMapper.writeValueAsString(history);

            repository.insert(
                    id, filename, folder, battleDate,
                    report.tier(), report.wave(),
                    cellsEarned,
                    report.realTime().getSeconds(),
                    report.gameTime().getSeconds(),
                    cellsPerHour, coinsPerHour,
                    report.killedBy(),
                    report.towerEra(),
                    payload,
                    report.battleReportDate().getEpochSecond());

            log.info("Indexed report: {} -> {}/{}", id, folder, filename);

        } catch (Exception e) {
            log.error("Failed to parse report: {}/{}", folder, filename, e);
        }
    }

    public static String buildId(String battleDate, int tier, int wave) {
        return String.format("%s_T%d_W%d", battleDate, tier, wave);
    }

    private double toRaw(com.pphi.tower.model.TowerNumber tn) {
        if (tn == null || tn.amount() == null) return 0.0;
        if (tn.scaleSuffix() == null) return tn.amount().doubleValue();
        return tn.amount().multiply(tn.scaleSuffix().getScientificNotation()).doubleValue();
    }
}
