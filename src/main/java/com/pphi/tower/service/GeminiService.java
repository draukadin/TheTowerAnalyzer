package com.pphi.tower.service;

import com.pphi.tower.config.GeminiProperties;
import com.pphi.tower.model.sheets.modules.Preset;
import com.pphi.tower.repository.GeminiRepository;
import com.pphi.tower.repository.UserProfileRepository;
import com.pphi.tower.service.context.*;
import com.pphi.tower.web.dto.ChatRequest;
import com.pphi.tower.web.dto.ChatResponse;
import org.springframework.stereotype.Service;

import com.pphi.tower.web.dto.ConversationTurn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiService {

    private static final String FALLBACK_SYSTEM_PROMPT =
            "You are an expert analyst for The Tower: Tower Defense Game. " +
            "You help players understand their battle statistics, compare run performance, " +
            "and provide actionable advice to improve their gameplay. " +
            "Be concise, specific, and refer to the provided data when relevant.";

    private final GeminiRepository geminiRepository;
    private final GeminiProperties geminiProperties;
    private final ComparisonService comparisonService;
    private final UserProfileRepository userProfileRepository;
    private final TowerTrackerFetcherService towerTrackerFetcherService;

    public GeminiService(GeminiRepository geminiRepository,
                         GeminiProperties geminiProperties,
                         ComparisonService comparisonService,
                         UserProfileRepository userProfileRepository,
                         TowerTrackerFetcherService towerTrackerFetcherService) {
        this.geminiRepository = geminiRepository;
        this.geminiProperties = geminiProperties;
        this.comparisonService = comparisonService;
        this.userProfileRepository = userProfileRepository;
        this.towerTrackerFetcherService = towerTrackerFetcherService;
    }

    public ChatResponse chat(ChatRequest request) {
        try {
            // Build context preamble only on the very first turn (empty history).
            // After that it lives in the conversation history sent back by the client,
            // so there is no need to re-fetch or re-transmit it.
            boolean isFirstTurn = request.history() == null || request.history().isEmpty();
            List<ConversationTurn> preamble = List.of();

            if (isFirstTurn) {
                List<ChatContext> contexts = buildContexts(request);
                if (!contexts.isEmpty()) {
                    String contextText = assembleContextText(contexts);
                    preamble = List.of(
                            new ConversationTurn("context", contextText),
                            new ConversationTurn("context_ack",
                                    "Context received. I have reviewed your player data and am ready to assist.")
                    );
                }
            }

            String systemPrompt = geminiProperties.resolvePrompt(request.promptKey());
            if (systemPrompt == null) systemPrompt = FALLBACK_SYSTEM_PROMPT;

            String reply = geminiRepository.sendChat(systemPrompt, request.prompt(), request.history(), preamble);
            return new ChatResponse(reply, preamble);
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
                case "tier_wave"         -> contexts.add(new TierWaveContext(towerTrackerFetcherService.fetchTierWave()));
                case "version_history"   -> contexts.add(new VersionHistoryContext(towerTrackerFetcherService.fetchVersionHistory()));
            }
        }
        return contexts;
    }

    private String assembleContextText(List<ChatContext> contexts) {
        StringBuilder sb = new StringBuilder("You have been provided the following player data:\n\n");
        for (ChatContext ctx : contexts) {
            sb.append("### ").append(ctx.getLabel()).append(" ###\n\n");
            sb.append(ctx.getContent()).append("\n");
        }
        return sb.toString();
    }
}
