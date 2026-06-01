package com.pphi.tower.web;

import com.pphi.tower.config.ClaudeProperties;
import com.pphi.tower.config.GeminiProperties;
import com.pphi.tower.repository.ChatHistoryRepository;
import com.pphi.tower.service.ClaudeService;
import com.pphi.tower.service.GeminiService;
import com.pphi.tower.web.dto.ChatRequest;
import com.pphi.tower.web.dto.ChatResponse;
import com.pphi.tower.web.dto.ConversationTurn;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final GeminiService geminiService;
    private final ClaudeService claudeService;
    private final ChatHistoryRepository chatHistoryRepository;
    private final GeminiProperties geminiProperties;
    private final ClaudeProperties claudeProperties;

    public ChatController(GeminiService geminiService,
                          ClaudeService claudeService,
                          ChatHistoryRepository chatHistoryRepository,
                          GeminiProperties geminiProperties,
                          ClaudeProperties claudeProperties) {
        this.geminiService = geminiService;
        this.claudeService = claudeService;
        this.chatHistoryRepository = chatHistoryRepository;
        this.geminiProperties = geminiProperties;
        this.claudeProperties = claudeProperties;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        ChatResponse response = isClaude(request.provider())
                ? claudeService.chat(request)
                : geminiService.chat(request);

        if (request.reportId1() != null) {
            String id1 = storageId1(request.provider(), request.reportId1());
            String id2 = request.reportId2() != null ? request.reportId2() : "";
            String modelRole = isClaude(request.provider()) ? "assistant" : "model";
            for (ConversationTurn turn : response.prependedTurns()) {
                chatHistoryRepository.save(id1, id2, turn.role(), turn.text());
            }
            chatHistoryRepository.save(id1, id2, "user", request.prompt());
            chatHistoryRepository.save(id1, id2, modelRole, response.reply());
        }
        return response;
    }

    @GetMapping("/prompts")
    public Map<String, Object> prompts(@RequestParam(required = false, defaultValue = "gemini") String provider) {
        if (isClaude(provider)) {
            return Map.of(
                    "keys", new ArrayList<>(claudeProperties.getPrompts().keySet()),
                    "active", claudeProperties.getActivePrompt()
            );
        }
        return Map.of(
                "keys", new ArrayList<>(geminiProperties.getPrompts().keySet()),
                "active", geminiProperties.getActivePrompt()
        );
    }

    @GetMapping("/history")
    public List<ConversationTurn> history(
            @RequestParam String reportId1,
            @RequestParam(required = false, defaultValue = "") String reportId2,
            @RequestParam(required = false, defaultValue = "gemini") String provider) {
        return chatHistoryRepository.findByPair(storageId1(provider, reportId1), reportId2);
    }

    @DeleteMapping("/history")
    public void clearHistory(
            @RequestParam String reportId1,
            @RequestParam(required = false, defaultValue = "") String reportId2,
            @RequestParam(required = false, defaultValue = "gemini") String provider) {
        chatHistoryRepository.deleteByPair(storageId1(provider, reportId1), reportId2);
    }

    private boolean isClaude(String provider) {
        return "claude".equalsIgnoreCase(provider);
    }

    private String storageId1(String provider, String reportId1) {
        return isClaude(provider) ? "claude::" + reportId1 : reportId1;
    }
}
