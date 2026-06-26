package com.pphi.tower.service;

import com.pphi.tower.config.AwsProperties;
import com.pphi.tower.repository.VersionHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class DdbVersionSyncService {

    private static final Logger log = LoggerFactory.getLogger(DdbVersionSyncService.class);

    private final AwsProperties aws;
    private final VersionHistoryRepository versionRepo;

    @Autowired(required = false)
    private DynamoDbClient dynamoDbClient;

    public DdbVersionSyncService(AwsProperties aws, VersionHistoryRepository versionRepo) {
        this.aws = aws;
        this.versionRepo = versionRepo;
    }

    /**
     * Writes the latest recorded tower version to DynamoDB. Called on setup completion so
     * the DDB record is initialized from actual local history rather than being left absent
     * until the user next records a version change.
     */
    public boolean syncLatestVersion() {
        List<VersionHistoryRepository.VersionEntry> versions = versionRepo.getAllVersions();
        if (versions.isEmpty()) return false;
        return syncVersion(versions.getFirst().version());
    }

    public boolean syncVersion(String version) {
        if (dynamoDbClient == null
                || aws.getDynamodbTable() == null
                || aws.getPlayerId() == null) {
            return false;
        }
        try {
            doPut(version);
            log.info("Wrote version {} to DynamoDB for player {}", version, aws.getPlayerId());
            return true;
        } catch (Exception e) {
            log.error("Failed to write version {} to DynamoDB: {}", version, e.getMessage());
            return false;
        }
    }

    private void doPut(String version) {
        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(aws.getDynamodbTable())
                .item(Map.of(
                        "player_id",       AttributeValue.fromS(aws.getPlayerId()),
                        "current_version", AttributeValue.fromS(version),
                        "updated_at",      AttributeValue.fromS(Instant.now().toString())))
                .build());
    }

}
