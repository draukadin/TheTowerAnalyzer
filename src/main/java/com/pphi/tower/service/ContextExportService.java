package com.pphi.tower.service;

import com.pphi.tower.model.sheets.cards.CardPresetType;
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
