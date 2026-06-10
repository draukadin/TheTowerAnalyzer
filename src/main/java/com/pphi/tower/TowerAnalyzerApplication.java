package com.pphi.tower;

import com.pphi.tower.analyzers.BattleDiagnostic;
import com.pphi.tower.analyzers.RunComparison;
import com.pphi.tower.config.DriveProperties;
import com.pphi.tower.config.SheetProperties;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.parser.BattleHistoryParser;
import com.pphi.tower.reporter.ReflectionBattleComparisonReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
public class TowerAnalyzerApplication {

    private static final Logger log = LoggerFactory.getLogger(TowerAnalyzerApplication.class);

    public static void main(String[] args) throws IOException {
        installBundledDatabaseIfAbsent();
        installUserPropertiesIfAbsent();
        String version = TowerAnalyzerApplication.class.getPackage().getImplementationVersion();
        log.info("Starting TheTowerAnalyzer version {}", version != null ? version : "unknown (dev build)");
        SpringApplication app = new SpringApplication(TowerAnalyzerApplication.class);
        if (args.length > 0) {
            app.setWebApplicationType(WebApplicationType.NONE);
        }
        app.run(args);
    }

    /**
     * Copies the bundled analyzer.db from the classpath to %APPDATA%\TheTowerAnalyzer
     * before Spring (and HikariCP) start up. This must happen before any datasource
     * bean is initialized, so it cannot live in a Spring component.
     */
    private static void installBundledDatabaseIfAbsent() throws IOException {
        String appData = System.getenv("APPDATA");
        Path dir    = Path.of(appData, "TheTowerAnalyzer");
        Path dbFile = dir.resolve("analyzer.db");
        Files.createDirectories(dir);
        if (!Files.exists(dbFile)) {
            try (InputStream bundled = TowerAnalyzerApplication.class.getResourceAsStream("/analyzer.db")) {
                if (bundled != null) {
                    log.info("First run detected — copying bundled database to {}", dbFile);
                    Files.copy(bundled, dbFile, StandardCopyOption.REPLACE_EXISTING);
                    log.info("Bundled database installed successfully.");
                } else {
                    log.info("First run detected — no bundled database found, seeding from scratch.");
                }
            }
        }
    }

    private static void installUserPropertiesIfAbsent() throws IOException {
        String appData = System.getenv("APPDATA");
        Path dir   = Path.of(appData, "TheTowerAnalyzer");
        Path props = dir.resolve("user.properties");
        Files.createDirectories(dir);
        if (!Files.exists(props)) {
            log.info("First run detected — creating user.properties template at {}", props);
            String fwdDir = dir.toString().replace('\\', '/');
            String content = String.join(System.lineSeparator(),
                "# Google Drive / Sheets OAuth 2.0 client secret",
                "# oauth-credentials.json = OAuth 2.0 client secret (used by both Google Drive and Sheets)",
                "drive.oauth-credentials-file=" + fwdDir + "/oauth-credentials.json",
                "drive.tokens-dir=" + fwdDir + "/tokens",
                "drive.application-name=TheTowerAnalyzer",
                "",
                "# Google Drive folder IDs - replace with your own values",
                "drive.backup-folder-id=REPLACE_WITH_YOUR_BACKUP_FOLDER_ID",
                "drive.battle-reports-folder-id=REPLACE_WITH_YOUR_BATTLE_REPORTS_FOLDER_ID",
                "",
                "# Google Sheets sheet IDs - replace with your own values",
                "sheets.ids.player-tracker=REPLACE_WITH_YOUR_PLAYER_TRACKER_SHEET_ID",
                "",
                "# Optional: change the port if 8080 is already in use on your machine",
                "# server.port=8080"
            );
            Files.writeString(props, content);
            log.info("user.properties created. Edit {} and replace all REPLACE_WITH_... placeholders before restarting.", props);
        }
    }

    @EventListener(WebServerInitializedEvent.class)
    public void openBrowser(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        String url = "http://localhost:" + port;
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI.create(url));
            } else {
                // Fallback for headless/non-Desktop environments (e.g. --win-console mode)
                new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", url).start();
            }
        } catch (Exception e) {
            log.warn("Could not open browser automatically: {}", e.getMessage());
        }
    }

    @Bean
    public ApplicationRunner startupConfigLogger(DriveProperties drive, SheetProperties sheet) {
        return args -> {
            String userPropsPath = System.getenv("APPDATA") + "\\TheTowerAnalyzer\\user.properties";
            log.info("Loading user config from: {}", userPropsPath);
            log.info("drive.oauth-credentials-file   = {}", drive.getOauthCredentialsFile());
            log.info("drive.tokens-dir               = {}", drive.getTokensDir());
            log.info("drive.application-name         = {}", drive.getApplicationName());
            log.info("drive.battle-reports-folder-id = {}", drive.getBattleReportsFolderId() != null ? drive.getBattleReportsFolderId() : "(NOT SET)");
            log.info("drive.backup-folder-id         = {}", drive.getBackupFolderId() != null ? drive.getBackupFolderId() : "(NOT SET)");
            log.info("sheets.ids.player-tracker      = {}", sheet.getIds().get("player-tracker") != null ? sheet.resolve("player-tracker") : "(NOT SET)");
        };
    }
}
