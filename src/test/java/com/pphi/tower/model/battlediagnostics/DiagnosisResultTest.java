package com.pphi.tower.model.battlediagnostics;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DiagnosisResultTest {

    private DiagnosisResult result(double swarm, double heavy, double lifesteal,
                                   double totalDmg, double block,
                                   double vampire, double ranged,
                                   Confidence confidence) {
        return new DiagnosisResult(
                FailureType.UNKNOWN_DUE_TO_VARIANCE, confidence,
                "test explanation",
                swarm, heavy, lifesteal, totalDmg, block, vampire, ranged,
                List.of());
    }

    // ── isHighConfidence ─────────────────────────────────────────────────────

    @Test
    void isHighConfidence_whenHigh_returnsTrue() {
        DiagnosisResult r = result(0, 0, 0, 0, 0, 0, 0, Confidence.HIGH);
        assertThat(r.isHighConfidence()).isTrue();
    }

    @Test
    void isHighConfidence_whenMedium_returnsFalse() {
        DiagnosisResult r = result(0, 0, 0, 0, 0, 0, 0, Confidence.MEDIUM);
        assertThat(r.isHighConfidence()).isFalse();
    }

    // ── formatted percentage methods ─────────────────────────────────────────

    @Test
    void swarmKillShareFormatted_0p5_displays50percent() {
        DiagnosisResult r = result(0.5, 0, 0, 0, 0, 0, 0, Confidence.HIGH);
        assertThat(r.swarmKillShareFormatted()).isEqualTo("50.00 %");
    }

    @Test
    void heavyKillShareFormatted_0p25_displays25percent() {
        DiagnosisResult r = result(0, 0.25, 0, 0, 0, 0, 0, Confidence.HIGH);
        assertThat(r.heavyKillShareFormatted()).isEqualTo("25.00 %");
    }

    @Test
    void blockEfficiencyFormatted_0p8_displays80percent() {
        DiagnosisResult r = result(0, 0, 0, 0, 0.8, 0, 0, Confidence.HIGH);
        assertThat(r.blockEfficiencyFormatted()).isEqualTo("80.00 %");
    }

    @Test
    void vampireDensityFormatted_0_displays0percent() {
        DiagnosisResult r = result(0, 0, 0, 0, 0, 0.0, 0, Confidence.HIGH);
        assertThat(r.vampireDensityFormatted()).isEqualTo("0.00 %");
    }

    @Test
    void rangedDensityFormatted_0p123_displaysCorrect() {
        DiagnosisResult r = result(0, 0, 0, 0, 0, 0, 0.123, Confidence.HIGH);
        assertThat(r.rangedDensityFormatted()).isEqualTo("12.30 %");
    }

    // ── record equality ──────────────────────────────────────────────────────

    @Test
    void equals_sameInstance_returnsTrue() {
        DiagnosisResult r = result(0.1, 0.2, 100.0, 200.0, 0.8, 0.05, 0.1, Confidence.HIGH);
        assertThat(r).isEqualTo(r);
    }

    @Test
    void equals_differentType_returnsFalse() {
        DiagnosisResult r = result(0, 0, 0, 0, 0, 0, 0, Confidence.HIGH);
        assertThat(r).isNotEqualTo("not a DiagnosisResult");
    }

    @Test
    void equals_sameValues_returnsTrue() {
        DiagnosisResult r1 = result(0.5, 0.1, 10.0, 20.0, 0.8, 0.02, 0.03, Confidence.HIGH);
        DiagnosisResult r2 = result(0.5, 0.1, 10.0, 20.0, 0.8, 0.02, 0.03, Confidence.HIGH);
        assertThat(r1).isEqualTo(r2);
    }
}