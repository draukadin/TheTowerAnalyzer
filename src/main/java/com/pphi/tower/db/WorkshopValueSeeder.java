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
        if (count == null || count == 0) {
            log.info("Seeding WorkshopItemLevelValue...");
            seedFile("workshop_values.json", false);
            seedFile("enhancement_values.json", true);
            log.info("Finished seeding WorkshopItemLevelValue");
            return;
        }

        // v28.3 raised enhancement caps. Re-run the idempotent INSERT OR IGNORE for enhancement values
        // when a raised-cap sentinel row is missing so existing databases pick up the new levels.
        Integer enhMissing = jdbc.queryForObject(
                "SELECT COUNT(*) FROM workshop_item wi WHERE wi.is_plus = 1 AND wi.name = 'Damage +'" +
                " AND NOT EXISTS (SELECT 1 FROM workshop_item_level_value wlv WHERE wlv.workshop_item_id = wi.id AND wlv.level = 600)",
                Integer.class);
        if (enhMissing != null && enhMissing > 0) {
            log.info("Migrating WorkshopItemLevelValue (v28.3 enhancement caps)...");
            seedFile("enhancement_values.json", true);
            log.info("Finished migrating WorkshopItemLevelValue");
        }
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
