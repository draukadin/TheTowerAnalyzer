package com.pphi.tower.repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.config.SheetProperties;
import com.pphi.tower.model.sheets.GoogleSheet;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class GoogleSheetsRepository {

    private final Sheets sheets;
    private final SheetProperties sheetProperties;

    public GoogleSheetsRepository(Sheets sheets, SheetProperties sheetProperties) {
        this.sheets = sheets;
        this.sheetProperties = sheetProperties;
    }

    /**
     * Reads one or more (possibly non-contiguous) cell ranges from a worksheet.
     * Formulas are returned as their computed values, not as formula strings.
     * Results are cached by the GoogleSheet identity; call DELETE /api/cache/sheets to evict.
     *
     * @param googleSheet sheetKey, sheetName, and ranges
     * @return one ValueRange per requested range; call {@link ValueRange#getValues()} for the rows
     */
    @Cacheable("sheets")
    public List<ValueRange> readRanges(GoogleSheet googleSheet) throws IOException {
        String actualSheetId = sheetProperties.resolve(googleSheet.sheetId());
        return readRangesInternal(actualSheetId, googleSheet.sheetName(), googleSheet.ranges());
    }

    private List<ValueRange> readRangesInternal(String spreadsheetId, String sheetName, List<String> ranges)
            throws IOException {
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
}
