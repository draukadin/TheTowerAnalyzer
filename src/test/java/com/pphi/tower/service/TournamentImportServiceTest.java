package com.pphi.tower.service;

import com.pphi.tower.config.AwsProperties;
import com.pphi.tower.repository.S3TournamentRepository;
import com.pphi.tower.repository.TournamentRepository;
import com.pphi.tower.repository.TournamentRepository.BattleConditionData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentImportServiceTest {

    @Mock private TournamentRepository tournamentRepo;
    @Mock private AwsProperties aws;
    @Mock private ObjectProvider<S3TournamentRepository> s3Provider;
    @Mock private S3TournamentRepository s3Repo;

    private TournamentImportService service;

    // 2026-06-24 = Wednesday, 2026-06-27 = Saturday, 2026-06-22 = Monday
    private static final String WED = "2026-06-24";
    private static final String SAT = "2026-06-27";
    private static final String MON = "2026-06-22";

    private static final List<BattleConditionData> CONDITIONS = List.of(
            new BattleConditionData(1L, "Orb Resistance",  "OR",  "HEAT"),
            new BattleConditionData(2L, "More Bosses",     "MB",  "OVERHEAT"),
            new BattleConditionData(3L, "Health Decay",    "HLD", "OVERHEAT"),
            new BattleConditionData(4L, "More Enemies",    "ME",  "HEAT")
    );

    private static final String VALID_CSV = """
            ,Legend,Champion,Platinum,Gold,Silver,Copper
            0,Orb Resistance,More Bosses,Orb Resistance,Orb Resistance,Orb Resistance,Orb Resistance
            1,Health Decay,None,,None,None,None
            """;

    @BeforeEach
    void setUp() {
        service = new TournamentImportService(tournamentRepo, aws, s3Provider);
    }

    // ── validateTournamentDate — null / blank ────────────────────────────────────

    @Test
    void importAndUpload_nullDate_throws400() {
        var ex = catchThrowableOfType(
                () -> service.importAndUpload(null, VALID_CSV),
                ResponseStatusException.class);
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void importAndUpload_blankDate_throws400() {
        var ex = catchThrowableOfType(
                () -> service.importAndUpload("   ", VALID_CSV),
                ResponseStatusException.class);
        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // ── validateTournamentDate — bad format ──────────────────────────────────────

    @Test
    void importAndUpload_malformedDate_throws400WithFormatMessage() {
        var ex = catchThrowableOfType(
                () -> service.importAndUpload("not-a-date", VALID_CSV),
                ResponseStatusException.class);
        assertThat(ex.getReason()).contains("Invalid date format");
    }

    // ── validateTournamentDate — wrong day ───────────────────────────────────────

    @Test
    void importAndUpload_mondayDate_throws400WithDayMessage() {
        var ex = catchThrowableOfType(
                () -> service.importAndUpload(MON, VALID_CSV),
                ResponseStatusException.class);
        assertThat(ex.getReason()).containsIgnoringCase("Wednesday or Saturday");
    }

    @Test
    void fetchDateFromS3_mondayDate_throws400() {
        var ex = catchThrowableOfType(
                () -> service.fetchDateFromS3(MON),
                ResponseStatusException.class);
        assertThat(ex.getReason()).containsIgnoringCase("Wednesday or Saturday");
    }

    // ── validateCsvContent ───────────────────────────────────────────────────────

    @Test
    void importAndUpload_nullCsv_throws400() {
        var ex = catchThrowableOfType(
                () -> service.importAndUpload(WED, null),
                ResponseStatusException.class);
        assertThat(ex.getReason()).contains("empty");
    }

    @Test
    void importAndUpload_blankCsv_throws400() {
        var ex = catchThrowableOfType(
                () -> service.importAndUpload(WED, "   "),
                ResponseStatusException.class);
        assertThat(ex.getReason()).contains("empty");
    }

    @Test
    void importAndUpload_csvWithNoKnownTier_throws400() {
        var ex = catchThrowableOfType(
                () -> service.importAndUpload(WED, ",Foo,Bar\n0,x,y"),
                ResponseStatusException.class);
        assertThat(ex.getReason()).containsIgnoringCase("recognized tournament tier");
    }

    // ── importCsv — basic parsing ────────────────────────────────────────────────

    @Test
    void importCsv_validCsv_returnsCorrectDate() throws IOException {
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        TournamentImportService.ImportResult result = service.importCsv(WED, VALID_CSV);

        assertThat(result.date()).isEqualTo(WED);
    }

    @Test
    void importCsv_validCsv_legendsLeagueImported() throws IOException {
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        TournamentImportService.ImportResult result = service.importCsv(WED, VALID_CSV);

        assertThat(result.conditionsPerLeague()).containsKey("LEGENDS");
    }

    @Test
    void importCsv_validCsv_championLeagueImported() throws IOException {
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        TournamentImportService.ImportResult result = service.importCsv(WED, VALID_CSV);

        assertThat(result.conditionsPerLeague()).containsKey("CHAMPION");
    }

    @Test
    void importCsv_copperTier_addedToWarnings() throws IOException {
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        TournamentImportService.ImportResult result = service.importCsv(WED, VALID_CSV);

        assertThat(result.warnings()).anyMatch(w -> w.contains("Copper"));
    }

    @Test
    void importCsv_unknownConditionName_warningAddedAndSkipped() throws IOException {
        String csv = ",Legend,Champion\n0,Unknown Condition,Orb Resistance\n";
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        TournamentImportService.ImportResult result = service.importCsv(WED, csv);

        assertThat(result.warnings()).anyMatch(w -> w.contains("Unknown Condition"));
    }

    @Test
    void importCsv_unknownConditionName_onlyWarnedOnce() throws IOException {
        String csv = ",Legend\n0,Unknown\n1,Unknown\n";
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        TournamentImportService.ImportResult result = service.importCsv(WED, csv);

        long count = result.warnings().stream().filter(w -> w.contains("Unknown")).count();
        assertThat(count).isEqualTo(1);
    }

    @Test
    void importCsv_noneCell_skippedWithoutSavingLeague() throws IOException {
        String csv = ",Legend\n0,None\n";
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        TournamentImportService.ImportResult result = service.importCsv(WED, csv);

        verify(tournamentRepo, never()).save(any(), any(), any());
        assertThat(result.conditionsPerLeague()).isEmpty();
    }

    @Test
    void importCsv_emptyCell_skipped() throws IOException {
        String csv = ",Legend\n0,\n";
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        service.importCsv(WED, csv);

        verify(tournamentRepo, never()).save(any(), any(), any());
    }

    @Test
    void importCsv_duplicateConditionInSameLeague_savedOnlyOnce() throws IOException {
        String csv = ",Legend\n0,Orb Resistance\n1,Orb Resistance\n";
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        service.importCsv(WED, csv);

        verify(tournamentRepo).save(eq(WED), eq("LEGENDS"), argThat(ids -> ids.size() == 1));
    }

    @Test
    void importCsv_saturdayDate_importedSuccessfully() throws IOException {
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        TournamentImportService.ImportResult result = service.importCsv(SAT, VALID_CSV);

        assertThat(result.date()).isEqualTo(SAT);
    }

    @Test
    void importCsv_allFiveLeaguesInCsv_allImported() throws IOException {
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        TournamentImportService.ImportResult result = service.importCsv(WED, VALID_CSV);

        assertThat(result.conditionsPerLeague()).containsKeys("LEGENDS", "CHAMPION", "PLATINUM", "GOLD", "SILVER");
    }

    @Test
    void importCsv_columnPastEndOfRow_doesNotThrow() throws IOException {
        // Row is shorter than header — column index beyond row length is safely skipped
        String csv = ",Legend,Champion\n0,Orb Resistance\n";
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        assertThatNoException().isThrownBy(() -> service.importCsv(WED, csv));
    }

    // ── importAndUpload — S3 interaction ─────────────────────────────────────────

    @Test
    void importAndUpload_s3Available_uploadsWithHashedKey() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn("test-bucket");
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        service.importAndUpload(WED, VALID_CSV);

        verify(s3Repo).put(eq("test-bucket"),
                matches("tournaments/" + WED + "/[a-f0-9]{64}\\.csv"),
                eq(VALID_CSV));
    }

    @Test
    void importAndUpload_s3NotAvailable_noS3Call() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(null);
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        service.importAndUpload(WED, VALID_CSV);

        verify(s3Repo, never()).put(any(), any(), any());
    }

    @Test
    void importAndUpload_s3BucketNull_noS3Call() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn(null);
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        service.importAndUpload(WED, VALID_CSV);

        verify(s3Repo, never()).put(any(), any(), any());
    }

    @Test
    void importAndUpload_sameDateAndCsvTwice_sameS3Key() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn("test-bucket");
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        List<String> capturedKeys = new ArrayList<>();
        doAnswer(inv -> { capturedKeys.add(inv.getArgument(1)); return null; })
                .when(s3Repo).put(any(), any(), any());

        service.importAndUpload(WED, VALID_CSV);
        service.importAndUpload(WED, VALID_CSV);

        assertThat(capturedKeys).hasSize(2).satisfies(keys ->
                assertThat(keys.get(0)).isEqualTo(keys.get(1)));
    }

    @Test
    void importAndUpload_differentDates_differentS3Keys() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn("test-bucket");
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        List<String> capturedKeys = new ArrayList<>();
        doAnswer(inv -> { capturedKeys.add(inv.getArgument(1)); return null; })
                .when(s3Repo).put(any(), any(), any());

        service.importAndUpload(WED, VALID_CSV);
        service.importAndUpload(SAT, VALID_CSV);

        assertThat(capturedKeys.get(0)).isNotEqualTo(capturedKeys.get(1));
    }

    // ── fetchDateFromS3 ──────────────────────────────────────────────────────────

    @Test
    void fetchDateFromS3_s3NotAvailable_returnsEmpty() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(null);

        Optional<TournamentImportService.ImportResult> result = service.fetchDateFromS3(WED);

        assertThat(result).isEmpty();
    }

    @Test
    void fetchDateFromS3_noKeysForDate_returnsEmpty() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn("test-bucket");
        when(s3Repo.listKeys(eq("test-bucket"), anyString())).thenReturn(List.of());

        Optional<TournamentImportService.ImportResult> result = service.fetchDateFromS3(WED);

        assertThat(result).isEmpty();
    }

    @Test
    void fetchDateFromS3_keyFound_returnsImportResult() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn("test-bucket");
        when(s3Repo.listKeys(eq("test-bucket"), contains(WED)))
                .thenReturn(List.of("tournaments/" + WED + "/abc123.csv"));
        when(s3Repo.downloadAsString(eq("test-bucket"), anyString())).thenReturn(VALID_CSV);
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        Optional<TournamentImportService.ImportResult> result = service.fetchDateFromS3(WED);

        assertThat(result).isPresent();
        assertThat(result.get().date()).isEqualTo(WED);
    }

    @Test
    void fetchDateFromS3_keyFound_usesFirstKey() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn("test-bucket");
        when(s3Repo.listKeys(eq("test-bucket"), anyString()))
                .thenReturn(List.of(
                        "tournaments/" + WED + "/key1.csv",
                        "tournaments/" + WED + "/key2.csv"));
        when(s3Repo.downloadAsString(eq("test-bucket"), eq("tournaments/" + WED + "/key1.csv")))
                .thenReturn(VALID_CSV);
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        service.fetchDateFromS3(WED);

        verify(s3Repo).downloadAsString(eq("test-bucket"), eq("tournaments/" + WED + "/key1.csv"));
    }

    // ── syncFromS3 ───────────────────────────────────────────────────────────────

    @Test
    void syncFromS3_s3NotAvailable_returnsEmptyList() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(null);

        List<TournamentImportService.ImportResult> results = service.syncFromS3();

        assertThat(results).isEmpty();
    }

    @Test
    void syncFromS3_malformedKeyTooFewParts_skipped() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn("test-bucket");
        when(s3Repo.listKeys("test-bucket")).thenReturn(List.of("bad-key.csv"));

        List<TournamentImportService.ImportResult> results = service.syncFromS3();

        assertThat(results).isEmpty();
        verify(tournamentRepo, never()).getAllConditions();
    }

    @Test
    void syncFromS3_twoValidKeys_returnsResultsForEach() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn("test-bucket");
        when(s3Repo.listKeys("test-bucket")).thenReturn(List.of(
                "tournaments/" + WED + "/hash1.csv",
                "tournaments/" + SAT + "/hash2.csv"
        ));
        when(s3Repo.downloadAsString(eq("test-bucket"), anyString())).thenReturn(VALID_CSV);
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        List<TournamentImportService.ImportResult> results = service.syncFromS3();

        assertThat(results).hasSize(2);
        assertThat(results.get(0).date()).isEqualTo(WED);
        assertThat(results.get(1).date()).isEqualTo(SAT);
    }

    @Test
    void syncFromS3_mixedValidAndInvalidKeys_onlyValidProcessed() throws IOException {
        when(s3Provider.getIfAvailable()).thenReturn(s3Repo);
        when(aws.getS3Bucket()).thenReturn("test-bucket");
        when(s3Repo.listKeys("test-bucket")).thenReturn(List.of(
                "bad-key.csv",
                "tournaments/" + WED + "/hash1.csv"
        ));
        when(s3Repo.downloadAsString(eq("test-bucket"), anyString())).thenReturn(VALID_CSV);
        when(tournamentRepo.getAllConditions()).thenReturn(CONDITIONS);

        List<TournamentImportService.ImportResult> results = service.syncFromS3();

        assertThat(results).hasSize(1);
    }

    // ── ImportResult.summary() ───────────────────────────────────────────────────

    @Test
    void summary_emptyConditionsMap_returnsNoConditionsMessage() {
        var result = new TournamentImportService.ImportResult(WED, Map.of(), List.of());

        assertThat(result.summary()).contains("No conditions imported").contains(WED);
    }

    @Test
    void summary_nonEmptyMap_containsDateAndLeagueAndCount() {
        var result = new TournamentImportService.ImportResult(WED,
                Map.of("LEGENDS", 13, "CHAMPION", 8), List.of());

        String summary = result.summary();
        assertThat(summary).contains("Imported").contains(WED).contains("LEGENDS").contains("13");
    }

    @Test
    void summary_nonEmptyMap_doesNotEndWithComma() {
        var result = new TournamentImportService.ImportResult(WED,
                Map.of("LEGENDS", 5), List.of());

        assertThat(result.summary()).doesNotEndWith(",").doesNotEndWith(", ");
    }
}
