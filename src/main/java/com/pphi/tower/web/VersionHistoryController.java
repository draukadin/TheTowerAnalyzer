package com.pphi.tower.web;

import com.pphi.tower.config.AwsProperties;
import com.pphi.tower.config.CredentialVendingClient;
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
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.io.IOException;
import java.time.Instant;
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

    @Autowired(required = false)
    private CredentialVendingClient credentialClient;

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
    public VersionWithChanges create(@RequestBody CreateRequest req) {
        repo.create(req.version(), req.type(), req.changes());
        String summary = repo.getAllVersions().stream()
                .filter(v -> v.version().equals(req.version()))
                .map(VersionEntry::summary).findFirst().orElse("");
        boolean synced = syncVersionCell(req.version());
        return new VersionWithChanges(req.version(), req.type(), summary,
                repo.getChangesForVersion(req.version()), synced, syncTarget());
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
        if (dynamoDbClient == null
                || awsProperties.getDynamodbTable() == null
                || awsProperties.getPlayerId() == null) {
            return false;
        }
        try {
            doPutVersionItem(version);
            log.info("Wrote version {} to DynamoDB for player {}", version, awsProperties.getPlayerId());
            return true;
        } catch (AwsServiceException e) {
            if (isAccessDenied(e) && credentialClient != null) {
                log.warn("DynamoDB access denied — re-vending credentials and retrying");
                credentialClient.forceRefresh();
                try {
                    doPutVersionItem(version);
                    log.info("Wrote version {} to DynamoDB for player {} (after re-vend)", version, awsProperties.getPlayerId());
                    return true;
                } catch (Exception retryEx) {
                    log.error("Failed to write version {} to DynamoDB after re-vend: {}", version, retryEx.getMessage());
                    return false;
                }
            }
            log.error("Failed to write version {} to DynamoDB: {}", version, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Failed to write version {} to DynamoDB: {}", version, e.getMessage());
            return false;
        }
    }

    private void doPutVersionItem(String version) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(awsProperties.getDynamodbTable())
                .item(Map.of(
                        "player_id",       AttributeValue.fromS(awsProperties.getPlayerId()),
                        "current_version", AttributeValue.fromS(version),
                        "updated_at",      AttributeValue.fromS(Instant.now().toString())))
                .build());
    }

    private static boolean isAccessDenied(AwsServiceException e) {
        String code = e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : "";
        return "AccessDenied".equals(code) || "AccessDeniedException".equals(code);
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
