package com.pphi.tower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    private String region;
    private String s3Bucket;
    private String playerId;
    private String dynamodbTable;

    public String getRegion()         { return region; }
    public void setRegion(String v)   { this.region = v; }

    public String getS3Bucket()           { return s3Bucket; }
    public void setS3Bucket(String v)     { this.s3Bucket = v; }

    public String getPlayerId()           { return playerId; }
    public void setPlayerId(String v)     { this.playerId = v; }

    public String getDynamodbTable()          { return dynamodbTable; }
    public void setDynamodbTable(String v)    { this.dynamodbTable = v; }

    public boolean isConfigured() {
        return region != null && !region.isBlank()
                && s3Bucket != null && !s3Bucket.isBlank()
                && playerId != null && !playerId.isBlank();
    }
}
