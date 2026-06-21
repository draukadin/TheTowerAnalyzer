package com.pphi.tower.web;

import com.pphi.tower.config.AwsProperties;
import com.pphi.tower.repository.GoogleSheetsRepository;
import com.pphi.tower.repository.PendingVersionChangeRepository;
import com.pphi.tower.repository.PendingVersionChangeRepository.PendingChange;
import com.pphi.tower.repository.VersionHistoryRepository;
import com.pphi.tower.repository.VersionHistoryRepository.VersionChange;
import com.pphi.tower.repository.VersionHistoryRepository.VersionEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    @Autowired(required = false)
    private DynamoDbClient dynamoDbClient;

    public VersionHistoryController(VersionHistoryRepository repo,
                                    PendingVersionChangeRepository pendingRepo,
                                    GoogleSheetsRepository sheetsRepo,
                                    AwsProperties awsProperties) {
        this.repo          = repo;
        this.pendingRepo   = pendingRepo;
        this.sheetsRepo    = sheetsRepo;
        this.awsProperties = awsProperties;
    }

    public record VersionWithChanges(String version, String type, String summary,
                                     List<VersionChange> changes, boolean syncedToSheet) {}
    public record CreateRequest(String version, String type,
                                List<VersionHistoryRepository.NewChange> changes) {}
    public record UpdateRequest(String type, List<VersionHistoryRepository.NewChange> changes) {}
    public record SyncResult(boolean synced) {}

    @GetMapping
    public List<VersionWithChanges> getAll() {
        return repo.getAllVersions().stream()
                .map(v -> new VersionWithChanges(
                        v.version(), v.type(), v.summary(),
                        repo.getChangesForVersion(v.version()), true))
                .toList();
    }

    @PostMapping
    public VersionWithChanges create(@RequestBody CreateRequest req) {
        repo.create(req.version(), req.type(), req.changes());
        String summary = repo.getAllVersions().stream()
                .filter(v -> v.version().equals(req.version()))
                .map(VersionEntry::summary).findFirst().orElse("");
        boolean synced = syncVersionCell(req.version());
        return new VersionWithChanges(req.version(), req.type(), summary,
                repo.getChangesForVersion(req.version()), synced);
    }

    @PutMapping("/{version}")
    public VersionWithChanges update(@PathVariable String version, @RequestBody UpdateRequest req) {
        repo.update(version, req.type(), req.changes());
        String summary = repo.getAllVersions().stream()
                .filter(v -> v.version().equals(version))
                .map(VersionEntry::summary).findFirst().orElse("");
        return new VersionWithChanges(version, req.type(), summary,
                repo.getChangesForVersion(version), true);
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
        sheetsRepo.writeCell(TRACKER_SHEET_KEY, TRACKER_SHEET_NAME, VERSION_CELL, version);
        return new SyncResult(true);
    }

    @PostMapping("/{version}/sync-ddb")
    public SyncResult retrySyncDdb(@PathVariable String version) {
        syncVersionToDdb(version);
        return new SyncResult(true);
    }

    private boolean syncVersionCell(String version) {
        boolean ddb    = syncVersionToDdb(version);
        boolean sheets = syncVersionToSheet(version);
        return ddb || sheets;
    }

    private boolean syncVersionToDdb(String version) {
        if (dynamoDbClient == null
                || awsProperties.getDynamodbTable() == null
                || awsProperties.getPlayerId() == null) {
            return false;
        }
        try {
            dynamoDbClient.putItem(PutItemRequest.builder()
                    .tableName(awsProperties.getDynamodbTable())
                    .item(Map.of(
                            "player_id",       AttributeValue.fromS(awsProperties.getPlayerId()),
                            "current_version", AttributeValue.fromS(version)))
                    .build());
            log.info("Wrote version {} to DynamoDB for player {}", version, awsProperties.getPlayerId());
            return true;
        } catch (Exception e) {
            log.error("Failed to write version {} to DynamoDB: {}", version, e.getMessage());
            return false;
        }
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
