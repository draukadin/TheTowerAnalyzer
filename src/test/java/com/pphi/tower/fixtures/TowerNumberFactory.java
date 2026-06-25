package com.pphi.tower.fixtures;

import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerNumber;

import java.math.BigDecimal;

public final class TowerNumberFactory {

    private TowerNumberFactory() {}

    public static TowerNumber of(double amount, ScaleSuffix suffix) {
        return new TowerNumber(BigDecimal.valueOf(amount), suffix);
    }

    public static TowerNumber ofRaw(double raw) {
        if (raw == 0.0) return zero();
        ScaleSuffix match = null;
        for (ScaleSuffix s : ScaleSuffix.values()) {
            if (raw >= s.getScientificNotation().doubleValue()) match = s;
        }
        if (match == null) return new TowerNumber(BigDecimal.valueOf(raw), null);
        return new TowerNumber(
                BigDecimal.valueOf(raw / match.getScientificNotation().doubleValue()), match);
    }

    public static TowerNumber zero() {
        return new TowerNumber(BigDecimal.ZERO, null);
    }
}