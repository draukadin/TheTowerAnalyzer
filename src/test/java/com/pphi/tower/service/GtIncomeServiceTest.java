package com.pphi.tower.service;

import com.pphi.tower.web.dto.GtIncomeProjectionDto;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GtIncomeServiceTest {

    private final GtIncomeService service = new GtIncomeService();

    private static final int    GT_PLUS_LEVEL    = 0;
    private static final double GT_DURATION_SEC  = 30.0;
    private static final double GT_COOLDOWN_SEC  = 120.0;
    private static final double KPS              = 10.0;
    private static final double RUN_DURATION_SEC = 7200.0;
    private static final double INCOME_PER_MOB   = 100.0;

    @Test
    void projection_projectedIncome_isPositive() {
        assertThat(project().projectedIncome()).isGreaterThan(0.0);
    }

    @Test
    void projection_gtPlusLevel0_matchesFormula() {
        double r        = 0.0003 * (GT_PLUS_LEVEL + 1);
        double K        = KPS * GT_DURATION_SEC;
        double T        = RUN_DURATION_SEC / GT_COOLDOWN_SEC;
        double expected = T * KPS * GT_DURATION_SEC * INCOME_PER_MOB * Math.pow(1 + r, K);
        assertThat(project().projectedIncome())
                .isCloseTo(expected, org.assertj.core.data.Offset.offset(1.0));
    }

    @Test
    void projection_higherGtPlusLevel_producesHigherIncome() {
        double fi0  = project(0).projectedIncome();
        double fi5  = project(5).projectedIncome();
        double fi10 = project(10).projectedIncome();
        assertThat(fi5).isGreaterThan(fi0);
        assertThat(fi10).isGreaterThan(fi5);
    }

    @Test
    void comparisonTable_hasBetween9And10Entries() {
        // 9 fixed milestones; current duration adds 1 more unless it's already in the set
        assertThat(project().comparisonTable()).hasSizeBetween(9, 10);
    }

    @Test
    void comparisonTable_allDurationsUnique() {
        var table = project().comparisonTable();
        Set<Double> durations = new HashSet<>();
        table.forEach(m -> durations.add(m.durationSec()));
        assertThat(durations).hasSameSizeAs(table);
    }

    @Test
    void comparisonTable_exactlyOneCurrentEntry() {
        long currentCount = project().comparisonTable().stream()
                .filter(GtIncomeProjectionDto.DurationMilestone::isCurrent)
                .count();
        assertThat(currentCount).isEqualTo(1);
    }

    @Test
    void comparisonTable_currentEntry_matchesInputDuration() {
        double current = project().comparisonTable().stream()
                .filter(GtIncomeProjectionDto.DurationMilestone::isCurrent)
                .mapToDouble(GtIncomeProjectionDto.DurationMilestone::durationSec)
                .findFirst().orElseThrow();
        assertThat(current).isEqualTo(GT_DURATION_SEC);
    }

    @Test
    void comparisonTable_currentEntry_hasZeroGainVsCurrent() {
        double gain = project().comparisonTable().stream()
                .filter(GtIncomeProjectionDto.DurationMilestone::isCurrent)
                .mapToDouble(GtIncomeProjectionDto.DurationMilestone::incomeGainVsCurrent)
                .findFirst().orElseThrow();
        assertThat(gain).isCloseTo(0.0, org.assertj.core.data.Offset.offset(0.01));
    }

    @Test
    void marginalDurationValue_isPositive() {
        assertThat(project().marginalDurationValue()).isGreaterThan(0.0);
    }

    @Test
    void activationsPerRun_matchesFormula() {
        double expected = RUN_DURATION_SEC / GT_COOLDOWN_SEC;
        assertThat(project().activationsPerRun())
                .isCloseTo(expected, org.assertj.core.data.Offset.offset(0.001));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private GtIncomeProjectionDto project() {
        return project(GT_PLUS_LEVEL);
    }

    private GtIncomeProjectionDto project(int level) {
        return service.project(level, GT_DURATION_SEC, GT_COOLDOWN_SEC,
                KPS, RUN_DURATION_SEC, INCOME_PER_MOB);
    }
}