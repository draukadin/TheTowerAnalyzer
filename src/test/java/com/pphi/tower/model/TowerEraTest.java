package com.pphi.tower.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TowerEraTest {

    // ── parse ────────────────────────────────────────────────────────────────

    @ParameterizedTest
    @CsvSource({
            "1.0.0, 1, 0, 0",
            "2.3.4, 2, 3, 4",
            "10.20.30, 10, 20, 30"
    })
    void parse_validString_extractsComponents(String input, int major, int minor, int patch) {
        TowerEra era = TowerEra.parse(input);
        assertThat(era.major()).isEqualTo(major);
        assertThat(era.minor()).isEqualTo(minor);
        assertThat(era.patch()).isEqualTo(patch);
    }

    @Test
    void parse_null_returnsDefault() {
        TowerEra era = TowerEra.parse(null);
        assertThat(era).isEqualTo(new TowerEra(1, 0, 0));
    }

    @Test
    void parse_blank_returnsDefault() {
        TowerEra era = TowerEra.parse("  ");
        assertThat(era).isEqualTo(new TowerEra(1, 0, 0));
    }

    @Test
    void parse_noMatch_returnsDefault() {
        TowerEra era = TowerEra.parse("not-a-version");
        assertThat(era).isEqualTo(new TowerEra(1, 0, 0));
    }

    @Test
    void parse_embeddedVersion_extractsFirst() {
        TowerEra era = TowerEra.parse("Version 3.14.15 release");
        assertThat(era.major()).isEqualTo(3);
        assertThat(era.minor()).isEqualTo(14);
        assertThat(era.patch()).isEqualTo(15);
    }

    // ── toString / JsonValue ─────────────────────────────────────────────────

    @Test
    void toString_formatsAsMajorDotMinorDotPatch() {
        assertThat(new TowerEra(2, 3, 4).toString()).isEqualTo("2.3.4");
    }

    @Test
    void toString_roundTrip_viaParseAndToString() {
        TowerEra era = TowerEra.parse("5.1.99");
        assertThat(era.toString()).isEqualTo("5.1.99");
    }

    // ── compareTo ────────────────────────────────────────────────────────────

    @Test
    void compareTo_equal_returnsZero() {
        TowerEra a = new TowerEra(2, 3, 4);
        TowerEra b = new TowerEra(2, 3, 4);
        assertThat(a.compareTo(b)).isZero();
    }

    @Test
    void compareTo_majorDiffers_majorDominates() {
        TowerEra lower = new TowerEra(1, 99, 99);
        TowerEra higher = new TowerEra(2, 0, 0);
        assertThat(lower.compareTo(higher)).isNegative();
        assertThat(higher.compareTo(lower)).isPositive();
    }

    @Test
    void compareTo_sameMajor_minorDiffers() {
        TowerEra lower = new TowerEra(2, 1, 99);
        TowerEra higher = new TowerEra(2, 2, 0);
        assertThat(lower.compareTo(higher)).isNegative();
    }

    @Test
    void compareTo_sameMajorMinor_patchDiffers() {
        TowerEra lower = new TowerEra(2, 3, 4);
        TowerEra higher = new TowerEra(2, 3, 5);
        assertThat(lower.compareTo(higher)).isNegative();
        assertThat(higher.compareTo(lower)).isPositive();
    }
}