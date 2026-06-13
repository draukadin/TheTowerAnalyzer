package com.pphi.tower.model.googledrive;

import com.google.api.services.drive.model.File;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class BattleReportDriveFile extends DriveFile {

    public BattleReportDriveFile(File file, String contents) {
        this(file.getId(), file.getName(), contents);
    }

    public BattleReportDriveFile(String id, String name, String contents) {
        super(id, name, insertTowerEra(name, contents));
    }

    private static String insertTowerEra(String name, String original) {
        List<String> lines = original.lines().collect(Collectors.toCollection(ArrayList::new));
        lines.add(1, getTowerEra(name));
        return String.join(System.lineSeparator(), lines);
    }

    //TODO add handling here to default to 1.0.0 if the file name doesn't contain a version
    //TODO verify if the sheet doesn't have a version in B2 it the file name format is _Battle_Report_2026-05-21_21-58-09.txt
    private static String getTowerEra(String name) {
        return String.format("Tower Era\t%s", name.split("_")[0].replace("v", ""));
    }

    public String runType() {
        // v2.0.2_Battle_Report_2026-05-21_21-58-09.txt
        return name().split("_")[1];
    }
}
