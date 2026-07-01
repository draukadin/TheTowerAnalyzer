package com.pphi.tower.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WorkshopCostSeeder {

    private static final Logger log = LoggerFactory.getLogger(WorkshopCostSeeder.class);

    private final JdbcTemplate jdbc;

    public WorkshopCostSeeder(JdbcTemplate jdbc, WorkshopSeeder workshopSeeder) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer regularCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM workshop_item_level_cost wlc" +
                " JOIN workshop_item wi ON wi.id = wlc.workshop_item_id WHERE wi.is_plus = 0",
                Integer.class);
        if (regularCount == null || regularCount == 0) {
            log.info("Seeding {}#costs...", this.getClass().getSimpleName().replace("Seeder", ""));
            seedCosts();
            log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
        }

        Integer plusCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM workshop_item_level_cost wlc" +
                " JOIN workshop_item wi ON wi.id = wlc.workshop_item_id WHERE wi.is_plus = 1",
                Integer.class);
        // v28.3 raised enhancement caps. Re-run the idempotent INSERT OR IGNORE batch when a raised-cap
        // sentinel row is missing so existing databases pick up the new levels (see WorkshopSeeder#migrateV28_3).
        Integer plusMissing = jdbc.queryForObject(
                "SELECT COUNT(*) FROM workshop_item wi WHERE wi.is_plus = 1 AND wi.name = 'Damage +'" +
                " AND NOT EXISTS (SELECT 1 FROM workshop_item_level_cost wlc WHERE wlc.workshop_item_id = wi.id AND wlc.level = 600)",
                Integer.class);
        boolean plusNeedsMigration = plusMissing != null && plusMissing > 0;
        if (plusCount == null || plusCount == 0 || plusNeedsMigration) {
            log.info("Seeding {}#plusCosts...", this.getClass().getSimpleName().replace("Seeder", ""));
            seedPlusCosts();
            log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
        }

    }

    private void seedCosts() {
        try {
            var resource = new ClassPathResource("workshop_costs.json");
            var mapper = new ObjectMapper();
            // Structure: { "Item Name": { "1": 10, "2": 12, ... }, ... }
            Map<String, Map<String, Number>> data =
                    mapper.readValue(resource.getInputStream(), new TypeReference<>() {});

            List<Object[]> batch = new ArrayList<>();
            for (var itemEntry : data.entrySet()) {
                String itemName = itemEntry.getKey();
                Long itemId = jdbc.queryForObject(
                        "SELECT id FROM workshop_item WHERE name = ? AND is_plus = 0",
                        Long.class, itemName);
                if (itemId == null) continue;

                for (var levelEntry : itemEntry.getValue().entrySet()) {
                    int level = Integer.parseInt(levelEntry.getKey());
                    double cost = levelEntry.getValue().doubleValue();
                    batch.add(new Object[]{itemId, level, cost});
                }
            }

            jdbc.batchUpdate(
                    "INSERT OR IGNORE INTO workshop_item_level_cost (workshop_item_id, level, base_cost) VALUES (?,?,?)",
                    batch);
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed Workshop costs", e);
        }
    }

    private void seedPlusCosts() {
        try {
            var resource = new ClassPathResource("workshop_plus_costs.json");
            var mapper = new ObjectMapper();
            // Structure: { "Item Name": { "1": 5000000000, "2": 5040000000, ... }, ... }
            Map<String, Map<String, Number>> data =
                    mapper.readValue(resource.getInputStream(), new TypeReference<>() {});

            List<Object[]> batch = new ArrayList<>();
            for (var itemEntry : data.entrySet()) {
                String itemName = itemEntry.getKey();
                Long itemId = jdbc.queryForObject(
                        "SELECT id FROM workshop_item WHERE name = ? AND is_plus = 1",
                        Long.class, itemName);
                if (itemId == null) continue;

                for (var levelEntry : itemEntry.getValue().entrySet()) {
                    int level = Integer.parseInt(levelEntry.getKey());
                    double cost = levelEntry.getValue().doubleValue();
                    batch.add(new Object[]{itemId, level, cost});
                }
            }

            jdbc.batchUpdate(
                    "INSERT OR IGNORE INTO workshop_item_level_cost (workshop_item_id, level, base_cost) VALUES (?,?,?)",
                    batch);
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed Workshop+ costs", e);
        }
    }
}
