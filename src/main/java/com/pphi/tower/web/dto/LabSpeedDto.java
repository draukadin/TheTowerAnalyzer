package com.pphi.tower.web.dto;

import java.util.List;

public record LabSpeedDto(
        int windowDays,
        int runsAnalyzed,
        double averageCellsPerHour,
        double effectiveCellsPerHour,
        DeadTimeStatsDto deadTimeStats,
        List<SlotAffordabilityDto> slots,
        OptimalCombinationDto optimalCombination) {

    public record DeadTimeStatsDto(
            double totalActiveHours,
            double totalDeadHours,
            double totalCalendarHours,
            double deadTimePercent,
            double hoursSinceLastRun) {}

    public record SpeedOptionDto(
            String speed,
            double costPerHour,
            double costPerDay,
            double netCellsPerHour,
            boolean affordable) {}

    public record SlotAffordabilityDto(
            int slot,
            List<SpeedOptionDto> options,
            String maxAffordableSpeed) {}

    public record OptimalCombinationDto(
            List<String> slots,
            double totalCostPerHour,
            double totalCostPerDay,
            double netCellsPerHour,
            boolean affordable) {}
}
