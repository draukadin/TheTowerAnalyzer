package com.pphi.tower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "claude")
public class ClaudeProperties {

    private String apiKey;
    private String model = "claude-sonnet-4-6";
    private String activePrompt = "analyst";
    private Map<String, String> prompts = new LinkedHashMap<>();

    public String resolvePrompt(String requestedKey) {
        if (requestedKey != null && !requestedKey.isBlank() && prompts.containsKey(requestedKey)) {
            return prompts.get(requestedKey);
        }
        if (activePrompt != null && prompts.containsKey(activePrompt)) {
            return prompts.get(activePrompt);
        }
        return prompts.values().stream().findFirst().orElse(null);
    }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String v) { this.apiKey = v; }

    public String getModel() { return model; }
    public void setModel(String v) { this.model = v; }

    public String getActivePrompt() { return activePrompt; }
    public void setActivePrompt(String v) { this.activePrompt = v; }

    public Map<String, String> getPrompts() { return prompts; }
    public void setPrompts(Map<String, String> v) { this.prompts = v; }
}
