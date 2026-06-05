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
public class LabCostSeeder {

    private final JdbcTemplate jdbc;

    public LabCostSeeder(JdbcTemplate jdbc, LabSeeder labSeeder) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM lab_level_cost", Integer.class);
        Integer negCount = jdbc.queryForObject("SELECT COUNT(*) FROM lab_level_cost WHERE coin_cost < 0", Integer.class);
        Integer nullDurCount = jdbc.queryForObject(
                "SELECT COUNT(*) FROM lab_level_cost lc JOIN lab l ON l.id = lc.lab_id WHERE l.name IN ('Shatter Shards','Super Crit Chance','Damage Mastery','Flame Bot - Cooldown','Ban Perks','Swamp Rend','Swamp Rend - Additional Enemies') AND lc.duration_seconds IS NULL",
                Integer.class);
        boolean hasCorrupt = (negCount != null && negCount > 0) || (nullDurCount != null && nullDurCount > 0);
        if (count != null && count > 0 && !hasCorrupt) return;
        if (hasCorrupt) jdbc.execute("DELETE FROM lab_level_cost");

        try {
            var resource = new ClassPathResource("lab_costs.json");
            var mapper = new ObjectMapper();
            // Structure: { "Lab Name": { "1": { "dur": 599, "cost": 300 }, ... }, ... }
            Map<String, Map<String, Map<String, Object>>> data =
                    mapper.readValue(resource.getInputStream(),
                            new TypeReference<>() {});

            List<Object[]> batch = new ArrayList<>();
            for (var labEntry : data.entrySet()) {
                String labName = labEntry.getKey();
                Long labId = jdbc.queryForObject(
                        "SELECT id FROM lab WHERE name = ?", Long.class, labName);
                if (labId == null) continue;

                for (var levelEntry : labEntry.getValue().entrySet()) {
                    int level = Integer.parseInt(levelEntry.getKey());
                    Map<String, Object> row = levelEntry.getValue();
                    Integer dur  = row.get("dur")  != null ? ((Number) row.get("dur")).intValue() : null;
                    Double  cost = row.get("cost") != null ? ((Number) row.get("cost")).doubleValue() : null;
                    batch.add(new Object[]{labId, level, dur, cost});
                }
            }

            jdbc.batchUpdate(
                    "INSERT OR IGNORE INTO lab_level_cost (lab_id, level, duration_seconds, coin_cost) VALUES (?,?,?,?)",
                    batch);
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed lab costs", e);
        }
    }
}
