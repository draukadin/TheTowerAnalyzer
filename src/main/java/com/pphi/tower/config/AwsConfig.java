package com.pphi.tower.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(name = "aws.region")
public class AwsConfig {

    private final AwsProperties aws;

    public AwsConfig(AwsProperties aws) {
        this.aws = aws;
    }

    private AwsCredentialsProvider credentialsProvider() {
        String profile = aws.getProfile();
        return (profile != null && !profile.isBlank())
                ? ProfileCredentialsProvider.create(profile)
                : DefaultCredentialsProvider.create();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(aws.getRegion()))
                .credentialsProvider(credentialsProvider())
                .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(aws.getRegion()))
                .credentialsProvider(credentialsProvider())
                .build();
    }
}
