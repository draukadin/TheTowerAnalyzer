package com.pphi.tower.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.config.DriveProperties;
import com.pphi.tower.config.OAuthStateService;
import com.pphi.tower.config.SetupStateService;
import com.pphi.tower.config.SheetProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/setup")
public class SetupController {

    private final SetupStateService setupState;
    private final DriveProperties drive;
    private final SheetProperties sheets;
    private final OAuthStateService oAuthStateService;
    private final ObjectMapper objectMapper;

    public SetupController(SetupStateService setupState, DriveProperties drive,
                           SheetProperties sheets, OAuthStateService oAuthStateService,
                           ObjectMapper objectMapper) {
        this.setupState = setupState;
        this.drive = drive;
        this.sheets = sheets;
        this.oAuthStateService = oAuthStateService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of("step", setupState.currentStep().name().toLowerCase());
    }

    @PostMapping("/credentials")
    public ResponseEntity<Map<String, String>> saveCredentials(@RequestBody Map<String, String> body) throws IOException {
        String content = body.get("content");
        if (content == null || content.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Credentials JSON content is required."));
        }

        JsonNode root;
        try {
            root = objectMapper.readTree(content);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON — please paste the full contents of the file you downloaded from Google Cloud Console."));
        }

        if (!root.has("installed") && !root.has("web")) {
            return ResponseEntity.badRequest().body(Map.of("error", "This does not look like a Google OAuth credentials file. It must contain an \"installed\" or \"web\" key."));
        }

        Path dest = Path.of(drive.getOauthCredentialsFile());
        Files.createDirectories(dest.getParent());
        Files.writeString(dest, content);

        return ResponseEntity.ok(Map.of("step", setupState.currentStep().name().toLowerCase()));
    }

    @PostMapping("/config")
    public ResponseEntity<Map<String, String>> saveConfig(@RequestBody ConfigRequest req) throws IOException {
        if (req.backupFolderId() == null || req.backupFolderId().isBlank()
                || req.battleReportsFolderId() == null || req.battleReportsFolderId().isBlank()
                || req.playerTrackerSheetId() == null || req.playerTrackerSheetId().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "All three IDs are required."));
        }

        // Update the live beans so the running app has the correct values immediately
        drive.setBackupFolderId(req.backupFolderId());
        drive.setBattleReportsFolderId(req.battleReportsFolderId());
        Map<String, String> ids = new HashMap<>(sheets.getIds());
        ids.put("player-tracker", req.playerTrackerSheetId());
        sheets.setIds(ids);

        // Persist to user.properties
        String appData = System.getenv("APPDATA");
        Path dir = Path.of(appData, "TheTowerAnalyzer");
        String fwdDir = dir.toString().replace('\\', '/');
        String props = String.join(System.lineSeparator(),
            "drive.oauth-credentials-file=" + fwdDir + "/oauth-credentials.json",
            "drive.tokens-dir=" + fwdDir + "/tokens",
            "drive.application-name=TheTowerAnalyzer",
            "",
            "drive.backup-folder-id=" + req.backupFolderId(),
            "drive.battle-reports-folder-id=" + req.battleReportsFolderId(),
            "",
            "sheets.ids.player-tracker=" + req.playerTrackerSheetId(),
            "",
            "# Optional: change the port if 8080 is already in use on your machine",
            "# server.port=8080"
        );
        Files.writeString(dir.resolve("user.properties"), props);

        // Start the OAuth flow now that credentials and config are in place
        oAuthStateService.reinitialize();

        return ResponseEntity.ok(Map.of("step", "complete"));
    }

    @PostMapping("/mcp")
    public ResponseEntity<Map<String, String>> setupMcp() {
        Path mcpDir;
        try {
            mcpDir = getMcpDir();
        } catch (URISyntaxException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", "Could not determine install directory."));
        }

        Path nodeExe  = mcpDir.resolve("node.exe");
        Path serverJs = mcpDir.resolve("server.js");

        if (!Files.exists(nodeExe) || !Files.exists(serverJs)) {
            return ResponseEntity.ok(Map.of("status", "not_found"));
        }

        try {
            Process p = new ProcessBuilder(
                    "cmd.exe", "/c", "claude", "mcp", "add", "tower-analyzer",
                    "--", nodeExe.toString(), serverJs.toString()
            ).redirectErrorStream(true).start();

            String output = new String(p.getInputStream().readAllBytes());
            int exit = p.waitFor();

            if (exit == 0) {
                return ResponseEntity.ok(Map.of("status", "ok"));
            }
            return ResponseEntity.ok(Map.of("status", "error", "message", output.isBlank()
                    ? "claude mcp add exited with code " + exit
                    : output.strip()));
        } catch (IOException e) {
            return ResponseEntity.ok(Map.of("status", "claude_not_found"));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", "Interrupted."));
        }
    }

    // jpackage layout: <install>/app/<jar>  →  <install>/mcp
    private Path getMcpDir() throws URISyntaxException {
        Path codeSource = Path.of(SetupController.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        Path base = Files.isDirectory(codeSource) ? codeSource : codeSource.getParent();
        return base.getParent().resolve("mcp");
    }

    record ConfigRequest(String backupFolderId, String battleReportsFolderId, String playerTrackerSheetId) {}
}
