package com.pphi.tower.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.pphi.tower.config.OAuthStateService.Status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuthStateServiceTest {

    @Mock GoogleAuthorizationCodeFlow flow;
    @Mock SetupStateService setupState;
    @Mock AwsProperties awsProperties;

    @Test
    void awsConfigured_initSkipsOAuth_statusIsAuthenticated() throws Exception { // 2.1 + 2.2
        when(awsProperties.isConfigured()).thenReturn(true);
        var sut = new OAuthStateService(flow, setupState, awsProperties);
        sut.init();
        verify(flow, never()).loadCredential(anyString());
        assertThat(sut.getStatus()).isEqualTo(Status.AUTHENTICATED);
    }

    @Test
    void awsNotConfigured_setupIncomplete_statusIsPending() {                   // 2.3
        when(awsProperties.isConfigured()).thenReturn(false);
        when(setupState.isComplete()).thenReturn(false);
        var sut = new OAuthStateService(flow, setupState, awsProperties);
        sut.init();
        assertThat(sut.getStatus()).isEqualTo(Status.PENDING);
    }
}
