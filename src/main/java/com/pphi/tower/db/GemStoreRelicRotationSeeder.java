package com.pphi.tower.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GemStoreRelicRotationSeeder {

    private static final Logger log = LoggerFactory.getLogger(GemStoreRelicRotationSeeder.class);

    private final JdbcTemplate jdbc;

    public GemStoreRelicRotationSeeder(JdbcTemplate jdbc, RelicSeeder relicSeeder) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM gem_store_relic_rotation", Integer.class);
        if (count != null && count > 0) return;
        log.info("Seeding {}...", this.getClass().getSimpleName().replace("Seeder", ""));
        seedRotations();
        log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
    }

    private void seedRotations() {
        // Week 1 - two variants due to launch bug
        rotation("2026-04-14", "STANDARD_1", "Abduction Room", "A");
        rotation("2026-04-14", "STANDARD_2", "Lunar Cat Paw",  "A");
        rotation("2026-04-14", "PREMIUM_1",  "Shadow Puppet",  "A");
        rotation("2026-04-14", "PREMIUM_2",  "Pet Cat",        "A");

        rotation("2026-04-14", "STANDARD_1", "Ionized Plasma",  "B");
        rotation("2026-04-14", "STANDARD_2", "Pizza",           "B");
        rotation("2026-04-14", "PREMIUM_1",  "Planetary Rings", "B");
        rotation("2026-04-14", "PREMIUM_2",  "Alien Warp Drive","B");

        // Week 2
        rotation("2026-04-21", "STANDARD_1", "Lava Flow",      "");
        rotation("2026-04-21", "STANDARD_2", "Pulsar Core",    "");
        rotation("2026-04-21", "PREMIUM_1",  "Planetary Rings","");
        rotation("2026-04-21", "PREMIUM_2",  "Mountain Goat",  "");

        // Week 3
        rotation("2026-04-28", "STANDARD_1", "Bloom Burst",     "");
        rotation("2026-04-28", "STANDARD_2", "Cherry",          "");
        rotation("2026-04-28", "PREMIUM_1",  "Cloud Lightning", "");
        rotation("2026-04-28", "PREMIUM_2",  "Angler Fish",     "");

        // Week 4
        rotation("2026-05-05", "STANDARD_1", "Falling Apple",  "");
        rotation("2026-05-05", "STANDARD_2", "Haunted Mirror", "");
        rotation("2026-05-05", "PREMIUM_1",  "3 Body Solution","");
        rotation("2026-05-05", "PREMIUM_2",  "Crop Circle",    "");

        // Week 5
        rotation("2026-05-12", "STANDARD_1", "UFO Beam",       "");
        rotation("2026-05-12", "STANDARD_2", "Coral Crown",    "");
        rotation("2026-05-12", "PREMIUM_1",  "Sakura Lantern", "");
        rotation("2026-05-12", "PREMIUM_2",  "Time Compass",   "");

        // Week 6
        rotation("2026-05-19", "STANDARD_1", "Infinite Ruler", "");
        rotation("2026-05-19", "STANDARD_2", "Comet",          "");
        rotation("2026-05-19", "PREMIUM_1",  "Cursed Candle",  "");
        rotation("2026-05-19", "PREMIUM_2",  "Do While True",  "");

        // Week 7
        rotation("2026-05-26", "STANDARD_1", "Bacteriophage",    "");
        rotation("2026-05-26", "STANDARD_2", "Clip Ons",         "");
        rotation("2026-05-26", "PREMIUM_1",  "The Fly",          "");
        rotation("2026-05-26", "PREMIUM_2",  "Light Speedometer","");

        // Week 8
        rotation("2026-06-02", "STANDARD_1", "Koi Fish",         "");
        rotation("2026-06-02", "STANDARD_2", "Cobweb",           "");
        rotation("2026-06-02", "PREMIUM_1",  "Space Distortion", "");
        rotation("2026-06-02", "PREMIUM_2",  "Time Travel",      "");
    }

    private void rotation(String startDate, String slot, String relicName, String variant) {
        jdbc.update("""
                INSERT OR IGNORE INTO gem_store_relic_rotation (start_date, slot, relic_id, variant)
                SELECT ?, ?, id, ? FROM relic WHERE name = ?
                """, startDate, slot, variant, relicName);
    }
}
