package com.pphi.tower.model.sheets;

import java.util.List;

public interface GoogleSheet {

    List<String> ranges();
    String sheetName();

    /** Returns the logical sheet key (resolved to an actual ID via SheetProperties). */
    String sheetId();

    record Ref(String sheetId, String sheetName, List<String> ranges) implements GoogleSheet {}
}
