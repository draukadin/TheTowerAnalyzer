package com.pphi.tower.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnExpression("!'${aws.region:}'.isEmpty()")
public class AwsConfig {

    private final AwsProperties aws;
    private final CredentialVendingClient credentialVendingClient;

    public AwsConfig(AwsProperties aws, CredentialVendingClient credentialVendingClient) {
        this.aws = aws;
        this.credentialVendingClient = credentialVendingClient;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(aws.getRegion()))
                .credentialsProvider(credentialVendingClient)
                .build();
    }

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(aws.getRegion()))
                .credentialsProvider(credentialVendingClient)
                .build();
    }
}
