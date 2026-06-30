package com.pphi.tower.web;

import com.pphi.tower.config.AwsProperties;
import com.pphi.tower.repository.GoogleSheetsRepository;
import com.pphi.tower.repository.PendingVersionChangeRepository;
import com.pphi.tower.repository.PendingVersionChangeRepository.PendingChange;
import com.pphi.tower.repository.VersionHistoryRepository;
import com.pphi.tower.repository.VersionHistoryRepository.VersionChange;
import com.pphi.tower.repository.VersionHistoryRepository.VersionEntry;
import com.pphi.tower.service.DdbVersionSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/versions")
@CrossOrigin(origins = "*")
public class VersionHistoryController {

    private static final Logger log = LoggerFactory.getLogger(VersionHistoryController.class);
    private static final String TRACKER_SHEET_KEY  = "player-tracker";
    private static final String TRACKER_SHEET_NAME = "TowerVersionTracking";
    private static final String VERSION_CELL       = "B2";

    private final VersionHistoryRepository        repo;
    private final PendingVersionChangeRepository  pendingRepo;
    private final GoogleSheetsRepository          sheetsRepo;
    private final AwsProperties                   awsProperties;
    private final DdbVersionSyncService           ddbSync;

    public VersionHistoryController(
            final VersionHistoryRepository repo,
            final PendingVersionChangeRepository pendingRepo,
            final GoogleSheetsRepository sheetsRepo,
            final AwsProperties awsProperties,
            final DdbVersionSyncService ddbSync) {
        this.repo          = repo;
        this.pendingRepo   = pendingRepo;
        this.sheetsRepo    = sheetsRepo;
        this.awsProperties = awsProperties;
        this.ddbSync       = ddbSync;
    }

    public record VersionWithChanges(String version, String type, String summary,
                                     List<VersionChange> changes, boolean synced, String syncTarget) {}
    public record CreateRequest(String version, String type,
                                List<VersionHistoryRepository.NewChange> changes) {}
    public record UpdateRequest(String type, List<VersionHistoryRepository.NewChange> changes) {}
    public record SyncResult(boolean synced) {}

    @GetMapping
    public List<VersionWithChanges> getAll() {
        return repo.getAllVersions().stream()
                .map(v -> new VersionWithChanges(
                        v.version(), v.type(), v.summary(),
                        repo.getChangesForVersion(v.version()), true, syncTarget()))
                .toList();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequest req) {
        if (repo.versionExists(req.version())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    "Version \"" + req.version() + "\" already exists. "
                    + "Please choose a different version number.");
        }
        repo.create(req.version(), req.type(), req.changes());
        String summary = repo.getAllVersions().stream()
                .filter(v -> v.version().equals(req.version()))
                .map(VersionEntry::summary).findFirst().orElse("");
        boolean synced = syncVersionCell(req.version());
        return ResponseEntity.ok(new VersionWithChanges(req.version(), req.type(), summary,
                repo.getChangesForVersion(req.version()), synced, syncTarget()));
    }

    @PutMapping("/{version}")
    public VersionWithChanges update(@PathVariable String version, @RequestBody UpdateRequest req) {
        repo.update(version, req.type(), req.changes());
        String summary = repo.getAllVersions().stream()
                .filter(v -> v.version().equals(version))
                .map(VersionEntry::summary).findFirst().orElse("");
        return new VersionWithChanges(version, req.type(), summary,
                repo.getChangesForVersion(version), true, syncTarget());
    }

    @GetMapping("/pending")
    public List<PendingChange> getPending() {
        return pendingRepo.getAll();
    }

    @DeleteMapping("/pending/{id}")
    public void deletePendingById(@PathVariable long id) {
        pendingRepo.deleteById(id);
    }

    @DeleteMapping("/pending")
    public void clearPending() {
        pendingRepo.deleteAll();
    }

    @PostMapping("/{version}/sync-sheet")
    public SyncResult retrySync(@PathVariable String version) throws IOException {
        if (awsProperties.isConfigured()) {
            // Centralized mode: version is tracked in DynamoDB; the Google Sheet is legacy-only.
            return new SyncResult(true);
        }
        sheetsRepo.writeCell(TRACKER_SHEET_KEY, TRACKER_SHEET_NAME, VERSION_CELL, version);
        return new SyncResult(true);
    }

    @PostMapping("/{version}/sync-ddb")
    public SyncResult retrySyncDdb(@PathVariable String version) {
        syncVersionToDdb(version);
        return new SyncResult(true);
    }

    /** Which backend a version write targets: "ddb" in centralized mode, "sheet" in legacy mode. */
    private String syncTarget() {
        return awsProperties.isConfigured() ? "ddb" : "sheet";
    }

    private boolean syncVersionCell(String version) {
        if (awsProperties.isConfigured()) {
            // Centralized mode: version tracking lives in DynamoDB; skip the legacy Google Sheet.
            return syncVersionToDdb(version);
        }
        return syncVersionToSheet(version);
    }

    private boolean syncVersionToDdb(String version) {
        return ddbSync.syncVersion(version);
    }

    private boolean syncVersionToSheet(String version) {
        try {
            sheetsRepo.writeCell(TRACKER_SHEET_KEY, TRACKER_SHEET_NAME, VERSION_CELL, version);
            return true;
        } catch (IOException e) {
            log.error("Failed to write version {} to sheet cell {}: {}", version, VERSION_CELL, e.getMessage());
            return false;
        }
    }
}
