package com.pphi.tower.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class OAuthStateService {

    private static final Logger log = LoggerFactory.getLogger(OAuthStateService.class);

    public enum Status { PENDING, AUTHENTICATED, ERROR }

    private final GoogleAuthorizationCodeFlow flow;
    private volatile Status status = Status.PENDING;
    private volatile String authUrl;
    private final CompletableFuture<Credential> credentialFuture = new CompletableFuture<>();

    public OAuthStateService(GoogleAuthorizationCodeFlow flow) {
        this.flow = flow;
    }

    @PostConstruct
    public void init() {
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
            status = Status.ERROR;
            credentialFuture.completeExceptionally(e);
        }
    }

    public Status getStatus() { return status; }

    public String getAuthUrl() { return authUrl; }

    public Credential getCredential() throws Exception {
        return credentialFuture.get(10, TimeUnit.MINUTES);
    }
}
