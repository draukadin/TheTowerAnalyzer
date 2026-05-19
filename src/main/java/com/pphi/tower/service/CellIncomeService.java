package com.pphi.tower.service;

import com.pphi.tower.config.AppConfig;
import com.pphi.tower.repository.RunRepository;
import com.pphi.tower.web.dto.CellIncomeDto;
import com.pphi.tower.web.dto.CellIncomeDto.RunDataPointDto;
import com.pphi.tower.web.dto.LabSpeedDto;
import com.pphi.tower.web.dto.LabSpeedDto.*;
import com.pphi.tower.web.dto.ReportSummaryDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CellIncomeService {

    // Lab speed costs in cells/hour, index 0 = x1.5 ... index 7 = x8
    private static final String[] SPEED_LABELS = {"x1.5","x2","x3","x4","x5","x6","x7","x8"};
    private static final double[] SPEED_COSTS  = {15, 100, 840, 3_360, 11_900, 60_000, 250_000, 1_000_000};
    private static final int LAB_SLOTS = 5;

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

        double avgCph = runs.stream().mapToDouble(ReportSummaryDto::cellsPerHour).average().orElse(0.0);

        // Build per-slot affordability (slots are independent but share the same cells/hour budget)
        List<SlotAffordabilityDto> slots = new ArrayList<>();
        for (int slot = 1; slot <= LAB_SLOTS; slot++) {
            List<SpeedOptionDto> options = buildSpeedOptions(avgCph);
            String maxAffordable = options.stream()
                    .filter(SpeedOptionDto::affordable)
                    .map(SpeedOptionDto::speed)
                    .reduce((a, b) -> b)  // last affordable = highest
                    .orElse("None");
            slots.add(new SlotAffordabilityDto(slot, options, maxAffordable));
        }

        // Optimal combination: greedy — allocate from a shared budget across all slots.
        // Each slot claims from the remaining budget so total cost never exceeds avgCph.
        List<String> optimal = new ArrayList<>();
        double remainingBudget = avgCph;
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
        double netCph = avgCph - totalCostPerHour;

        OptimalCombinationDto combo = new OptimalCombinationDto(
                optimal,
                totalCostPerHour,
                totalCostPerHour * 24,
                netCph,
                netCph >= 0);

        return new LabSpeedDto(days, runs.size(), avgCph, slots, combo);
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
