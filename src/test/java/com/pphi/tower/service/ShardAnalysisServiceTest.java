package com.pphi.tower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pphi.tower.fixtures.BattleHistoryFixtures;
import com.pphi.tower.jackson.BattleHistoryDeserializer;
import com.pphi.tower.model.ModuleLevelTable;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.repository.CurrencySnapshotRepository;
import com.pphi.tower.repository.CurrencySnapshotRepository.SnapshotPair;
import com.pphi.tower.repository.RunRepository;
import com.pphi.tower.web.dto.ShardRateDto;
import com.pphi.tower.web.dto.ShardRateDto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShardAnalysisServiceTest {

    @Mock private RunRepository runRepository;
    @Mock private CurrencySnapshotRepository snapshotRepository;

    private ShardAnalysisService service;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        SimpleModule m = new SimpleModule();
        m.addDeserializer(BattleHistory.class, new BattleHistoryDeserializer());
        mapper.registerModule(m);
        service = new ShardAnalysisService(runRepository, snapshotRepository, mapper);
    }

    // ── getShardRates — empty repository ────────────────────────────────────

    @Test
    void getShardRates_emptyRepo_returnsZeroAverages() {
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of());
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of());

        ShardRateDto dto = service.getShardRates(30, 1, 1, 1, 1, 0, 161, 161, 161, 161);

        assertThat(dto.averages().cannon()).isZero();
        assertThat(dto.averages().armor()).isZero();
        assertThat(dto.averages().generator()).isZero();
        assertThat(dto.averages().core()).isZero();
    }

    @Test
    void getShardRates_emptyRepo_runsAnalyzedIsZero() {
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of());
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of());

        ShardRateDto dto = service.getShardRates(30, 1, 1, 1, 1, 0, 161, 161, 161, 161);

        assertThat(dto.runsAnalyzed()).isZero();
    }

    @Test
    void getShardRates_emptyRepo_projectionsHaveNegativeHours() {
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of());
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of());

        ShardRateDto dto = service.getShardRates(30, 1, 1, 1, 1, 0, 161, 161, 161, 161);

        // rate=0 → hoursToNext = -1
        assertThat(dto.projections().cannon().hoursToNextLevel()).isEqualTo(-1.0);
    }

    @Test
    void getShardRates_emptyRepo_windowDaysPreserved() {
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of());
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of());

        ShardRateDto dto = service.getShardRates(14, 1, 1, 1, 1, 0, 161, 161, 161, 161);
        assertThat(dto.windowDays()).isEqualTo(14);
    }

    @Test
    void getShardRates_rowWithZeroRealTime_excluded() {
        var row = shardRow("r1", 0L, 10L, 20L, 30L, 40L,
                serializeHistory());
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of(row));
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of(row));

        ShardRateDto dto = service.getShardRates(30, 1, 1, 1, 1, 0, 161, 161, 161, 161);
        assertThat(dto.runsAnalyzed()).isZero();
    }

    @Test
    void getShardRates_malformedPayload_excluded() {
        var row = shardRow("r1", 3600L, 10L, 20L, 30L, 40L, "not-json");
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of(row));
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of(row));

        ShardRateDto dto = service.getShardRates(30, 1, 1, 1, 1, 0, 161, 161, 161, 161);
        assertThat(dto.runsAnalyzed()).isZero();
    }

    @Test
    void getShardRates_discountClamped_aboveThirty() {
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of());
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of());

        // discount = 50 → clamped to 30 in projection
        ShardRateDto dto = service.getShardRates(30, 1, 1, 1, 1, 50, 161, 161, 161, 161);
        assertThat(dto.projections().cannon().discountLevel()).isEqualTo(30);
    }

    @Test
    void getShardRates_targetLevelAtOrBelowCurrent_clampedAboveCurrent() {
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of());
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of());

        // currentLevel=10, target=5 → target clamped to 11
        ShardRateDto dto = service.getShardRates(30, 10, 10, 10, 10, 0, 5, 5, 5, 5);
        assertThat(dto.projections().cannon().targetLevel()).isEqualTo(11);
    }

    // ── project helper via public API ────────────────────────────────────────

    @Test
    void getShardRates_shardsToNextLevel_matchesTable() {
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of());
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of());

        ShardRateDto dto = service.getShardRates(30, 10, 10, 10, 10, 0, 161, 161, 161, 161);
        long expected = ModuleLevelTable.shardsForLevel(11);
        assertThat(dto.projections().cannon().shardsToNextLevelBase()).isEqualTo(expected);
    }

    @Test
    void getShardRates_currentLevelAboveMax_clamped() {
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of());
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of());

        // level=9999 → clamped to MAX_LEVEL
        ShardRateDto dto = service.getShardRates(30, 9999, 9999, 9999, 9999, 0, 161, 161, 161, 161);
        assertThat(dto.projections().cannon().currentLevel()).isEqualTo(ModuleLevelTable.MAX_LEVEL);
    }

    // ── getSnapshotCount ─────────────────────────────────────────────────────

    @Test
    void getSnapshotCount_delegatesToRepository() {
        when(snapshotRepository.countSnapshots()).thenReturn(42);
        assertThat(service.getSnapshotCount()).isEqualTo(42);
    }

    // ── getSnapshotBasedShardRates — failure path ────────────────────────────

    @Test
    void getSnapshotBasedShardRates_repoThrows_returnsEmptyResult() {
        when(snapshotRepository.findSnapshotPairsInWindow(any(), any()))
                .thenThrow(new RuntimeException("DB down"));

        ShardRateDto dto = service.getSnapshotBasedShardRates(30, 1, 1, 1, 1, 0, 161);
        assertThat(dto.windowDays()).isEqualTo(30);
        assertThat(dto.runsAnalyzed()).isZero();
    }

    @Test
    void getSnapshotBasedShardRates_emptyPairs_returnsZeroAverages() {
        when(snapshotRepository.findSnapshotPairsInWindow(any(), any())).thenReturn(List.of());
        when(snapshotRepository.findModuleLevelsBetween(any(), any())).thenReturn(Map.of());

        ShardRateDto dto = service.getSnapshotBasedShardRates(30, 1, 1, 1, 1, 0, 161);
        assertThat(dto.averages().cannon()).isZero();
    }

    @Test
    void getSnapshotBasedShardRates_validPairWithPositiveHours_producesOneDataPoint() {
        java.time.LocalDateTime earlier = java.time.LocalDateTime.now().minusHours(2);
        java.time.LocalDateTime later   = java.time.LocalDateTime.now();
        var pair = new CurrencySnapshotRepository.SnapshotPair(
                earlier, later,
                100, 200,    // cannon: 100 earlier, 200 later
                50,  100,    // armor
                20,  40,     // generator
                10,  20);    // core
        when(snapshotRepository.findSnapshotPairsInWindow(any(), any())).thenReturn(List.of(pair));
        when(snapshotRepository.findModuleLevelsBetween(any(), any())).thenReturn(Map.of());

        ShardRateDto dto = service.getSnapshotBasedShardRates(30, 1, 1, 1, 1, 0, 161);
        // 100 cannon earned over 2h → 50/h average
        assertThat(dto.averages().cannon()).isGreaterThan(0);
        assertThat(dto.runsAnalyzed()).isOne();
    }

    @Test
    void getSnapshotBasedShardRates_pairWithZeroHours_excluded() {
        java.time.LocalDateTime same = java.time.LocalDateTime.now();
        var pair = new CurrencySnapshotRepository.SnapshotPair(
                same, same, 0, 100, 0, 50, 0, 20, 0, 10);
        when(snapshotRepository.findSnapshotPairsInWindow(any(), any())).thenReturn(List.of(pair));
        when(snapshotRepository.findModuleLevelsBetween(any(), any())).thenReturn(Map.of());

        ShardRateDto dto = service.getSnapshotBasedShardRates(30, 1, 1, 1, 1, 0, 161);
        assertThat(dto.runsAnalyzed()).isZero();
    }

    @Test
    void getShardRates_validPayloadWithCurrencies_runsAnalyzedIsOne() {
        var row = shardRow("r1", 3600L, 0L, 0L, 0L, 0L, serializeHistory());
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of(row));
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of(row));

        ShardRateDto dto = service.getShardRates(30, 1, 1, 1, 1, 0, 161, 161, 161, 161);
        // The fixture has currencies with zero shard counts, so averages are 0
        assertThat(dto.runsAnalyzed()).isOne();
        assertThat(dto.averages().cannon()).isZero();
    }

    @Test
    void getShardRates_twoIdenticalRows_noOutliersFiltered() {
        var row = shardRow("r1", 3600L, 0L, 0L, 0L, 0L, serializeHistory());
        var row2 = shardRow("r2", 3600L, 0L, 0L, 0L, 0L, serializeHistory());
        when(runRepository.findFarmingRunsWithPayload(any(), any())).thenReturn(List.of(row, row2));
        when(runRepository.findAllRunsWithPayload(any(), any())).thenReturn(List.of(row, row2));

        ShardRateDto dto = service.getShardRates(30, 1, 1, 1, 1, 0, 161, 161, 161, 161);
        // stdDev = 0 → both within 2*0, all kept
        assertThat(dto.runsAnalyzed()).isEqualTo(2);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private RunRepository.ShardRunRow shardRow(String id, long realTimeSec,
                                                long cannon, long armor,
                                                long generator, long core,
                                                String payload) {
        return new RunRepository.ShardRunRow(
                id, LocalDate.now().toString(), 10, 3000, null,
                realTimeSec, payload);
    }

    private String serializeHistory() {
        try {
            return mapper.writeValueAsString(BattleHistoryFixtures.unknownVariance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}