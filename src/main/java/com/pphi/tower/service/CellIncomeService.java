package com.pphi.tower.service;

import com.pphi.tower.config.AppConfig;
import com.pphi.tower.repository.RunRepository;
import com.pphi.tower.web.dto.CellIncomeDto;
import com.pphi.tower.web.dto.CellIncomeDto.RunDataPointDto;
import com.pphi.tower.web.dto.LabSpeedDto;
import com.pphi.tower.web.dto.LabSpeedDto.*;
import com.pphi.tower.web.dto.ReportSummaryDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class CellIncomeService {

    // Lab speed costs in cells/hour, index 0 = x1.5 ... index 7 = x8
    private static final String[] SPEED_LABELS = {"x1.5","x2","x3","x4","x5","x6","x7","x8"};
    private static final double[] SPEED_COSTS  = {15, 100, 840, 3_360, 11_900, 60_000, 250_000, 1_000_000};
    private static final int LAB_SLOTS = 5;
    private static final ZoneId LA_ZONE = ZoneId.of("America/Los_Angeles");

    private final RunRepository repository;
    private final AppConfig config;

    public CellIncomeService(RunRepository repository, AppConfig config) {
        this.repository = repository;
        this.config = config;
    }

    public CellIncomeDto getCellIncome(int requestedDays) {
        int days = clampDays(requestedDays);
        List<ReportSummaryDto> runs = getRunsInWindow(days);

        double totalCells = runs.stream().mapToDouble(ReportSummaryDto::cellsEarned).sum();
        double avgCph     = runs.stream().mapToDouble(ReportSummaryDto::cellsPerHour).average().orElse(0.0);
        double avgPerRun  = runs.isEmpty() ? 0.0 : totalCells / runs.size();

        List<RunDataPointDto> points = runs.stream()
                .map(r -> new RunDataPointDto(
                        r.id(), r.battleDate(), r.folder(),
                        r.tier(), r.wave(),
                        r.cellsEarned(), r.cellsPerHour(), r.realTimeSeconds()))
                .toList();

        return new CellIncomeDto(days, runs.size(), avgCph, totalCells, avgPerRun, points);
    }

    public LabSpeedDto getLabSpeedAffordability(int requestedDays) {
        int days = clampDays(requestedDays);
        List<ReportSummaryDto> runs = getRunsInWindow(days);

        // Farming CPH: straight average of per-run cellsPerHour (what you earn while actively playing)
        double farmingCph = runs.stream().mapToDouble(ReportSummaryDto::cellsPerHour).average().orElse(0.0);

        // Dead time: gaps between battles and the gap since the last battle ended
        Instant now = Instant.now();
        long totalActiveSeconds = runs.stream().mapToLong(ReportSummaryDto::realTimeSeconds).sum();
        long totalDeadSeconds = 0;
        double hoursSinceLastRun = 0;

        if (!runs.isEmpty()) {
            for (int i = 1; i < runs.size(); i++) {
                long prevEnd  = epochOf(runs.get(i - 1));
                long currStart = epochOf(runs.get(i)) - runs.get(i).realTimeSeconds();
                long gap = currStart - prevEnd;
                if (gap > 0) totalDeadSeconds += gap;
            }
            long postGap = now.getEpochSecond() - epochOf(runs.get(runs.size() - 1));
            if (postGap > 0) {
                totalDeadSeconds += postGap;
                hoursSinceLastRun = postGap / 3600.0;
            }
        }

        long totalCalendarSeconds = totalActiveSeconds + totalDeadSeconds;
        double totalCellsEarned = runs.stream().mapToDouble(ReportSummaryDto::cellsEarned).sum();
        double effectiveCph = totalCalendarSeconds > 0
                ? totalCellsEarned / (totalCalendarSeconds / 3600.0)
                : 0.0;

        DeadTimeStatsDto deadStats = new DeadTimeStatsDto(
                totalActiveSeconds / 3600.0,
                totalDeadSeconds / 3600.0,
                totalCalendarSeconds / 3600.0,
                totalCalendarSeconds > 0 ? (totalDeadSeconds * 100.0 / totalCalendarSeconds) : 0.0,
                hoursSinceLastRun);

        // Build per-slot affordability using effectiveCph as the budget
        List<SlotAffordabilityDto> slots = new ArrayList<>();
        for (int slot = 1; slot <= LAB_SLOTS; slot++) {
            List<SpeedOptionDto> options = buildSpeedOptions(effectiveCph);
            String maxAffordable = options.stream()
                    .filter(SpeedOptionDto::affordable)
                    .map(SpeedOptionDto::speed)
                    .reduce((a, b) -> b)
                    .orElse("None");
            slots.add(new SlotAffordabilityDto(slot, options, maxAffordable));
        }

        // Greedy optimal: allocate from a shared effectiveCph budget across all slots
        List<String> optimal = new ArrayList<>();
        double remainingBudget = effectiveCph;
        double totalCostPerHour = 0;
        for (int i = 0; i < LAB_SLOTS; i++) {
            String bestSpeed = "None";
            double bestCost = 0;
            for (int j = SPEED_LABELS.length - 1; j >= 0; j--) {
                if (SPEED_COSTS[j] <= remainingBudget) {
                    bestSpeed = SPEED_LABELS[j];
                    bestCost = SPEED_COSTS[j];
                    break;
                }
            }
            optimal.add(bestSpeed);
            remainingBudget -= bestCost;
            totalCostPerHour += bestCost;
        }
        double netCph = effectiveCph - totalCostPerHour;

        OptimalCombinationDto combo = new OptimalCombinationDto(
                optimal, totalCostPerHour, totalCostPerHour * 24, netCph, netCph >= 0);

        return new LabSpeedDto(days, runs.size(), farmingCph, effectiveCph, deadStats, slots, combo);
    }

    /**
     * Returns the epoch-second when a run ended.
     * Uses the stored value if available; falls back to start-of-day for old records
     * that pre-date the battle_epoch_seconds column.
     */
    private long epochOf(ReportSummaryDto run) {
        if (run.battleEpochSeconds() > 0) return run.battleEpochSeconds();
        return LocalDate.parse(run.battleDate()).atStartOfDay(LA_ZONE).toEpochSecond();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private List<ReportSummaryDto> getRunsInWindow(int days) {
        LocalDate to   = LocalDate.now();
        LocalDate from = to.minusDays(days);
        return repository.findByDateWindow(from, to);
    }

    private List<SpeedOptionDto> buildSpeedOptions(double avgCph) {
        List<SpeedOptionDto> options = new ArrayList<>();
        for (int i = 0; i < SPEED_LABELS.length; i++) {
            double cost    = SPEED_COSTS[i];
            double netCph  = avgCph - cost;
            options.add(new SpeedOptionDto(
                    SPEED_LABELS[i],
                    cost,
                    cost * 24,
                    netCph,
                    netCph >= 0));
        }
        return options;
    }

    private int clampDays(int requested) {
        int min = config.getCells().getWindowDaysMin();
        int max = config.getCells().getWindowDaysMax();
        return Math.max(min, Math.min(max, requested));
    }
}
