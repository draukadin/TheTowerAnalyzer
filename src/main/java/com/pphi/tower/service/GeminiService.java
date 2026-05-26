package com.pphi.tower.service;

import com.pphi.tower.model.sheets.modules.Preset;
import com.pphi.tower.repository.GeminiRepository;
import com.pphi.tower.repository.UserProfileRepository;
import com.pphi.tower.service.context.*;
import com.pphi.tower.web.dto.ChatRequest;
import com.pphi.tower.web.dto.ChatResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiService {

    private static final String SYSTEM_PROMPT_BASE =
            "You are an expert analyst for The Tower: Tower Defense Game. " +
            "You help players understand their battle statistics, compare run performance, " +
            "and provide actionable advice to improve their gameplay. " +
            "Be concise, specific, and refer to the provided data when relevant.";

    private final GeminiRepository geminiRepository;
    private final ComparisonService comparisonService;
    private final UserProfileRepository userProfileRepository;
    private final TowerTrackerFetcherService towerTrackerFetcherService;

    public GeminiService(GeminiRepository geminiRepository,
                         ComparisonService comparisonService,
                         UserProfileRepository userProfileRepository,
                         TowerTrackerFetcherService towerTrackerFetcherService) {
        this.geminiRepository = geminiRepository;
        this.comparisonService = comparisonService;
        this.userProfileRepository = userProfileRepository;
        this.towerTrackerFetcherService = towerTrackerFetcherService;
    }

    public ChatResponse chat(ChatRequest request) {
        try {
            List<ChatContext> contexts = buildContexts(request);
            String systemPrompt = assembleSystemPrompt(contexts);
            String reply = geminiRepository.sendChat(systemPrompt, request.prompt(), request.history());
            return new ChatResponse(reply);
        } catch (IOException e) {
            throw new RuntimeException("Failed to build chat context from tracker sheets", e);
        }
    }

    private List<ChatContext> buildContexts(ChatRequest request) throws IOException {
        List<ChatContext> contexts = new ArrayList<>();
        if (request.contextTypes() == null) return contexts;

        for (String type : request.contextTypes()) {
            switch (type) {
                case "comparison_report" -> {
                    if (request.reportId1() != null && request.reportId2() != null) {
                        var result = comparisonService.compare(request.reportId1(), request.reportId2());
                        contexts.add(new ComparisonReportContext(result, request.reportId1(), request.reportId2()));
                    }
                }
                case "user_profile"      -> contexts.add(new UserProfileContext(userProfileRepository.load()));
                case "player_currencies" -> contexts.add(new PlayerCurrenciesContext(towerTrackerFetcherService.fetchCurrencies()));
                case "labs"              -> contexts.add(new LabsContext(towerTrackerFetcherService.fetchLabs()));
                case "ultimate_weapons"  -> contexts.add(new UltimateWeaponsContext(towerTrackerFetcherService.fetchUltimateWeapons()));
                case "modules"           -> contexts.add(new ModulesContext(
                                                towerTrackerFetcherService.fetchModuleInventory(),
                                                towerTrackerFetcherService.fetchModuleSubStats()));
                case "modules_farming"   -> contexts.add(new ModulePresetContext(
                                                Preset.FARMING,
                                                towerTrackerFetcherService.fetchModulePreset(Preset.FARMING)));
                case "modules_tournament"-> contexts.add(new ModulePresetContext(
                                                Preset.TOURNAMENT,
                                                towerTrackerFetcherService.fetchModulePreset(Preset.TOURNAMENT)));
                case "workshop"          -> contexts.add(new WorkshopContext(towerTrackerFetcherService.fetchWorkshop()));
                case "guardians"         -> contexts.add(new GuardiansContext(towerTrackerFetcherService.fetchGuardians()));
                case "bots"              -> contexts.add(new BotsContext(towerTrackerFetcherService.fetchBots()));
                case "relics"            -> contexts.add(new RelicsContext(towerTrackerFetcherService.fetchRelics()));
            }
        }
        return contexts;
    }

    private String assembleSystemPrompt(List<ChatContext> contexts) {
        if (contexts.isEmpty()) return SYSTEM_PROMPT_BASE;

        StringBuilder sb = new StringBuilder(SYSTEM_PROMPT_BASE);
        sb.append("\n\nYou have been provided the following context:\n\n");
        for (ChatContext ctx : contexts) {
            sb.append("### ").append(ctx.getLabel()).append(" ###\n");
            sb.append(ctx.getContent()).append("\n");
        }
        return sb.toString();
    }
}
