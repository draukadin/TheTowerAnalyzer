package com.pphi.tower.repository;

import com.pphi.tower.config.ClaudeProperties;
import com.pphi.tower.web.dto.ConversationTurn;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ClaudeRepository {

    private static final String BASE_URL = "https://api.anthropic.com";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    private final RestClient restClient;
    private final ClaudeProperties properties;

    public ClaudeRepository(ClaudeProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public String sendChat(String systemPrompt, String userMessage,
                           List<ConversationTurn> history, List<ConversationTurn> preamble) {
        List<Map<String, Object>> messages = new ArrayList<>();

        if (preamble != null) {
            for (ConversationTurn turn : preamble) {
                messages.add(Map.of("role", toClaudeRole(turn.role()), "content", turn.text()));
            }
        }

        if (history != null) {
            for (ConversationTurn turn : history) {
                messages.add(Map.of("role", toClaudeRole(turn.role()), "content", turn.text()));
            }
        }

        messages.add(Map.of("role", "user", "content", userMessage));

        var body = Map.of(
                "model", properties.getModel(),
                "system", systemPrompt,
                "messages", messages,
                "max_tokens", 8192
        );

        ClaudeResponse response = restClient.post()
                .uri("/v1/messages")
                .header("x-api-key", properties.getApiKey())
                .header("anthropic-version", ANTHROPIC_VERSION)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(ClaudeResponse.class);

        if (response == null || response.content() == null || response.content().isEmpty()) {
            throw new RuntimeException("Empty response from Claude API");
        }
        return response.content().get(0).text();
    }

    private String toClaudeRole(String role) {
        return switch (role) {
            case "context", "user"              -> "user";
            case "context_ack", "model", "assistant" -> "assistant";
            default                             -> role;
        };
    }

    record ClaudeResponse(List<ContentBlock> content) {}
    record ContentBlock(String type, String text) {}
}
