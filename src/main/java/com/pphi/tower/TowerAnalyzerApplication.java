package com.pphi.tower;

import com.pphi.tower.analyzers.BattleDiagnostic;
import com.pphi.tower.analyzers.RunComparison;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.parser.BattleHistoryParser;
import com.pphi.tower.reporter.ReflectionBattleComparisonReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
public class TowerAnalyzerApplication {

    private static final Logger log = LoggerFactory.getLogger(TowerAnalyzerApplication.class);

    public static void main(String[] args) throws IOException {
        installBundledDatabaseIfAbsent();
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

    @Bean
    public CommandLineRunner cliRunner() {
        return args -> {
            if (args.length == 0) return;

            List<String> argList = Arrays.asList(args);
            BattleHistoryParser parser    = new BattleHistoryParser();
            BattleDiagnostic    diagnostic = new BattleDiagnostic();
            RunComparison       comparison = new RunComparison(parser);

            String reportPath = argList.stream()
                    .filter(a -> !a.startsWith("--"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("No report file path provided"));

            BattleHistory reportOne = parser.parse(Path.of(reportPath));
            DiagnosisResult result  = diagnostic.analyzeReport(reportOne);
            System.out.println(result);

            List<String> reportPaths = argList.stream().filter(a -> !a.startsWith("--")).toList();
            if (reportPaths.size() >= 2) {
                BattleHistory reportTwo      = parser.parse(Path.of(reportPaths.get(1)));
                List<BattleHistory> results  = comparison.compareBattles(reportOne, reportTwo);
                new ReflectionBattleComparisonReporter().printReport(results);
            }
        };
    }
}
