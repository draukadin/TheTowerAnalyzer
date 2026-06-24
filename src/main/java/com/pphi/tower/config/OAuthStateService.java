package com.pphi.tower.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class OAuthStateService {

    private static final Logger log = LoggerFactory.getLogger(OAuthStateService.class);

    public enum Status { PENDING, AUTHENTICATED, ERROR }

    private final GoogleAuthorizationCodeFlow flow;
    private final SetupStateService setupState;
    private final AwsProperties awsProperties;

    private volatile Status status = Status.PENDING;
    private volatile String authUrl;
    private volatile CompletableFuture<Credential> credentialFuture = new CompletableFuture<>();

    public OAuthStateService(@Lazy GoogleAuthorizationCodeFlow flow, SetupStateService setupState,
                             AwsProperties awsProperties) {
        this.flow = flow;
        this.setupState = setupState;
        this.awsProperties = awsProperties;
    }

    @PostConstruct
    public void init() {
        if (awsProperties.isConfigured()) {
            log.info("AWS centralized mode — Google OAuth not required.");
            return;
        }
        if (!setupState.isComplete()) {
            log.info("First-run setup not yet complete — OAuth flow deferred until setup finishes.");
            return;
        }
        CompletableFuture.runAsync(this::runAuthFlow);
    }

    public synchronized void reinitialize() {
        if (status == Status.AUTHENTICATED) return;
        credentialFuture = new CompletableFuture<>();
        status = Status.PENDING;
        authUrl = null;
        CompletableFuture.runAsync(this::runAuthFlow);
    }

    private void runAuthFlow() {
        try {
            Credential existing = flow.loadCredential("user");
            if (existing != null) {
                status = Status.AUTHENTICATED;
                credentialFuture.complete(existing);
                return;
            }

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            String redirectUri = receiver.getRedirectUri();

            authUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri).build();
            log.info("Google OAuth required. Open this URL in your browser to authorize:\n{}", authUrl);

            String code = receiver.waitForCode();
            receiver.stop();

            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(redirectUri)
                    .execute();
            Credential credential = flow.createAndStoreCredential(tokenResponse, "user");

            authUrl = null;
            status = Status.AUTHENTICATED;
            credentialFuture.complete(credential);

        } catch (Exception e) {
            log.error("OAuth flow failed", e);
            status = Status.ERROR;
            credentialFuture.completeExceptionally(e);
        }
    }

    public Status getStatus() {
        if (awsProperties.isConfigured()) return Status.AUTHENTICATED;
        return status;
    }

    public String getAuthUrl() { return authUrl; }

    public Credential getCredential() throws Exception {
        return credentialFuture.get(10, TimeUnit.MINUTES);
    }
}
