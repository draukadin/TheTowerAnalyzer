package com.pphi.tower.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PerkSeeder {

    private static final Logger log = LoggerFactory.getLogger(PerkSeeder.class);

    private final JdbcTemplate jdbc;

    public PerkSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM perk", Integer.class);
        if (count != null && count > 0) return;
        log.info("Seeding {}...", this.getClass().getSimpleName().replace("Seeder", ""));
        seedPerks();
        log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
    }

    private void seedPerks() {
        // Standard — multiplicative: effective = (1 + (base-1) * picks) * (1 + SPB/100)
        standard("Max Health",                   5, 1.20, "multiplicative", "multiplier");
        standard("Damage",                        5, 1.15, "multiplicative", "multiplier");
        standard("All Coin Bonuses",              5, 1.15, "multiplicative", "multiplier");
        standard("Defense Absolute",              5, 1.15, "multiplicative", "multiplier");
        standard("Cash Bonus",                    5, 1.15, "multiplicative", "multiplier");
        standard("Health Regen",                  5, 1.75, "multiplicative", "multiplier");
        standard("Interest",                      5, 1.50, "multiplicative", "multiplier");
        standard("Land Mine Damage",              5, 3.50, "multiplicative", "multiplier");
        // Standard — additive: effective = base * picks * (1 + SPB/100)
        standard("Free Upgrade Chance for All",   5, 5.00, "additive",       "percent");
        standard("Defense Percent",               5, 4.00, "additive",       "percent");
        standard("Bounce Shot",                   3, 2.00, "additive",       "flat");
        standard("Perk Wave Requirement",         3, 20.00,"additive",       "percent");
        standard("Orbs",                          2, 1.00, "additive",       "flat");
        // Standard — no numeric value
        perk("Unlock a Random Ultimate Weapon",  "Standard", 1, null, null, null, null);
        standard("Increase Max Game Speed by",       1, 1.00, "additive",       "flat");

        // UW perks — fixed bonuses, not affected by Standard Perk Bonus
        uw("More Smart Missiles",          4.00, "additive",       "flat");
        uw("Swamp Radius",            1.50, "multiplicative", "multiplier");
        uw("Wave on Death Wave",              1.00, "additive",       "flat");
        uw("Extra Set of Inner Land Mines",        1.00, "additive",       "flat");
        uw("Golden Tower Bonus",      1.50, "multiplicative", "multiplier");
        uw("Chain Lightning Damage",  2.00, "multiplicative", "multiplier");
        uw("Chrono Field Duration",   5.00, "additive",       "seconds");
        uw("Black Hole Duration",    12.00, "additive",       "seconds");
        uw("Spotlight Damage Bonus",  1.50, "multiplicative", "multiplier");

        // Trade-off perks — upside affected by Improve Trade-off Perks lab (except Ranged Distance)
        // effective_upside = base_value * (1 + improve_lab_level * 0.01)
        tradeoff("Tower Damage",
                1.50, "multiplicative", "multiplier",
                "but Bosses Have 8x Health");
        tradeoff("Coins",
                1.80, "multiplicative", "multiplier",
                "but Tower Max Health -70%");
        tradeoff("Enemy Health",
                -0.50, "additive",       "percent",
                "but Health Regen and Lifesteal -90%");
        tradeoff("Enemy Damage",
                -0.50, "additive",       "percent",
                "but Tower Damage -50%");
        // Ranged Distance has no numeric upside and is excluded from Improve Trade-off Perks
        perk("Ranged Enemies Attack Distance Reduced",  "TradeOff", 1, null, null, null,
                "but Tower Ranged Enemies Damage x3");
        tradeoff("Enemy Speed",
                -0.40, "additive",       "percent",
                "Enemies Damage x2.5");
        tradeoff("Cash Per Wave",
                12.00,"multiplicative", "multiplier",
                "but Enemy Kills Give No Cash");
        tradeoff("Tower Health Regen",
                8.0, "multiplicative", "multiplier",
                "but Tower Max Health -60%");
        tradeoff("Boss Health",
                -0.70, "additive",       "percent",
                "but Boss Speed +50%");
        tradeoff("Lifesteal",
                2.50, "multiplicative", "multiplier",
                "but Knockback Force -70%");
    }

    private void standard(String name, int maxPicks, double baseValue,
                           String formula, String unit) {
        perk(name, "Standard", maxPicks, baseValue, formula, unit, null);
    }

    private void uw(String name, double baseValue, String formula, String unit) {
        perk(name, "UW", 1, baseValue, formula, unit, null);
    }

    private void tradeoff(String name, double baseValue, String formula,
                           String unit, String downsideDesc) {
        perk(name, "TradeOff", 1, baseValue, formula, unit, downsideDesc);
    }

    private void perk(String name, String type, int maxPicks,
                       Double baseValue, String formula, String unit, String downsideDesc) {
        jdbc.update("""
                INSERT OR IGNORE INTO perk (name, type, max_picks, base_value,
                                            value_formula, value_unit, downside_desc)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """, name, type, maxPicks, baseValue, formula, unit, downsideDesc);
    }
}
