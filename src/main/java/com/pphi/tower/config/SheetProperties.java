package com.pphi.tower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "sheets")
public class SheetProperties {

    private Map<String, String> ids = Map.of();

    public String resolve(String logicalKey) {
        String id = ids.get(logicalKey);
        if (id == null) {
            throw new IllegalStateException("No sheet ID configured for key: " + logicalKey);
        }
        return id;
    }

    public Map<String, String> getIds() { return ids; }
    public void setIds(Map<String, String> ids) { this.ids = ids; }
}
