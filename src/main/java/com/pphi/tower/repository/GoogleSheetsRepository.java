package com.pphi.tower.repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class GoogleSheetsRepository {

    private final Sheets sheets;

    public GoogleSheetsRepository(Sheets sheets) {
        this.sheets = sheets;
    }

    /**
     * Reads one or more (possibly non-contiguous) cell ranges from a worksheet.
     * Formulas are returned as their computed values, not as formula strings.
     *
     * @param spreadsheetId Google Sheets document ID (from the URL)
     * @param sheetName     worksheet tab name
     * @param ranges        A1-notation ranges, e.g. ["A1:B3", "D5:D10", "F7"]
     * @return one ValueRange per requested range; call {@link ValueRange#getValues()} for the rows
     */
    public List<ValueRange> readRanges(String spreadsheetId, String sheetName, List<String> ranges) throws IOException {
        List<String> qualifiedRanges = ranges.stream()
                .map(r -> sheetName + "!" + r)
                .toList();

        BatchGetValuesResponse response = sheets.spreadsheets().values()
                .batchGet(spreadsheetId)
                .setRanges(qualifiedRanges)
                .setValueRenderOption("FORMATTED_VALUE")
                .execute();

        List<ValueRange> valueRanges = response.getValueRanges();
        return valueRanges != null ? valueRanges : List.of();
    }

    /**
     * Reads a single cell range from a worksheet.
     * Formulas are returned as their computed values, not as formula strings.
     *
     * @param spreadsheetId Google Sheets document ID (from the URL)
     * @param sheetName     worksheet tab name
     * @param range         A1-notation range, e.g. "A1:B3" or "C5"
     * @return the ValueRange for the requested range
     */
    public ValueRange readRange(String spreadsheetId, String sheetName, String range) throws IOException {
        List<ValueRange> results = readRanges(spreadsheetId, sheetName, List.of(range));
        return results.isEmpty() ? new ValueRange() : results.getFirst();
    }
}
