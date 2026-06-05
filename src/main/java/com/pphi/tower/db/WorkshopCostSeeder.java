package com.pphi.tower.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WorkshopCostSeeder {

    private final JdbcTemplate jdbc;

    public WorkshopCostSeeder(JdbcTemplate jdbc, WorkshopSeeder workshopSeeder) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM workshop_item_level_cost", Integer.class);
        if (count != null && count > 0) return;

        seedPlusCosts();
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
