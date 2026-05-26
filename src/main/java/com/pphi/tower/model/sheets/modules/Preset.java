package com.pphi.tower.model.sheets.modules;

public enum Preset {
    FARMING("1JROY3TS01YpH74I6zzmbds573_FKUu3mkfiEAFwgg14"),
    TOURNAMENT("1JROY3TS01YpH74I6zzmbds573_FKUu3mkfiEAFwgg14");

    private final String sheetId;

    Preset(String sheetId) {
        this.sheetId = sheetId;
    }

    public String sheetId() {
        return sheetId;
    }
}
