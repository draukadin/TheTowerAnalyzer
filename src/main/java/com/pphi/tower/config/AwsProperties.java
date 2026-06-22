package com.pphi.tower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    private String region;
    private String s3Bucket;
    private String playerId;
    private String dynamodbTable;
    private ApiGateway apiGateway = new ApiGateway();

    public static class ApiGateway {
        /** User-selected region key: "us", "eu", or "ap" */
        private String region;
        /** Base API Gateway stage URLs keyed by region: aws.api-gateway.url.us=https://... */
        private Map<String, String> url = new HashMap<>();

        public String getRegion()           { return region; }
        public void setRegion(String v)     { this.region = v; }
        public Map<String, String> getUrl() { return url; }
        public void setUrl(Map<String, String> v) { this.url = v; }
    }

    public String getRegion()         { return region; }
    public void setRegion(String v)   { this.region = v; }

    public String getS3Bucket()           { return s3Bucket; }
    public void setS3Bucket(String v)     { this.s3Bucket = v; }

    public String getPlayerId()           { return playerId; }
    public void setPlayerId(String v)     { this.playerId = v; }

    public String getDynamodbTable()          { return dynamodbTable; }
    public void setDynamodbTable(String v)    { this.dynamodbTable = v; }

    public ApiGateway getApiGateway()             { return apiGateway; }
    public void setApiGateway(ApiGateway v)       { this.apiGateway = v; }

    public boolean isConfigured() {
        return region != null && !region.isBlank()
                && s3Bucket != null && !s3Bucket.isBlank()
                && playerId != null && !playerId.isBlank();
    }
}
