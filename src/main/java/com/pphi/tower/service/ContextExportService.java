package com.pphi.tower.service;

import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlediagnostics.Observation;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.model.sheets.cards.CardPresetType;
import com.pphi.tower.service.context.ComparisonReportContext;
import com.pphi.tower.model.sheets.modules.Preset;
import com.pphi.tower.service.context.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContextExportService {

    private static final Logger log = LoggerFactory.getLogger(ContextExportService.class);
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TowerTrackerFetcherService fetcherService;

    public ContextExportService(TowerTrackerFetcherService fetcherService) {
        this.fetcherService = fetcherService;
    }

    public Path exportDiagnosisToDocuments(DiagnosisResult result, String reportName) throws IOException {
        Path outputDir = Path.of(System.getProperty("user.home"), "Documents", "TowerAnalyzer");
        Files.createDirectories(outputDir);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String baseName = reportName.isBlank() ? "diagnosis" : reportName;
        Path outputFile = outputDir.resolve(baseName + "-" + timestamp + ".md");
        Files.writeString(outputFile, buildDiagnosisMarkdown(result, reportName));
        return outputFile;
    }

    private String buildDiagnosisMarkdown(DiagnosisResult result, String reportName) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        StringBuilder sb = new StringBuilder();
        sb.append("# The Tower — Battle Run Diagnosis\n\n");
        sb.append("_Generated: ").append(timestamp).append("_  \n");
        if (!reportName.isBlank()) {
            sb.append("_Report: ").append(reportName).append("_\n");
        }
        sb.append("\n---\n\n");

        sb.append("## Primary Failure\n\n");
        sb.append("**").append(result.primaryFailure()).append("**");
        sb.append(" — Confidence: **").append(result.confidence()).append("**\n\n");
        sb.append(result.explanation()).append("\n\n");

        sb.append("---\n\n");
        sb.append("## Diagnostic Metrics\n\n");
        sb.append("| Metric | Value |\n");
        sb.append("|--------|-------|\n");
        sb.append("| Swarm Kill Share   | ").append(result.swarmKillShareFormatted()).append(" |\n");
        sb.append("| Heavy Kill Share   | ").append(result.heavyKillShareFormatted()).append(" |\n");
        sb.append("| Block Efficiency   | ").append(result.blockEfficiencyFormatted()).append(" |\n");
        sb.append("| Vampire Density    | ").append(result.vampireDensityFormatted()).append(" |\n");
        sb.append("| Ranged Density     | ").append(result.rangedDensityFormatted()).append(" |\n");
        sb.append(String.format("| Life Steal (raw)   | %.2f |%n", result.lifeStealRaw()));
        sb.append(String.format("| Total Damage Taken | %.2f |%n", result.totalDamageTakenRaw()));
        sb.append("\n");

        if (result.observations() != null && !result.observations().isEmpty()) {
            sb.append("---\n\n");
            sb.append("## Observations\n\n");
            for (Observation obs : result.observations()) {
                sb.append("### ").append(obs.label()).append("\n\n");
                sb.append(obs.detail()).append("\n\n");
            }
        }

        return sb.toString();
    }

    public Path exportComparisonToDocuments(List<BattleHistory> comparisonResult, String id1, String id2) throws IOException {
        Path outputDir = Path.of(System.getProperty("user.home"), "Documents", "TowerAnalyzer");
        Files.createDirectories(outputDir);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        Path outputFile = outputDir.resolve("comparison-" + timestamp + ".md");
        Files.writeString(outputFile, buildComparisonMarkdown(comparisonResult, id1, id2));
        return outputFile;
    }

    private String buildComparisonMarkdown(List<BattleHistory> comparisonResult, String id1, String id2) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        StringBuilder sb = new StringBuilder();
        sb.append("# The Tower — Battle Run Comparison\n\n");
        sb.append("_Generated: ").append(timestamp).append("_  \n");
        sb.append("_Report 1: ").append(id1).append("_  \n");
        sb.append("_Report 2: ").append(id2).append("_\n\n");
        sb.append("---\n\n");
        sb.append("```\n");
        sb.append(new ComparisonReportContext(comparisonResult, id1, id2).getContent());
        sb.append("```\n");
        return sb.toString();
    }

    public Path exportToDocuments() throws IOException {
        Path outputDir = Path.of(System.getProperty("user.home"), "Documents", "TowerAnalyzer");
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve("tower-context.md");
        String markdown = buildMarkdown();
        Files.writeString(outputFile, markdown);
        return outputFile;
    }

    private String buildMarkdown() throws IOException {
        List<ChatContext> contexts = fetchAllContexts();

        StringBuilder sb = new StringBuilder();
        sb.append("# Tower Analyzer — Player Context\n\n");
        sb.append("_Generated: ").append(LocalDateTime.now().format(TIMESTAMP_FMT)).append("_\n\n");
        sb.append("---\n\n");

        for (ChatContext ctx : contexts) {
            sb.append("## ").append(ctx.getLabel()).append("\n\n");
            sb.append(ctx.getContent()).append("\n\n");
            sb.append("---\n\n");
        }

        return sb.toString();
    }

    private List<ChatContext> fetchAllContexts() throws IOException {
        List<ChatContext> contexts = new ArrayList<>();

        log.info("Fetching player currencies...");
        contexts.add(new PlayerCurrenciesContext(fetcherService.fetchCurrencies()));

        log.info("Fetching labs...");
        contexts.add(new LabsContext(fetcherService.fetchLabs()));

        log.info("Fetching Lab Slot planning...");
        contexts.add(new LabPlanningContext(fetcherService.fetchLabPlanning()));

        log.info("Fetching ultimate weapons...");
        contexts.add(new UltimateWeaponsContext(fetcherService.fetchUltimateWeapons()));

        log.info("Fetching module inventory...");
        contexts.add(new ModulesContext(fetcherService.fetchModuleInventory(), fetcherService.fetchModuleSubStats()));

        log.info("Fetching farming module preset...");
        contexts.add(new ModulePresetContext(Preset.FARMING, fetcherService.fetchModulePreset(Preset.FARMING)));

        log.info("Fetching tournament module preset...");
        contexts.add(new ModulePresetContext(Preset.TOURNAMENT, fetcherService.fetchModulePreset(Preset.TOURNAMENT)));

        log.info("Fetching cards...");
        contexts.add(new CardsContext(fetcherService.fetchCards()));

        log.info("Fetching farming card preset...");
        contexts.add(new CardPresetContext(CardPresetType.FARMING, fetcherService.fetchCardPreset(CardPresetType.FARMING)));

        log.info("Fetching tournament card preset...");
        contexts.add(new CardPresetContext(CardPresetType.TOURNAMENT, fetcherService.fetchCardPreset(CardPresetType.TOURNAMENT)));

        log.info("Fetching workshop...");
        contexts.add(new WorkshopContext(fetcherService.fetchWorkshop()));

        log.info("Fetching guardians...");
        contexts.add(new GuardiansContext(fetcherService.fetchGuardians()));

        log.info("Fetching bots...");
        contexts.add(new BotsContext(fetcherService.fetchBots()));

        log.info("Fetching relics...");
        contexts.add(new RelicsContext(fetcherService.fetchRelics()));

        log.info("Fetching tier/wave info...");
        contexts.add(new TierWaveContext(fetcherService.fetchTierWave()));

        log.info("Fetching version history...");
        contexts.add(new VersionHistoryContext(fetcherService.fetchVersionHistory()));

        return contexts;
    }
}
