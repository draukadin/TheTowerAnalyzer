package com.pphi.tower.repository;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import com.pphi.tower.config.SheetProperties;
import com.pphi.tower.model.sheets.GoogleSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class GoogleSheetsRepository {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetsRepository.class);
    private static final long INITIAL_BACKOFF_MS = 5_000;
    private static final long MAX_BACKOFF_MS = 60_000;

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

    public void writeCell(String sheetKey, String sheetName, String a1, String value) throws IOException {
        String spreadsheetId = sheetProperties.resolve(sheetKey);
        ValueRange body = new ValueRange().setValues(List.of(List.of(value)));
        sheets.spreadsheets().values()
                .update(spreadsheetId, sheetName + "!" + a1, body)
                .setValueInputOption("RAW")
                .execute();
    }

    private List<ValueRange> readRangesInternal(String spreadsheetId, String sheetName, List<String> ranges)
            throws IOException {
        List<String> qualifiedRanges = ranges.stream()
                .map(r -> sheetName + "!" + r)
                .toList();

        long backoffMs = INITIAL_BACKOFF_MS;
        for (int attempt = 1; ; attempt++) {
            try {
                BatchGetValuesResponse response = sheets.spreadsheets().values()
                        .batchGet(spreadsheetId)
                        .setRanges(qualifiedRanges)
                        .setValueRenderOption("FORMATTED_VALUE")
                        .execute();
                List<ValueRange> valueRanges = response.getValueRanges();
                return valueRanges != null ? valueRanges : List.of();
            } catch (GoogleJsonResponseException e) {
                if (e.getStatusCode() != 429) {
                    throw e;
                }
                log.warn("Rate limited by Sheets API (attempt {}), retrying in {}s...", attempt, backoffMs / 1000);
                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Interrupted while waiting to retry Sheets API call", ie);
                }
                backoffMs = Math.min(backoffMs * 2, MAX_BACKOFF_MS);
            }
        }
    }
}
