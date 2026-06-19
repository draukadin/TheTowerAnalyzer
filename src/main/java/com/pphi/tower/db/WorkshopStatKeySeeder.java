package com.pphi.tower.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class WorkshopStatKeySeeder {

    private static final Logger log = LoggerFactory.getLogger(WorkshopStatKeySeeder.class);

    // Maps relic.bonus_stat display strings → canonical stat_key
    private static final Map<String, String> RELIC_STAT_KEYS = Map.ofEntries(
            Map.entry("Damage",                "damage"),
            Map.entry("Attack Speed",          "attack_speed"),
            Map.entry("Crit Factor",           "critical_factor"),
            Map.entry("Crit Chance",           "critical_chance"),
            Map.entry("Damage / Meter",        "damage_per_meter"),
            Map.entry("Health",                "health"),
            Map.entry("Health Regen",          "health_regen"),
            Map.entry("Defense Absolute",      "defense_absolute"),
            Map.entry("Defense %",             "defense_pct"),
            Map.entry("Coins",                 "coin_bonus"),
            Map.entry("Cash",                  "cash_bonus"),
            Map.entry("Lab Speed",             "lab_speed"),
            Map.entry("Ultimate Damage",       "ultimate_damage"),
            Map.entry("Bot Range",             "bot_range"),
            Map.entry("Thorns",                "thorns_damage"),
            Map.entry("Orb Speed",             "orb_speed"),
            Map.entry("Free Attack Upgrade",   "free_attack_upgrade"),
            Map.entry("Free Defense Upgrade",  "free_defense_upgrade"),
            Map.entry("Free Utility Upgrade",  "free_utility_upgrade"),
            Map.entry("Recovery Amount",       "recovery_amount"),
            Map.entry("Enemy Attack Level Skip","enemy_attack_level_skip"),
            Map.entry("Enemy Health Level Skip","enemy_health_level_skip"),
            Map.entry("Knockback Force",       "knockback_force"),
            Map.entry("Super Critical Chance", "super_crit_chance"),
            Map.entry("Super Critical Mult",   "super_crit_multi"),
            Map.entry("Wall Rebuild",          "wall_rebuild"),
            Map.entry("Rend Armor Mult",       "rend_armor_multi")
    );

    // Workshop item name → one or more stat keys
    private static final List<ItemMapping> ITEM_MAPPINGS = List.of(
            // ── Workshop Attack ──────────────────────────────────────────────
            new ItemMapping("Damage",                false, "damage"),
            new ItemMapping("Attack Speed",          false, "attack_speed"),
            new ItemMapping("Critical Chance",       false, "critical_chance"),
            new ItemMapping("Critical Factor",       false, "critical_factor"),
            new ItemMapping("Range",                 false, "attack_range"),
            new ItemMapping("Damage / Meter",        false, "damage_per_meter"),
            new ItemMapping("Multishot Chance",      false, "multishot_chance"),
            new ItemMapping("Multishot Targets",     false, "multishot_targets"),
            new ItemMapping("Rapid Fire Chance",     false, "rapid_fire_chance"),
            new ItemMapping("Rapid Fire Duration",   false, "rapid_fire_duration"),
            new ItemMapping("Bounce Shot Chance",    false, "bounce_shot_chance"),
            new ItemMapping("Bounce Shot Targets",   false, "bounce_shot_targets"),
            new ItemMapping("Bounce Shot Range",     false, "bounce_shot_range"),
            new ItemMapping("Super Critical Chance", false, "super_crit_chance"),
            new ItemMapping("Super Critical Mult",   false, "super_crit_multi"),
            new ItemMapping("Rend Armor Chance",     false, "rend_armor_chance"),
            new ItemMapping("Rend Armor Mult",       false, "rend_armor_multi"),
            // ── Workshop Defense ─────────────────────────────────────────────
            new ItemMapping("Health",                false, "health"),
            new ItemMapping("Health Regen",          false, "health_regen"),
            new ItemMapping("Defense %",             false, "defense_pct"),
            new ItemMapping("Defense Absolute",      false, "defense_absolute"),
            new ItemMapping("Thorn Damage",          false, "thorns_damage"),
            new ItemMapping("Lifesteal",             false, "lifesteal"),
            new ItemMapping("Knockback Chance",      false, "knockback_chance"),
            new ItemMapping("Knockback Force",       false, "knockback_force"),
            new ItemMapping("Orb Speed",             false, "orb_speed"),
            new ItemMapping("Orbs",                  false, "orbs"),
            new ItemMapping("Shockwave Size",        false, "shockwave_size"),
            new ItemMapping("Shockwave Frequency",   false, "shockwave_frequency"),
            new ItemMapping("Land Mine Chance",      false, "land_mine_chance"),
            new ItemMapping("Land Mine Damage",      false, "land_mine_damage"),
            new ItemMapping("Land Mine Radius",      false, "land_mine_radius"),
            new ItemMapping("Death Defy",            false, "death_defy"),
            new ItemMapping("Wall Health",           false, "wall_health"),
            new ItemMapping("Wall Rebuild",          false, "wall_rebuild"),
            // ── Workshop Utility ─────────────────────────────────────────────
            new ItemMapping("Cash Bonus",            false, "cash_bonus"),
            new ItemMapping("Cash / Wave",           false, "cash_per_wave"),
            new ItemMapping("Coin / Kill Bonus",     false, "coins_kill_bonus"),
            new ItemMapping("Coin / Wave",           false, "coins_per_wave"),
            new ItemMapping("Free Attack Upgrade",   false, "free_attack_upgrade"),
            new ItemMapping("Free Defense Upgrade",  false, "free_defense_upgrade"),
            new ItemMapping("Interest / Wave",       false, "interest_per_wave"),
            new ItemMapping("Free Utility Upgrade",  false, "free_utility_upgrade"),
            new ItemMapping("Recovery Amount",       false, "recovery_amount"),
            new ItemMapping("Max Amount",            false, "max_recovery"),
            new ItemMapping("Package Chance",        false, "package_chance"),
            new ItemMapping("Enemy Attack Level Skip",false,"enemy_attack_level_skip"),
            new ItemMapping("Enemy Health Level Skip",false,"enemy_health_level_skip"),
            // ── Workshop+ Attack ─────────────────────────────────────────────
            new ItemMapping("Damage +",              true,  "damage"),
            new ItemMapping("Rend Armor Mult +",     true,  "rend_armor_multi"),
            new ItemMapping("Critical Factor +",     true,  "critical_factor"),
            new ItemMapping("Damage / Meter +",      true,  "damage_per_meter"),
            new ItemMapping("Super Crit Multi +",    true,  "super_crit_multi"),
            new ItemMapping("Attack Speed +",        true,  "attack_speed"),
            // ── Workshop+ Defense ────────────────────────────────────────────
            new ItemMapping("Health +",              true,  "health"),
            new ItemMapping("Health Regen +",        true,  "health_regen"),
            new ItemMapping("Defense Absolute +",    true,  "defense_absolute"),
            new ItemMapping("Land Mine Damage +",    true,  "land_mine_damage"),
            new ItemMapping("Wall Health +",         true,  "wall_health"),
            new ItemMapping("Orb Size +",            true,  "orb_size"),
            // ── Workshop+ Utility ────────────────────────────────────────────
            new ItemMapping("Cash Bonus +",          true,  "cash_bonus"),
            new ItemMapping("Coin Bonus +",          true,  "coin_bonus"),
            new ItemMapping("Cells / Kill Bonus +",  true,  "cells_per_kill_bonus"),
            // multi-stat items
            new ItemMapping("Free Upgrades +",       true,  "free_attack_upgrade", "free_defense_upgrade", "free_utility_upgrade"),
            new ItemMapping("Recovery Package +",    true,  "max_recovery", "recovery_amount"),
            new ItemMapping("Enemy Level Skips +",   true,  "enemy_attack_level_skip", "enemy_health_level_skip")
    );

    private final JdbcTemplate jdbc;

    public WorkshopStatKeySeeder(JdbcTemplate jdbc, DatabaseInitializer init, WorkshopSeeder workshopSeeder) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        log.info("Seeding WorkshopStatKey...");
        seedItemKeys();
        seedRelicKeys();
        log.info("Finished seeding WorkshopStatKey");
    }

    private void seedItemKeys() {
        for (ItemMapping m : ITEM_MAPPINGS) {
            Long itemId = jdbc.queryForObject(
                    "SELECT id FROM workshop_item WHERE name = ? AND is_plus = ?",
                    Long.class, m.name(), m.isPlus() ? 1 : 0);
            if (itemId == null) {
                log.warn("Workshop item not found: '{}' (is_plus={})", m.name(), m.isPlus());
                continue;
            }
            for (String key : m.statKeys()) {
                jdbc.update("""
                        INSERT OR IGNORE INTO workshop_item_stat_key (workshop_item_id, stat_key)
                        VALUES (?, ?)
                        """, itemId, key);
            }
        }
    }

    private void seedRelicKeys() {
        for (Map.Entry<String, String> entry : RELIC_STAT_KEYS.entrySet()) {
            jdbc.update(
                    "UPDATE relic SET stat_key = ? WHERE bonus_stat = ? AND stat_key IS NULL",
                    entry.getValue(), entry.getKey());
        }
    }

    private record ItemMapping(String name, boolean isPlus, String... statKeys) {}
}
