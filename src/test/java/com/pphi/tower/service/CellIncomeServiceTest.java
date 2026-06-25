package com.pphi.tower.service;

import com.pphi.tower.config.AppConfig;
import com.pphi.tower.repository.RunRepository;
import com.pphi.tower.web.dto.CellIncomeDto;
import com.pphi.tower.web.dto.ReportSummaryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CellIncomeServiceTest {

    @Mock
    private RunRepository repository;

    private AppConfig config;
    private CellIncomeService service;

    @BeforeEach
    void setUp() {
        config = new AppConfig();
        service = new CellIncomeService(repository, config);
    }

    // ── clampDays ────────────────────────────────────────────────────────────

    @Test
    void getCellIncome_clamps_belowMin_to3() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        CellIncomeDto dto = service.getCellIncome(1);
        assertThat(dto.windowDays()).isEqualTo(3);
    }

    @Test
    void getCellIncome_clamps_aboveMax_to90() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        CellIncomeDto dto = service.getCellIncome(100);
        assertThat(dto.windowDays()).isEqualTo(90);
    }

    @Test
    void getCellIncome_inRange_unchanged() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        CellIncomeDto dto = service.getCellIncome(30);
        assertThat(dto.windowDays()).isEqualTo(30);
    }

    // ── empty run list ───────────────────────────────────────────────────────

    @Test
    void getCellIncome_emptyList_returnsZeros() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        CellIncomeDto dto = service.getCellIncome(30);
        assertThat(dto.runsAnalyzed()).isZero();
        assertThat(dto.totalCellsEarned()).isZero();
        assertThat(dto.averageCellsPerHour()).isZero();
        assertThat(dto.averageCellsPerRun()).isZero();
    }

    // ── 3-run aggregation ─────────────────────────────────────────────────────

    @Test
    void getCellIncome_threeRuns_sumAndAvgCorrect() {
        var runs = List.of(
                run("1", 1000.0, 500.0),
                run("2", 2000.0, 1000.0),
                run("3", 3000.0, 1500.0)
        );
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(runs);
        CellIncomeDto dto = service.getCellIncome(30);

        assertThat(dto.runsAnalyzed()).isEqualTo(3);
        assertThat(dto.totalCellsEarned()).isEqualTo(6000.0);
        assertThat(dto.averageCellsPerRun()).isEqualTo(2000.0);
        assertThat(dto.averageCellsPerHour()).isEqualTo(1000.0);
    }

    @Test
    void getCellIncome_dataPoints_matchRunInput() {
        var runs = List.of(run("abc", 500.0, 250.0));
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(runs);
        CellIncomeDto dto = service.getCellIncome(30);

        assertThat(dto.dataPoints()).hasSize(1);
        assertThat(dto.dataPoints().get(0).id()).isEqualTo("abc");
        assertThat(dto.dataPoints().get(0).cellsEarned()).isEqualTo(500.0);
    }

    // ── getLabSpeedAffordability ─────────────────────────────────────────────

    @Test
    void getLabSpeed_emptyRuns_returnsZeroEffectiveCph() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        var dto = service.getLabSpeedAffordability(30, 0, 0);
        assertThat(dto.effectiveCellsPerHour()).isZero();
        assertThat(dto.runsAnalyzed()).isZero();
    }

    @Test
    void getLabSpeed_fiveSlots_returned() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        var dto = service.getLabSpeedAffordability(30, 0, 0);
        assertThat(dto.slots()).hasSize(5);
    }

    @Test
    void getLabSpeed_eightSpeedOptions_perSlot() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        var dto = service.getLabSpeedAffordability(30, 0, 0);
        dto.slots().forEach(slot -> assertThat(slot.options()).hasSize(8));
    }

    @Test
    void getLabSpeed_highCph_allSlotsAffordable() {
        // effective CPH = 1_000_000 / 1h = 1_000_000/h  → all 8 speeds fit
        var runs = List.of(runWithEpoch("r1", 1_000_000.0, 1_000_000.0, 3600L, epochNow()));
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(runs);
        var dto = service.getLabSpeedAffordability(30, 0, 0);
        assertThat(dto.slots().get(0).maxAffordableSpeed()).isEqualTo("x8");
    }

    @Test
    void getLabSpeed_zeroCph_noSlotsAffordable() {
        var runs = List.of(runWithEpoch("r1", 0.0, 0.0, 3600L, epochNow()));
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(runs);
        var dto = service.getLabSpeedAffordability(30, 0, 0);
        dto.slots().forEach(slot -> assertThat(slot.maxAffordableSpeed()).isEqualTo("None"));
    }

    @Test
    void getLabSpeed_cellReserve_spendableIsHandMinusBuffer() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        var dto = service.getLabSpeedAffordability(30, 5000.0, 1000.0);
        assertThat(dto.cellReserve().spendableCells()).isEqualTo(4000.0);
    }

    @Test
    void getLabSpeed_deadTimeStats_hasNonNullFields() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        var dto = service.getLabSpeedAffordability(30, 0, 0);
        assertThat(dto.deadTimeStats()).isNotNull();
    }

    @Test
    void getLabSpeed_epochFallback_usedWhenEpochIsZero() {
        // run with battleEpochSeconds=0 triggers the LocalDate.atStartOfDay fallback
        var runs = List.of(run("r1", 100.0, 100.0));
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(runs);
        // Should not throw; dead-time calculation falls back to start-of-day
        var dto = service.getLabSpeedAffordability(30, 0, 0);
        assertThat(dto).isNotNull();
    }

    @Test
    void getLabSpeed_sustainableCombo_fiveSlots() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        var dto = service.getLabSpeedAffordability(30, 0, 0);
        assertThat(dto.optimalCombination().slots()).hasSize(5);
    }

    @Test
    void getLabSpeed_farmingCombo_fiveSlots() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        var dto = service.getLabSpeedAffordability(30, 0, 0);
        assertThat(dto.farmingCombination().slots()).hasSize(5);
    }

    @Test
    void getLabSpeed_windowDays_clamped() {
        when(repository.findFarmingAndEventByDateWindow(any(), any())).thenReturn(List.of());
        var dto = service.getLabSpeedAffordability(1, 0, 0);
        assertThat(dto.windowDays()).isEqualTo(3);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private long epochNow() {
        return java.time.Instant.now().getEpochSecond();
    }

    private ReportSummaryDto run(String id, double cellsEarned, double cellsPerHour) {
        return new ReportSummaryDto(
                id, 1, id + ".txt", "farming", null,
                LocalDate.now().toString(),
                10, 3000, null, "Natural",
                cellsEarned, cellsPerHour, 0.0,
                3600L, 3600L, 0L);
    }

    private ReportSummaryDto runWithEpoch(String id, double cellsEarned, double cellsPerHour,
                                          long realTimeSec, long epochSec) {
        return new ReportSummaryDto(
                id, 1, id + ".txt", "farming", null,
                LocalDate.now().toString(),
                10, 3000, null, "Natural",
                cellsEarned, cellsPerHour, 0.0,
                realTimeSec, realTimeSec, epochSec);
    }
}