package com.pphi.tower.util;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

import static com.pphi.tower.util.ValueRangeUtils.collectRowValues;
import static com.pphi.tower.util.ValueRangeUtils.getRowCount;

public final class ValueRangeConcatenation {

    private ValueRangeConcatenation() { }

    public static String concatenate(final List<ValueRange> valueRanges) {
        if (valueRanges.isEmpty()) {
            return "";
        }
        final int rowCount = getRowCount(valueRanges);
        final StringBuilder sb = new StringBuilder();
        for (int row = 0; row < rowCount; row++) {
            final List<String> rowValues = collectRowValues(valueRanges, row);
            if (rowValues.stream().anyMatch(v -> !v.isEmpty())) {
                sb.append(String.join(",", rowValues)).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
