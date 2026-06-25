package com.pphi.tower.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.config.AwsProperties;
import com.pphi.tower.config.SetupStateService;
import com.pphi.tower.config.SetupStateService.Step;
import com.pphi.tower.service.ClaudeSkillsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.pphi.tower.web.SetupController.ConfigRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetupControllerTest {

    @Mock SetupStateService setupState;
    @Mock AwsProperties aws;
    @Mock ClaudeSkillsService claudeSkillsService;
    ObjectMapper objectMapper = new ObjectMapper();

    SetupController sut;

    @BeforeEach
    void setUp(@TempDir Path dir) {
        sut = controller(dir);
    }

    // ── Section 3: GET /api/setup/status ─────────────────────────────────────

    @Test
    void status_config() {                                  // 3.1
        when(setupState.currentStep()).thenReturn(Step.CONFIG);
        assertThat(sut.status()).containsEntry("step", "config");
    }

    @Test
    void status_complete() {                                // 3.2
        when(setupState.currentStep()).thenReturn(Step.COMPLETE);
        assertThat(sut.status()).containsEntry("step", "complete");
    }

    // ── Section 4: POST /api/setup/config — validation ───────────────────────

    @Test
    void blankPlayerId_400() throws Exception {             // 4.4
        assertThat(sut.saveConfig(new ConfigRequest("  ", "us")).getStatusCode().value()).isEqualTo(400);
    }

    @Test
    void nullPlayerId_400() throws Exception {              // 4.5
        var resp = sut.saveConfig(new ConfigRequest(null, "us"));
        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        assertThat(resp.getBody()).containsEntry("error", "Player ID is required.");
    }

    @Test
    void invalidRegion_400() throws Exception {             // 4.6
        var resp = sut.saveConfig(new ConfigRequest("abc", "ca"));
        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        assertThat(resp.getBody()).containsEntry("error", "Region must be one of: us, eu, ap.");
    }

    @Test
    void nullRegion_400() throws Exception {                // 4.7
        var resp = sut.saveConfig(new ConfigRequest("abc", null));
        assertThat(resp.getStatusCode().value()).isEqualTo(400);
        assertThat(resp.getBody()).containsEntry("error", "Region must be one of: us, eu, ap.");
    }

    // ── Section 4: POST /api/setup/config — happy paths ──────────────────────

    @Test
    void saveConfig_us_200AndFileWritten(@TempDir Path dir) throws Exception { // 4.1
        var gw = stubForWrite();
        var controller = controller(dir);
        var resp = controller.saveConfig(new ConfigRequest("abc123", "us"));
        assertThat(resp.getStatusCode().value()).isEqualTo(200);
        assertThat(resp.getBody()).containsEntry("step", "complete");
        verify(aws).setPlayerId("abc123");
        verify(gw).setRegion("us");
        var content = Files.readString(dir.resolve("user.properties"));
        assertThat(content)
                .contains("aws.player-id=abc123")
                .contains("aws.api-gateway.region=us");
    }

    @Test
    void saveConfig_eu_fileHasEuRegion(@TempDir Path dir) throws Exception {   // 4.2
        stubForWrite();
        controller(dir).saveConfig(new ConfigRequest("p1", "eu"));
        assertThat(Files.readString(dir.resolve("user.properties")))
                .contains("aws.api-gateway.region=eu");
    }

    @Test
    void saveConfig_ap_fileHasApRegion(@TempDir Path dir) throws Exception {   // 4.3
        stubForWrite();
        controller(dir).saveConfig(new ConfigRequest("p1", "ap"));
        assertThat(Files.readString(dir.resolve("user.properties")))
                .contains("aws.api-gateway.region=ap");
    }

    @Test
    void saveConfig_dirMissing_createsDirectoryAndFile(@TempDir Path base) throws Exception { // 4.8
        stubForWrite();
        Path nested = base.resolve("nonexistent");          // does not exist yet
        controller(nested).saveConfig(new ConfigRequest("abc", "us"));
        assertThat(nested).isDirectory();
        assertThat(nested.resolve("user.properties")).exists();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private SetupController controller(Path dir) {
        return new SetupController(setupState, aws, claudeSkillsService, objectMapper) {
            @Override Path dataDir() { return dir; }
        };
    }

    private AwsProperties.ApiGateway stubForWrite() {
        var gw = mock(AwsProperties.ApiGateway.class);
        when(aws.getApiGateway()).thenReturn(gw);
        return gw;
    }
}
