package com.pphi.tower;

import com.pphi.tower.analyzers.BattleDiagnostic;
import com.pphi.tower.analyzers.RunComparison;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.parser.BattleHistoryParser;
import com.pphi.tower.reporter.ReflectionBattleComparisonReporter;
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
        if (args.length > 0) {
            app.setWebApplicationType(WebApplicationType.NONE);
        }
        app.run(args);
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
