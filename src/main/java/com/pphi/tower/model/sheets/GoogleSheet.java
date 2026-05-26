package com.pphi.tower.model.sheets;

import java.util.List;

public interface GoogleSheet {

    List<String> ranges();
    String sheetName();
    String sheetId();
}
