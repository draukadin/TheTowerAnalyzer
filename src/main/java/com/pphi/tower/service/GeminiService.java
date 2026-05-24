package com.pphi.tower.service;

import com.pphi.tower.repository.GeminiRepository;
import com.pphi.tower.repository.UserProfileRepository;
import com.pphi.tower.service.context.ChatContext;
import com.pphi.tower.service.context.ComparisonReportContext;
import com.pphi.tower.service.context.UserProfileContext;
import com.pphi.tower.web.dto.ChatRequest;
import com.pphi.tower.web.dto.ChatResponse;
import org.springframework.stereotype.Service;

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

    public GeminiService(GeminiRepository geminiRepository,
                         ComparisonService comparisonService,
                         UserProfileRepository userProfileRepository) {
        this.geminiRepository = geminiRepository;
        this.comparisonService = comparisonService;
        this.userProfileRepository = userProfileRepository;
    }

    public ChatResponse chat(ChatRequest request) {
        List<ChatContext> contexts = buildContexts(request);
        String systemPrompt = assembleSystemPrompt(contexts);
        String reply = geminiRepository.sendChat(systemPrompt, request.prompt(), request.history());
        return new ChatResponse(reply);
    }

    private List<ChatContext> buildContexts(ChatRequest request) {
        List<ChatContext> contexts = new ArrayList<>();
        if (request.contextTypes() == null) return contexts;

        for (String type : request.contextTypes()) {
            if ("comparison_report".equals(type)
                    && request.reportId1() != null
                    && request.reportId2() != null) {
                var result = comparisonService.compare(request.reportId1(), request.reportId2());
                contexts.add(new ComparisonReportContext(result, request.reportId1(), request.reportId2()));
            } else if ("user_profile".equals(type)) {
                contexts.add(new UserProfileContext(userProfileRepository.load()));
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
