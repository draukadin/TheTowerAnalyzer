package com.pphi.tower.web.dto;

import java.util.List;

public record ShardRateDto(
        int windowDays,
        int runsAnalyzed,
        int blendedRunsAnalyzed,
        ShardAverages averages,
        ShardAverages blendedAverages,
        ShardStdDev stdDev,
        ShardStdDev blendedStdDev,
        List<RunDataPointDto> dataPoints,
        ShardProjections projections,
        ShardProjections blendedProjections) {

    public record ShardAverages(
            double cannon, double armor, double generator, double core) {}

    public record ShardStdDev(
            double cannon, double armor, double generator, double core) {}

    /**
     * Level-aware projection for one shard type.
     * Shard costs are post-discount (base × (1 − discountPct/100)).
     * All "hours" fields are -1 when rate is 0 or target already reached.
     */
    public record ShardProjection(
            int currentLevel,
            int targetLevel,
            int discountLevel,
            double discountPct,
            long shardsToNextLevelBase,
            long shardsToNextLevelEffective,
            double hoursToNextLevel,
            long shardsToTargetLevelBase,
            long shardsToTargetLevelEffective,
            double hoursToTargetLevel) {}

    public record ShardProjections(
            ShardProjection cannon,
            ShardProjection armor,
            ShardProjection generator,
            ShardProjection core) {}

    public record RunDataPointDto(
            String id,
            String battleDate,
            int tier,
            int wave,
            String towerEra,
            long realTimeSeconds,
            long cannonShards,
            long armorShards,
            long generatorShards,
            long coreShards,
            double cannonPerHour,
            double armorPerHour,
            double generatorPerHour,
            double corePerHour) {}
}
