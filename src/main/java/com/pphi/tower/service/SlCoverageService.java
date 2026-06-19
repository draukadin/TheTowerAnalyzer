package com.pphi.tower.service;

import com.pphi.tower.web.dto.SlCoverageEfficiencyDto;
import org.springframework.stereotype.Service;

@Service
public class SlCoverageService {

    public SlCoverageEfficiencyDto compute(
            int angleLevel,
            int quantityLevel,
            double angleDegrees,
            int quantityBeams,
            Integer angleNextStoneCost,
            Integer quantityNextStoneCost) {

        double effectiveCoverage = angleDegrees * quantityBeams;

        Double angleGain = null;
        Double angleCps  = null;
        if (angleNextStoneCost != null && angleNextStoneCost > 0) {
            angleGain = (double) quantityBeams; // +1° × beams
            angleCps  = angleGain / angleNextStoneCost;
        }

        Double quantityGain = null;
        Double quantityCps  = null;
        if (quantityNextStoneCost != null && quantityNextStoneCost > 0) {
            quantityGain = angleDegrees; // +1 beam × current degrees
            quantityCps  = quantityGain / quantityNextStoneCost;
        }

        String recommendation = buildRecommendation(angleCps, quantityCps, angleNextStoneCost, quantityNextStoneCost);

        return new SlCoverageEfficiencyDto(
                angleLevel, quantityLevel,
                angleDegrees, quantityBeams,
                effectiveCoverage,
                angleGain, angleNextStoneCost, angleCps,
                quantityGain, quantityNextStoneCost, quantityCps,
                recommendation);
    }

    private String buildRecommendation(Double angleCps, Double quantityCps,
                                       Integer angleCost, Integer quantityCost) {
        if (angleCps == null && quantityCps == null) return "Both Angle and Quantity are maxed.";
        if (angleCps == null) return "Angle is maxed — invest in Quantity.";
        if (quantityCps == null) return "Quantity is maxed — invest in Angle.";
        if (angleCps > quantityCps) {
            double ratio = angleCps / quantityCps;
            return String.format("Angle is more efficient (%.2f× better coverage-per-stone): costs %d stones vs %d for Quantity. Prefer Angle.", ratio, angleCost, quantityCost);
        } else if (quantityCps > angleCps) {
            double ratio = quantityCps / angleCps;
            return String.format("Quantity is more efficient (%.2f× better coverage-per-stone): costs %d stones vs %d for Angle. Prefer Quantity.", ratio, quantityCost, angleCost);
        }
        return String.format("Angle and Quantity have equal coverage-per-stone (%d stones each).", angleCost);
    }
}
