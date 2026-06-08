package com.pphi.tower.web;

import com.google.api.services.drive.model.File;
import com.pphi.tower.config.DriveProperties;
import com.pphi.tower.repository.GoogleDriveRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/backup")
public class BackupController {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private final GoogleDriveRepository driveRepository;
    private final DriveProperties driveProperties;

    public BackupController(GoogleDriveRepository driveRepository, DriveProperties driveProperties) {
        this.driveRepository = driveRepository;
        this.driveProperties = driveProperties;
    }

    @PostMapping("/database")
    public ResponseEntity<Map<String, String>> backupDatabase() throws Exception {
        String appdata = System.getenv("APPDATA");
        Path source = Path.of(appdata, "TheTowerAnalyzer", "analyzer.db");

        String fileName = "analyzer_" + LocalDateTime.now().format(TIMESTAMP) + ".db";
        Path tempCopy = Files.createTempFile("tower-backup-", ".db");
        try {
            Files.copy(source, tempCopy, StandardCopyOption.REPLACE_EXISTING);
            File uploaded = driveRepository.uploadFile(tempCopy.toFile(), fileName, driveProperties.getBackupFolderId());
            return ResponseEntity.ok(Map.of(
                    "fileId", uploaded.getId(),
                    "fileName", uploaded.getName()
            ));
        } finally {
            Files.deleteIfExists(tempCopy);
        }
    }
}
