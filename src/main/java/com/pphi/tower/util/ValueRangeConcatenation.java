package com.pphi.tower.util;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.ArrayList;
import java.util.List;

import static com.pphi.tower.util.ValueRangeUtils.collectRowValues;
import static com.pphi.tower.util.ValueRangeUtils.getRowCount;

public final class ValueRangeConcatenation {

    private ValueRangeConcatenation() { }

    /**
     * Formats a list of ValueRanges as a Markdown table.
     * Row 0 is treated as the header row; subsequent non-empty rows become data rows.
     */
    public static String toMarkdownTable(final List<ValueRange> valueRanges) {
        if (valueRanges.isEmpty()) return "";

        final int rowCount = getRowCount(valueRanges);
        if (rowCount == 0) return "";

        // Collect all rows up front so we know the true column count
        final List<List<String>> allRows = new ArrayList<>();
        for (int row = 0; row < rowCount; row++) {
            allRows.add(collectRowValues(valueRanges, row));
        }

        final int colCount = allRows.stream().mapToInt(List::size).max().orElse(0);
        if (colCount == 0) return "";

        final StringBuilder sb = new StringBuilder();

        // Header row
        appendRow(sb, allRows.get(0), colCount);

        // Separator row
        sb.append("| ");
        for (int c = 0; c < colCount; c++) {
            sb.append(":--- | ");
        }
        sb.append("\n");

        // Data rows — skip fully empty rows
        for (int row = 1; row < rowCount; row++) {
            final List<String> rowValues = allRows.get(row);
            if (rowValues.stream().anyMatch(v -> !v.isEmpty())) {
                appendRow(sb, rowValues, colCount);
            }
        }

        return sb.toString();
    }

    /** Legacy CSV concatenation — kept for any callers that still need it. */
    public static String concatenate(final List<ValueRange> valueRanges) {
        if (valueRanges.isEmpty()) return "";
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

    private static void appendRow(StringBuilder sb, List<String> values, int colCount) {
        sb.append("| ");
        for (int c = 0; c < colCount; c++) {
            sb.append(c < values.size() ? values.get(c) : "");
            sb.append(" | ");
        }
        sb.append("\n");
    }
}
