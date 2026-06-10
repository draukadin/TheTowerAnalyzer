package com.pphi.tower.config;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class SetupStateService {

    public enum Step { CREDENTIALS, CONFIG, COMPLETE }

    private static final String PLACEHOLDER_PREFIX = "REPLACE_WITH_";

    private final DriveProperties drive;
    private final SheetProperties sheets;

    public SetupStateService(DriveProperties drive, SheetProperties sheets) {
        this.drive = drive;
        this.sheets = sheets;
    }

    public Step currentStep() {
        if (!Files.exists(Path.of(drive.getOauthCredentialsFile()))) {
            return Step.CREDENTIALS;
        }
        if (isPlaceholder(drive.getBackupFolderId())
                || isPlaceholder(drive.getBattleReportsFolderId())
                || isPlaceholder(sheets.getIds().get("player-tracker"))) {
            return Step.CONFIG;
        }
        return Step.COMPLETE;
    }

    public boolean isComplete() {
        return currentStep() == Step.COMPLETE;
    }

    private boolean isPlaceholder(String value) {
        return value == null || value.startsWith(PLACEHOLDER_PREFIX);
    }
}
