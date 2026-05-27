package com.pphi.tower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "gemini")
public class GeminiProperties {

    private String apiKey;
    private String model = "gemini-2.5-flash";
    private String activePrompt = "analyst";
    private Map<String, String> prompts = new LinkedHashMap<>();
    private String activeModel = "flash";
    private Map<String, String> models = new LinkedHashMap<>();

    /**
     * Resolves the system prompt to use. Priority: requestedKey > activePrompt > first prompt > null.
     */
    public String resolvePrompt(String requestedKey) {
        if (requestedKey != null && !requestedKey.isBlank() && prompts.containsKey(requestedKey)) {
            return prompts.get(requestedKey);
        }
        if (activePrompt != null && prompts.containsKey(activePrompt)) {
            return prompts.get(activePrompt);
        }
        return prompts.values().stream().findFirst().orElse(null);
    }

    /**
     * Resolves the model ID to use. Priority: requestedKey > activeModel > models first entry > model fallback.
     */
    public String resolveModel(String requestedKey) {
        if (requestedKey != null && !requestedKey.isBlank() && models.containsKey(requestedKey)) {
            return models.get(requestedKey);
        }
        if (activeModel != null && models.containsKey(activeModel)) {
            return models.get(activeModel);
        }
        return models.values().stream().findFirst().orElse(model);
    }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String v) { this.apiKey = v; }

    public String getModel() { return model; }
    public void setModel(String v) { this.model = v; }

    public String getActivePrompt() { return activePrompt; }
    public void setActivePrompt(String v) { this.activePrompt = v; }

    public Map<String, String> getPrompts() { return prompts; }
    public void setPrompts(Map<String, String> v) { this.prompts = v; }

    public String getActiveModel() { return activeModel; }
    public void setActiveModel(String v) { this.activeModel = v; }

    public Map<String, String> getModels() { return models; }
    public void setModels(Map<String, String> v) { this.models = v; }
}
