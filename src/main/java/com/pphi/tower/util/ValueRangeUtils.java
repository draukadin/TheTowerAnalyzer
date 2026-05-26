package com.pphi.tower.util;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ValueRangeUtils {

    private ValueRangeUtils() {}

    public static int getRowCount(final List<ValueRange> valueRanges) {
        return valueRanges.stream()
                .mapToInt(vr -> vr.getValues() == null ? 0 : vr.getValues().size())
                .max()
                .orElse(0);
    }

    public static int getColumnCount(final List<List<Object>> rows) {
        return rows.stream().mapToInt(List::size).max().orElse(0);
    }

    public static List<List<Object>> getRows(final ValueRange valueRange) {
        final List<List<Object>> values = valueRange.getValues();
        return values != null ? values : List.of();
    }

    public static List<String> collectRowValues(final List<ValueRange> valueRanges, final int row) {
        final List<String> values = new ArrayList<>();
        for (final ValueRange vr : valueRanges) {
            final List<List<Object>> rows = getRows(vr);
            final int colCount = getColumnCount(rows);
            if (row >= rows.size()) {
                for (int i = 0; i < colCount; i++) {
                    values.add("");
                }
            } else {
                final List<Object> rowData = rows.get(row);
                for (int col = 0; col < colCount; col++) {
                    values.add(col < rowData.size() ? Objects.toString(rowData.get(col), "") : "");
                }
            }
        }
        return values;
    }

    public static String getValue(final List<ValueRange> valueRanges, final int rowIndex, final int colIndex) {
        int rowCount = getRowCount(valueRanges);
        if (rowIndex <= rowCount) {
            final List<String> rowValues = collectRowValues(valueRanges, rowIndex);
            return rowValues.get(colIndex);
        }
        throw new ArrayIndexOutOfBoundsException(String.format("%d >= %d", rowIndex, rowCount));
    }
}
