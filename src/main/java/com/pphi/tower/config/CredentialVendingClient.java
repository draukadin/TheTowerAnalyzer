package com.pphi.tower.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.core.exception.SdkClientException;

import java.time.Duration;
import java.time.Instant;

@Component
@ConditionalOnProperty(name = "aws.region")
public class CredentialVendingClient implements AwsCredentialsProvider {

    private static final Logger log = LoggerFactory.getLogger(CredentialVendingClient.class);
    private static final Duration REFRESH_WINDOW = Duration.ofMinutes(5);

    private final AwsProperties aws;
    private final RestClient http;

    private volatile AwsSessionCredentials credentials;
    private volatile Instant expiry;

    public CredentialVendingClient(AwsProperties aws) {
        this.aws = aws;
        this.http = RestClient.create();
    }

    @PostConstruct
    public void init() {
        if (canVend()) {
            try {
                vend();
            } catch (Exception e) {
                log.warn("Credential vending failed at startup — AWS features will be unavailable: {}", e.getMessage());
            }
        } else {
            log.warn("Credential vending skipped: aws.player-id or aws.api-gateway.region not configured");
        }
    }

    @Scheduled(fixedDelay = 60_000)
    public void refreshIfNearExpiry() {
        if (expiry == null || !canVend()) return;
        if (Instant.now().isAfter(expiry.minus(REFRESH_WINDOW))) {
            log.info("Pre-emptively refreshing vended AWS credentials");
            try {
                vend();
            } catch (Exception e) {
                log.error("Scheduled credential refresh failed: {}", e.getMessage());
            }
        }
    }

    /** Called after an AccessDenied error to re-vend credentials for the caller's current IP. */
    public void forceRefresh() {
        log.info("Force-refreshing vended credentials (IP change recovery)");
        vend();
    }

    @Override
    public AwsCredentials resolveCredentials() {
        if (credentials == null || (expiry != null && Instant.now().isAfter(expiry.minus(Duration.ofSeconds(30))))) {
            if (!canVend()) {
                throw SdkClientException.create(
                        "AWS credentials unavailable: aws.player-id or aws.api-gateway.region not configured");
            }
            vend();
        }
        return credentials;
    }

    private void vend() {
        String url = resolveCredentialsUrl();
        String playerId = aws.getPlayerId();
        log.debug("Vending credentials from {}", url);

        VendedCredentials resp = http.get()
                .uri(url)
                .header("X-Player-Id", playerId)
                .retrieve()
                .body(VendedCredentials.class);

        if (resp == null) {
            throw SdkClientException.create("Empty response from credential-vending endpoint");
        }
        credentials = AwsSessionCredentials.create(
                resp.accessKeyId(), resp.secretAccessKey(), resp.sessionToken());
        expiry = resp.expiration();
        log.info("Vended AWS credentials for player {}; expiry={}", playerId, expiry);
    }

    private boolean canVend() {
        String playerId = aws.getPlayerId();
        AwsProperties.ApiGateway gw = aws.getApiGateway();
        String region = gw.getRegion();
        return playerId != null && !playerId.isBlank()
                && region != null && !region.isBlank()
                && gw.getUrl().containsKey(region);
    }

    private String resolveCredentialsUrl() {
        AwsProperties.ApiGateway gw = aws.getApiGateway();
        String region = gw.getRegion();
        String base = gw.getUrl().get(region);
        if (base == null) {
            throw SdkClientException.create("No API Gateway URL configured for region: " + region);
        }
        return base + "/credentials";
    }

    public record VendedCredentials(
            @JsonProperty("AccessKeyId")    String accessKeyId,
            @JsonProperty("SecretAccessKey") String secretAccessKey,
            @JsonProperty("SessionToken")   String sessionToken,
            @JsonProperty("Expiration")     Instant expiration
    ) {}
}