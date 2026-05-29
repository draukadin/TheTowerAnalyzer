package com.pphi.tower;

import com.pphi.tower.analyzers.BattleDiagnostic;
import com.pphi.tower.analyzers.RunComparison;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.parser.BattleHistoryParser;
import com.pphi.tower.reporter.ReflectionBattleComparisonReporter;
import com.pphi.tower.service.ContextExportService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
public class TowerAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TowerAnalyzerApplication.class);
        // If any CLI arguments are supplied, run headless (no web server).
        // This covers --export-context, report paths, etc.
        if (args.length > 0) {
            app.setWebApplicationType(WebApplicationType.NONE);
        }
        app.run(args);
    }

    /**
     * Preserves the original CLI behaviour when report paths are passed as arguments.
     * When no args are supplied the app starts normally as a web server.
     */
    @Bean
    public CommandLineRunner cliRunner(ContextExportService contextExportService) {
        return args -> {
            if (args.length == 0) return;  // web server mode

            if (Arrays.asList(args).contains("--export-context")) {
                System.out.println("Fetching player data from Google Sheets...");
                Path output = contextExportService.exportToDocuments();
                System.out.println("Context exported to: " + output.toAbsolutePath());
                return;
            }

            BattleHistoryParser parser    = new BattleHistoryParser();
            BattleDiagnostic    diagnostic = new BattleDiagnostic();
            RunComparison       comparison = new RunComparison(parser);

            BattleHistory reportOne = parser.parse(Path.of(args[0]));
            DiagnosisResult result  = diagnostic.analyzeReport(reportOne);
            System.out.println(result);

            if (args.length >= 2) {
                BattleHistory reportTwo      = parser.parse(Path.of(args[1]));
                List<BattleHistory> results  = comparison.compareBattles(reportOne, reportTwo);
                new ReflectionBattleComparisonReporter().printReport(results);
            }
        };
    }
}
