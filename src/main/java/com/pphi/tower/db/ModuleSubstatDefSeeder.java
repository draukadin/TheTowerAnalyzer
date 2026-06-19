package com.pphi.tower.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModuleSubstatDefSeeder {

    private static final Logger log = LoggerFactory.getLogger(ModuleSubstatDefSeeder.class);

    private final JdbcTemplate jdbc;

    public ModuleSubstatDefSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM module_substat_def", Integer.class);
        if (count != null && count > 0) return;
        seed();
    }

    private void seed() {
        log.info("Seeding {}...", this.getClass().getSimpleName().replace("Seeder", ""));
        try {
            var resource = new ClassPathResource("module_substat_def.json");
            var mapper   = new ObjectMapper();
            List<Entry> entries = mapper.readValue(resource.getInputStream(), new TypeReference<>() {});
            List<Object[]> batch = entries.stream()
                    .map(e -> new Object[]{ e.moduleType(), e.key(), e.label(), e.minRarity() })
                    .toList();
            jdbc.batchUpdate("""
                    INSERT INTO module_substat_def (module_type, key, label, min_rarity) VALUES (?, ?, ?, ?)
                    ON CONFLICT(module_type, key) DO UPDATE SET label = excluded.label, min_rarity = excluded.min_rarity
                    """, batch);
            log.info("Upserted {} module substat definitions", batch.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to seed module_substat_def", e);
        }
        log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
    }

    private record Entry(String moduleType, String key, String label, String minRarity) {}
}
