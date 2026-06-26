package com.pphi.tower.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ScaleSuffixTest {

    @ParameterizedTest(name = "fromSuffix({0}) == {1}")
    @CsvSource({
            "K, THOUSAND",
            "M, MILLION",
            "B, BILLION",
            "T, TRILLION",
            "q, QUADRILLION",
            "Q, QUINTILLION",
            "s, SEXTILLION",
            "S, SEPTILLION",
            "O, OCTILLION",
            "N, NONILLION",
            "d, DECILLION"
    })
    void fromSuffix_allValues(String suffix, ScaleSuffix expected) {
        assertThat(ScaleSuffix.fromSuffix(suffix)).isEqualTo(expected);
    }

    @Test
    void fromSuffix_unknown_returnsNull() {
        assertThat(ScaleSuffix.fromSuffix("X")).isNull();
    }

    @Test
    void fromSuffix_empty_returnsNull() {
        assertThat(ScaleSuffix.fromSuffix("")).isNull();
    }

    @Test
    void fromSuffix_caseSensitive_uppercase_k_returnsNull() {
        // "k" is not a valid suffix — only "K" is
        assertThat(ScaleSuffix.fromSuffix("k")).isNull();
    }

    @Test
    void fromSuffix_caseSensitive_lowercase_q_isQuadrillion() {
        assertThat(ScaleSuffix.fromSuffix("q")).isEqualTo(ScaleSuffix.QUADRILLION);
    }

    @Test
    void fromSuffix_caseSensitive_uppercase_q_isQuintillion() {
        assertThat(ScaleSuffix.fromSuffix("Q")).isEqualTo(ScaleSuffix.QUINTILLION);
    }

    @Test
    void allSuffixes_haveDistinctSuffixString() {
        long distinct = java.util.Arrays.stream(ScaleSuffix.values())
                .map(ScaleSuffix::getSuffix)
                .distinct()
                .count();
        assertThat(distinct).isEqualTo(ScaleSuffix.values().length);
    }

    @Test
    void allSuffixes_scientificNotationsAscending() {
        ScaleSuffix[] values = ScaleSuffix.values();
        for (int i = 1; i < values.length; i++) {
            assertThat(values[i].getScientificNotation())
                    .isGreaterThan(values[i - 1].getScientificNotation());
        }
    }
}