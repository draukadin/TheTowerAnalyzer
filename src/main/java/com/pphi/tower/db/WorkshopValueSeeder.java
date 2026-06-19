package com.pphi.tower.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Map;

@Component
public class WorkshopValueSeeder {

    private static final Logger log = LoggerFactory.getLogger(WorkshopValueSeeder.class);

    private final JdbcTemplate jdbc;

    public WorkshopValueSeeder(JdbcTemplate jdbc, DatabaseInitializer init, WorkshopSeeder workshopSeeder) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM workshop_item_level_value", Integer.class);
        if (count != null && count > 0) return;

        log.info("Seeding WorkshopItemLevelValue...");
        seedFile("workshop_values.json", false);
        seedFile("enhancement_values.json", true);
        log.info("Finished seeding WorkshopItemLevelValue");
    }

    private void seedFile(String resourceName, boolean isPlus) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) throw new IllegalStateException("Resource not found: " + resourceName);
            Map<String, Map<String, Double>> data =
                    new ObjectMapper().readValue(is, new TypeReference<>() {});

            for (Map.Entry<String, Map<String, Double>> itemEntry : data.entrySet()) {
                String name = itemEntry.getKey();
                Long itemId = jdbc.queryForObject(
                        "SELECT id FROM workshop_item WHERE name = ? AND is_plus = ?",
                        Long.class, name, isPlus ? 1 : 0);
                if (itemId == null) {
                    log.warn("Workshop item not found: '{}' (is_plus={})", name, isPlus);
                    continue;
                }
                for (Map.Entry<String, Double> levelEntry : itemEntry.getValue().entrySet()) {
                    int level = Integer.parseInt(levelEntry.getKey());
                    double value = levelEntry.getValue();
                    jdbc.update(
                            "INSERT OR IGNORE INTO workshop_item_level_value (workshop_item_id, level, value) VALUES (?, ?, ?)",
                            itemId, level, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed from " + resourceName, e);
        }
    }
}
