package com.pphi.tower.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BattleConditionSeeder {

    private static final Logger log = LoggerFactory.getLogger(BattleConditionSeeder.class);

    private final JdbcTemplate jdbc;

    public BattleConditionSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        // Remove the mistakenly-seeded "Overheat" row — it's the category name, not a condition.
        jdbc.update("DELETE FROM battle_condition WHERE name = 'Overheat'");

        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM battle_condition", Integer.class);
        if (count != null && count > 0) return;
        log.info("Seeding {}...", this.getClass().getSimpleName().replace("Seeder", ""));
        seedConditions();
        log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
    }

    private void seedConditions() {
        condition("Skip Decay",                "SD",   "OVERHEAT");
        condition("Skip Reduction - Multiply", "SRM",  "OVERHEAT");
        condition("More Bosses",               "MB",   "OVERHEAT");

        // Heat — randomly assigned per tournament (count depends on league)
        condition("Tank's Ultimate",           "TU",   "HEAT");
        condition("Ranged Ultimate",           "RU",   "HEAT");
        condition("Fast's Ultimate",           "FU",   "HEAT");
        condition("Basic's Ultimate",          "BU",   "HEAT");
        condition("Boss's Ultimate",           "BOU",  "HEAT");
        condition("Protector's Ultimate",      "PU",   "HEAT");
        condition("More Enemies",              "ME",   "HEAT");
        condition("Death Defy Down",           "DD",   "HEAT");
        condition("Armored Enemies",           "AR",   "HEAT");
        condition("Plasma Cannon Resistance",  "PC",   "HEAT");
        condition("Thorns Resistance",         "TR",   "HEAT");
        condition("Orb Resistance",            "OR",   "HEAT");
        condition("Knockback Resistance",      "KR",   "HEAT");
        condition("Death Ray Resistance",      "DR",   "HEAT");
        condition("Energy Shields Down",       "ESD",  "HEAT");
        condition("Enemy Level Skip",          "ELS",  "HEAT");
        condition("Enemy Speed",               "ES",   "HEAT");
        condition("Ultimate Weapon Durations", "UWD",  "HEAT");
        condition("Mass Enforcement",          "MAE",  "HEAT");
        condition("Enemy Attack Speed",        "EAS",  "HEAT");
    }

    private void condition(String name, String acronym, String category) {
        jdbc.update("""
                INSERT OR IGNORE INTO battle_condition (name, acronym, category)
                VALUES (?, ?, ?)
                """, name, acronym, category);
    }
}
