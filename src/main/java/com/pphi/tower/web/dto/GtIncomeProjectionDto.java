package com.pphi.tower.web.dto;

import java.util.List;

public record GtIncomeProjectionDto(
        double projectedIncome,
        double permGtIncome,
        double activationsPerRun,
        double killsPerActivation,
        double bonusFraction,
        double marginalDurationValue,
        List<DurationMilestone> comparisonTable
) {
    public record DurationMilestone(
            double durationSec,
            double projectedIncome,
            double incomeGainVsCurrent,
            boolean isCurrent
    ) {}
}
