package com.pphi.tower.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pphi.tower.jackson.TowerNumberDeserializer;
import com.pphi.tower.jackson.TowerNumberSerializer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;

@JsonSerialize(using = TowerNumberSerializer.class)
@JsonDeserialize(using = TowerNumberDeserializer.class)
public record TowerNumber(BigDecimal amount, ScaleSuffix scaleSuffix) {

    public TowerNumber minus(final TowerNumber subtrahend) {
        // 1. Convert both to raw flat doubles
        BigDecimal minuend = this.amount.multiply(this.scaleSuffix != null ? this.scaleSuffix.getScientificNotation() : BigDecimal.ONE);
        BigDecimal subtrahendRaw = subtrahend.amount.multiply(subtrahend.scaleSuffix != null ? subtrahend.scaleSuffix.getScientificNotation() : BigDecimal.ONE);

        // 2. Perform the subtraction
        BigDecimal difference = minuend.subtract(subtrahendRaw);

        // Handle edge case if the result drops to 0 or negative
        if (difference.doubleValue() <= 0) {
            return new TowerNumber(BigDecimal.valueOf(Math.max(0, difference.doubleValue())), null);
        }

        // 3. Find the largest suffix that is less than or equal to our raw result
        // We sort descending by notation to find the biggest tier that fits
        ScaleSuffix matchingSuffix = Arrays.stream(ScaleSuffix.values())
                .filter(suffix -> difference.compareTo(suffix.getScientificNotation()) >= 0)
                .max(Comparator.comparing(ScaleSuffix::getScientificNotation))
                .orElse(null); // Returns null if the number is less than 1,000 (no suffix)

        // 4. Scale the raw value down to fit the chosen suffix
        BigDecimal finalAmount = difference;
        if (matchingSuffix != null) {
            finalAmount = difference.divide(matchingSuffix.getScientificNotation(), RoundingMode.HALF_UP);
        }

        return new TowerNumber(finalAmount, matchingSuffix);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            sb.append("0.00");
        } else {
            sb.append(amount.toPlainString());
        }
        if (scaleSuffix != null) {
            sb.append(scaleSuffix.getSuffix());
        }
        return sb.toString();
    }
}
