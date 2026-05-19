package com.pphi.tower.web.dto;

/**
 * Lightweight summary of a single run for the report list endpoint.
 * Keeps the list fast — full payload fetched separately by ID.
 */
public record ReportSummaryDto(
        String id,
        String filename,
        String folder,
        String battleDate,
        int tier,
        int wave,
        String towerEra,
        String killedBy,
        double cellsEarned,
        double cellsPerHour,
        double coinsPerHour,
        long realTimeSeconds,
        long gameTimeSeconds) {}
