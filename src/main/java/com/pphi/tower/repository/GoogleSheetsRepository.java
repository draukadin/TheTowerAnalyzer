package com.pphi.tower.repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.config.SheetProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public class GoogleSheetsRepository {

    private final Sheets sheets;
    private final SheetProperties sheetProperties;

    public GoogleSheetsRepository(@Lazy Sheets sheets, SheetProperties sheetProperties) {
        this.sheets = sheets;
        this.sheetProperties = sheetProperties;
    }

    public void writeCell(String sheetKey, String sheetName, String a1, String value) throws IOException {
        String spreadsheetId = sheetProperties.resolve(sheetKey);
        ValueRange body = new ValueRange().setValues(List.of(List.of(value)));
        sheets.spreadsheets().values()
                .update(spreadsheetId, sheetName + "!" + a1, body)
                .setValueInputOption("RAW")
                .execute();
    }
}
