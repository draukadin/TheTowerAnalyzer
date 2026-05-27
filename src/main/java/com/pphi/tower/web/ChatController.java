package com.pphi.tower.web;

import com.pphi.tower.config.GeminiProperties;
import com.pphi.tower.repository.ChatHistoryRepository;
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
    private final ChatHistoryRepository chatHistoryRepository;
    private final GeminiProperties geminiProperties;

    public ChatController(GeminiService geminiService,
                          ChatHistoryRepository chatHistoryRepository,
                          GeminiProperties geminiProperties) {
        this.geminiService = geminiService;
        this.chatHistoryRepository = chatHistoryRepository;
        this.geminiProperties = geminiProperties;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        ChatResponse response = geminiService.chat(request);
        if (request.reportId1() != null) {
            String id2 = request.reportId2() != null ? request.reportId2() : "";
            // Persist context preamble turns first so they're included in future history loads
            for (ConversationTurn turn : response.prependedTurns()) {
                chatHistoryRepository.save(request.reportId1(), id2, turn.role(), turn.text());
            }
            chatHistoryRepository.save(request.reportId1(), id2, "user", request.prompt());
            chatHistoryRepository.save(request.reportId1(), id2, "model", response.reply());
        }
        return response;
    }

    @GetMapping("/prompts")
    public Map<String, Object> prompts() {
        return Map.of(
                "keys", new ArrayList<>(geminiProperties.getPrompts().keySet()),
                "active", geminiProperties.getActivePrompt()
        );
    }

    @GetMapping("/history")
    public List<ConversationTurn> history(
            @RequestParam String reportId1,
            @RequestParam(required = false, defaultValue = "") String reportId2) {
        return chatHistoryRepository.findByPair(reportId1, reportId2);
    }

    @DeleteMapping("/history")
    public void clearHistory(
            @RequestParam String reportId1,
            @RequestParam(required = false, defaultValue = "") String reportId2) {
        chatHistoryRepository.deleteByPair(reportId1, reportId2);
    }
}
