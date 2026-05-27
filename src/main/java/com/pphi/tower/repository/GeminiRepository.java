package com.pphi.tower.repository;

import com.pphi.tower.config.GeminiProperties;
import com.pphi.tower.web.dto.ConversationTurn;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GeminiRepository {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com";

    private final RestClient restClient;
    private final GeminiProperties properties;

    public GeminiRepository(GeminiProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public ChatResult sendChat(String systemPrompt, String modelId, String thinkingLevel,
                               String userMessage, List<ConversationTurn> history,
                               List<ConversationTurn> preamble) {
        List<Map<String, Object>> contents = new ArrayList<>();

        // Inject context preamble at the very start (only populated on the first turn)
        if (preamble != null) {
            for (ConversationTurn turn : preamble) {
                contents.add(Map.of(
                        "role", toGeminiRole(turn.role()),
                        "parts", List.of(Map.of("text", turn.text()))
                ));
            }
        }

        if (history != null) {
            for (ConversationTurn turn : history) {
                List<Map<String, Object>> parts = new ArrayList<>();
                parts.add(Map.of("text", turn.text()));
                if (turn.thoughtSignature() != null && !turn.thoughtSignature().isBlank()) {
                    parts.add(Map.of("thoughtSignature", turn.thoughtSignature()));
                }
                contents.add(Map.of(
                        "role", toGeminiRole(turn.role()),
                        "parts", parts
                ));
            }
        }

        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userMessage))
        ));

        Map<String, Object> genConfig = new HashMap<>();
        genConfig.put("temperature", 0.7);
        genConfig.put("maxOutputTokens", 8192);
        if (thinkingLevel != null && !thinkingLevel.isBlank()) {
            genConfig.put("thinkingConfig", Map.of("thinkingLevel", thinkingLevel));
        }

        Map<String, Object> body = new HashMap<>();
        body.put("system_instruction", Map.of("parts", List.of(Map.of("text", systemPrompt))));
        body.put("contents", contents);
        body.put("generationConfig", genConfig);

        GeminiResponse response = restClient.post()
                .uri("/v1beta/models/{model}:generateContent?key={key}", modelId, properties.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(GeminiResponse.class);

        if (response == null || response.candidates() == null || response.candidates().isEmpty()) {
            throw new RuntimeException("Empty response from Gemini API");
        }

        List<Part> parts = response.candidates().get(0).content().parts();
        String text = parts.stream()
                .filter(p -> p.text() != null)
                .map(Part::text)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No text part in Gemini response"));
        String sig = parts.stream()
                .filter(p -> p.thoughtSignature() != null)
                .map(Part::thoughtSignature)
                .findFirst()
                .orElse(null);

        return new ChatResult(text, sig);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Maps application-level turn roles to the two roles Gemini accepts.
     * "context"     — the preamble user turn (context data)  → "user"
     * "context_ack" — the preamble model acknowledgement     → "model"
     */
    private String toGeminiRole(String role) {
        return switch (role) {
            case "context", "user"        -> "user";
            case "context_ack", "model"   -> "model";
            default                       -> role;
        };
    }

    // -------------------------------------------------------------------------
    // Result type
    // -------------------------------------------------------------------------

    public record ChatResult(String text, String thoughtSignature) {}

    // -------------------------------------------------------------------------
    // Gemini API response model
    // -------------------------------------------------------------------------

    record GeminiResponse(List<Candidate> candidates) {}
    record Candidate(Content content) {}
    record Content(List<Part> parts) {}
    record Part(String text, String thoughtSignature) {}
}
