package com.pphi.tower.web.dto;

import com.pphi.tower.model.TowerEra;

/**
 * Lightweight summary of a single run for the report list endpoint.
 * Keeps the list fast — full payload fetched separately by ID.
 */
public record ReportSummaryDto(
        String id,
        int runNumber,
        String filename,
        String runType,
        String battleDate,
        int tier,
        int wave,
        TowerEra towerEra,
        String killedBy,
        double cellsEarned,
        double cellsPerHour,
        double coinsPerHour,
        long realTimeSeconds,
        long gameTimeSeconds,
        long battleEpochSeconds) {}
