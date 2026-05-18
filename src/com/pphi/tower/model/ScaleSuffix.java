package com.pphi.tower.model;

import java.math.BigDecimal;
import java.util.Arrays;

public enum ScaleSuffix {
    THOUSAND("Thousand", new BigDecimal("1000"), "k"),
    MILLION("Million", new BigDecimal("1000000"), "M"),
    BILLION("Billion", new BigDecimal("1000000000"), "B"),
    TRILLION("Trillion", new BigDecimal("1000000000000"), "T"),
    QUADRILLION("Quadrillion", new BigDecimal("1000000000000000"), "q"),
    QUINTILLION("Quintillion", new BigDecimal("1000000000000000000"), "Q"),
    SEXTILLION("Sextillion", new BigDecimal("1000000000000000000000"), "s"),
    SEPTILLION("Septillion", new BigDecimal("1000000000000000000000000"), "S"),
    OCTILLION("Octillion", new BigDecimal("1000000000000000000000000000"), "O"),
    NONILLION("Nonillion", new BigDecimal("1000000000000000000000000000000"), "N"),
    DECILLION("Decillion", new BigDecimal("1000000000000000000000000000000000"), "d");

    private final String name;
    private final BigDecimal scientificNotation;
    private final String suffix;

    ScaleSuffix(String name, BigDecimal scientificNotation, String suffix) {
        this.name = name;
        this.scientificNotation = scientificNotation;
        this.suffix = suffix;
    }

    public static ScaleSuffix fromSuffix(String suffix) {
        return Arrays.stream(ScaleSuffix.values())
                .filter(e -> e.suffix.equals(suffix))
                .findFirst()
                .orElse(null);
    }

    public String getName() {
        return name;
    }

    public BigDecimal getScientificNotation() {
        return scientificNotation;
    }

    public String getSuffix() {
        return suffix;
    }
}
