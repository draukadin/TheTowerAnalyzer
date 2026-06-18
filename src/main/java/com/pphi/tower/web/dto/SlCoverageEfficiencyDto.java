package com.pphi.tower.web.dto;

public record SlCoverageEfficiencyDto(
        int angleLevel,
        int quantityLevel,
        double angleDegrees,
        int quantityBeams,
        double effectiveCoverage,
        Double angleNextCoverageGain,
        Integer angleNextStoneCost,
        Double angleCoveragePerStone,
        Double quantityNextCoverageGain,
        Integer quantityNextStoneCost,
        Double quantityCoveragePerStone,
        String recommendation
) {}
