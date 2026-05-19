package com.pphi.tower.web.dto;

import java.util.List;

public record CellIncomeDto(
        int windowDays,
        int runsAnalyzed,
        double averageCellsPerHour,
        double totalCellsEarned,
        double averageCellsPerRun,
        List<RunDataPointDto> dataPoints) {

    public record RunDataPointDto(
            String id,
            String battleDate,
            String folder,
            int tier,
            int wave,
            double cellsEarned,
            double cellsPerHour,
            long realTimeSeconds) {}
}
