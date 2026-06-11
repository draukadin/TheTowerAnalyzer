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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        Path mcpDir = getMcpDir();

        Path nodeExe  = mcpDir.resolve("node.exe");
        Path serverJs = mcpDir.resolve("server.js");

        if (!Files.exists(nodeExe) || !Files.exists(serverJs)) {
            return ResponseEntity.ok(Map.of("status", "not_found"));
        }

        // Try Claude Code CLI first, then fall back to Claude Desktop App config file
        try {
            String claudeCli = resolveClaudePath();
            if (claudeCli != null) {
                return registerViaCli(claudeCli, nodeExe, serverJs);
            }
        } catch (IOException e) {
            // CLI not available — fall through to Desktop config
        }

        Path desktopConfig = resolveDesktopConfigPath();
        if (desktopConfig != null) {
            return registerViaDesktopConfig(desktopConfig, nodeExe, serverJs);
        }

        return ResponseEntity.ok(Map.of("status", "claude_not_found"));
    }

    // jpackage bundles the JRE at <install>/runtime, so java.home -> <install>/mcp
    private Path getMcpDir() {
        return Path.of(System.getProperty("java.home")).getParent().resolve("mcp");
    }

    // Resolve the claude CLI path. Checks PATH via where.exe (exit-code gated) then
    // known install locations, since the jpackage launcher may not inherit full user PATH.
    private String resolveClaudePath() throws IOException {
        try {
            Process where = new ProcessBuilder("where.exe", "claude")
                    .redirectErrorStream(true).start();
            String result = new String(where.getInputStream().readAllBytes()).trim();
            int exit = where.waitFor();
            if (exit == 0 && !result.isBlank()) {
                return result.lines().findFirst().orElse(null);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String appData   = System.getenv("APPDATA");
        String localData = System.getenv("LOCALAPPDATA");
        List<Path> candidates = new ArrayList<>();
        if (appData != null) {
            candidates.add(Path.of(appData, "npm", "claude.cmd"));
            candidates.add(Path.of(appData, "npm", "claude"));
        }
        if (localData != null) {
            candidates.add(Path.of(localData, "Programs", "claude", "claude.exe"));
        }
        for (Path candidate : candidates) {
            if (Files.exists(candidate)) return candidate.toString();
        }
        return null;
    }

    // Locate the Claude Desktop App config file. Checks the standard Electron path first,
    // then MSIX/Windows Store virtualized AppData (AnthropicPBC.Claude_* package family).
    private Path resolveDesktopConfigPath() {
        String appData   = System.getenv("APPDATA");
        String localData = System.getenv("LOCALAPPDATA");

        if (appData != null) {
            Path standard = Path.of(appData, "Claude");
            if (Files.exists(standard)) {
                return standard.resolve("claude_desktop_config.json");
            }
        }

        // MSIX/Windows Store: %LOCALAPPDATA%\Packages\Claude_<id>\LocalCache\Roaming\Claude
        if (localData != null) {
            Path packages = Path.of(localData, "Packages");
            if (Files.exists(packages)) {
                try {
                    return Files.list(packages)
                            .filter(p -> p.getFileName().toString().toLowerCase().contains("claude"))
                            .map(p -> p.resolve("LocalCache").resolve("Roaming").resolve("Claude"))
                            .filter(Files::exists)
                            .findFirst()
                            .map(p -> p.resolve("claude_desktop_config.json"))
                            .orElse(null);
                } catch (IOException ignored) {}
            }
        }
        return null;
    }

    private ResponseEntity<Map<String, String>> registerViaCli(String claudePath, Path nodeExe, Path serverJs) {
        try {
            Process p = new ProcessBuilder(
                    claudePath, "mcp", "add", "tower-analyzer",
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
            return ResponseEntity.ok(Map.of("status", "claude_not_found", "message", "tried: " + claudePath));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "error", "message", "Interrupted."));
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Map<String, String>> registerViaDesktopConfig(Path configFile, Path nodeExe, Path serverJs) {
        try {
            Map<String, Object> config;
            if (Files.exists(configFile)) {
                config = new HashMap<>(objectMapper.readValue(configFile.toFile(), Map.class));
            } else {
                config = new HashMap<>();
            }

            Map<String, Object> mcpServers = new HashMap<>(
                    (Map<String, Object>) config.getOrDefault("mcpServers", new HashMap<>()));
            mcpServers.put("tower-analyzer", Map.of(
                    "command", nodeExe.toString(),
                    "args", List.of(serverJs.toString())
            ));
            config.put("mcpServers", mcpServers);

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile.toFile(), config);
            return ResponseEntity.ok(Map.of("status", "ok"));
        } catch (IOException e) {
            return ResponseEntity.ok(Map.of("status", "error", "message", "Could not write Claude Desktop config: " + e.getMessage()));
        }
    }

    record ConfigRequest(String backupFolderId, String battleReportsFolderId, String playerTrackerSheetId) {}
}
