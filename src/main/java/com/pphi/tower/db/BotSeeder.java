package com.pphi.tower.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Seeds bot reference data (definitions, stats, and level values).
 *
 * Data sourced from BotDataTable.xlsx.  Values confirmed against wiki text;
 * Excel is authoritative where discrepancies exist (e.g. Gold Bot Range 20 levels/60m,
 * not the wiki-listed 18 levels/56m).
 *
 * Cost formulae:
 *   Regular stat upgrade:  cost(N) = 100 + 40*N  (N = current level, 0-indexed)
 *   Bot+ stat upgrade:     cost(N) = 100 + 50*N
 *   Echoing Shot (Amp Bot+): cost(N) = 100 + 200*N
 *
 * Bot unlock order costs: 100, 300, 600, 900, 1200 medals (1st–5th bot).
 */
@Component
public class BotSeeder {

    private static final Logger log = LoggerFactory.getLogger(BotSeeder.class);

    private final JdbcTemplate jdbc;

    public BotSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM bot", Integer.class);
        if (count != null && count > 0) return;
        log.info("Seeding {}...", this.getClass().getSimpleName().replace("Seeder", ""));
        seedBots();
        log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
    }

    // ── Cost helpers ──────────────────────────────────────────────────────────

    /** Regular stat: 100, 140, 180, … (+40 per level) */
    private static int regularCost(int level) {
        return 100 + 40 * level;
    }

    /** Bot+ stat: 100, 150, 200, … (+50 per level) */
    private static int botPlusCost(int level) {
        return 100 + 50 * level;
    }

    /** Amplify Bot+ Echoing Shot: 100, 300, 500, … (+200 per level) */
    private static int echoingShotCost(int level) {
        return 100 + 200 * level;
    }

    // ── Seeding ───────────────────────────────────────────────────────────────

    @FunctionalInterface
    interface CostFn {
        int cost(int level);
    }

    private void seedBots() {
        // Bots listed in recommended unlock order (flame first for survivability).
        long flame   = bot(1, "FLAME",   "Flame Bot",   "Wildfire");
        long thunder = bot(2, "THUNDER", "Thunder Bot", "Titan Shock");
        long gold    = bot(3, "GOLD",    "Golden Bot",  "Bonus Cell");
        long amp     = bot(4, "AMP",     "Amplify Bot", "Echoing Shot");
        long botbot  = bot(5, "BOTBOT",  "Bot Bot",     "Maximum Power");

        // ── Flame Bot ─────────────────────────────────────────────────────────
        stat(flame, 1, "damage_reduction", "Damage Reduction", "PERCENT",    false, 25, 0.20, +0.03, BotSeeder::regularCost);
        stat(flame, 2, "cooldown",         "Cooldown",         "SECONDS",    false, 15, 75.0, -3.0,  BotSeeder::regularCost);
        stat(flame, 3, "damage",           "Damage",           "MULTIPLIER", false, 30, 50.0, +8.0,  BotSeeder::regularCost);
        stat(flame, 4, "range",            "Range",            "METERS",     false, 15, 30.0, +4.0,  BotSeeder::regularCost);
        stat(flame, 5, "bot_plus",         "Wildfire",         "MULTIPLIER", true,  20,  1.5, +0.1,  BotSeeder::botPlusCost);

        // ── Thunder Bot ───────────────────────────────────────────────────────
        stat(thunder, 1, "duration",   "Duration",    "SECONDS",  false, 20,   5.0, +0.5,  BotSeeder::regularCost);
        stat(thunder, 2, "cooldown",   "Cooldown",    "SECONDS",  false, 15, 120.0, -3.0,  BotSeeder::regularCost);
        stat(thunder, 3, "linger",     "Linger",      "PERCENT",  false, 20,   0.2, +0.03, BotSeeder::regularCost);
        stat(thunder, 4, "range",      "Range",       "METERS",   false, 15,  25.0, +3.0,  BotSeeder::regularCost);
        stat(thunder, 5, "bot_plus",   "Titan Shock", "PERCENT",  true,  20,  0.05, +0.01, BotSeeder::botPlusCost);

        // ── Golden Bot ────────────────────────────────────────────────────────
        stat(gold, 1, "duration", "Duration",   "SECONDS",    false, 30,  20.0, +0.5,  BotSeeder::regularCost);
        stat(gold, 2, "cooldown", "Cooldown",   "SECONDS",    false, 15, 120.0, -3.0,  BotSeeder::regularCost);
        stat(gold, 3, "bonus",    "Bonus",      "MULTIPLIER", false, 30,   2.0, +0.2,  BotSeeder::regularCost);
        stat(gold, 4, "range",    "Range",      "METERS",     false, 20,  20.0, +2.0,  BotSeeder::regularCost);
        stat(gold, 5, "bot_plus", "Bonus Cell", "MULTIPLIER", true,  25,  1.25, +0.05, BotSeeder::botPlusCost);

        // ── Amplify Bot ───────────────────────────────────────────────────────
        stat(amp, 1, "duration", "Duration",      "SECONDS",    false, 30,  20.0, +0.5, BotSeeder::regularCost);
        stat(amp, 2, "cooldown", "Cooldown",       "SECONDS",    false, 15, 120.0, -3.0, BotSeeder::regularCost);
        stat(amp, 3, "bonus",    "Bonus",          "MULTIPLIER", false, 30,   3.5, +0.4, BotSeeder::regularCost);
        stat(amp, 4, "range",    "Range",          "METERS",     false, 18,  25.0, +2.0, BotSeeder::regularCost);
        stat(amp, 5, "bot_plus", "Echoing Shot",   "COUNT",      true,  10,   3.0, +1.0, BotSeeder::echoingShotCost);

        // ── Bot Bot ───────────────────────────────────────────────────────────
        stat(botbot, 1, "duration", "Duration",       "SECONDS",    false, 30, 20.0,  +0.5,  BotSeeder::regularCost);
        stat(botbot, 2, "cooldown", "Cooldown",       "SECONDS",    false, 15, 120.0, -3.0,  BotSeeder::regularCost);
        stat(botbot, 3, "bonus",    "Bonus",          "MULTIPLIER", false, 19,  1.05, +0.05, BotSeeder::regularCost);
        stat(botbot, 4, "range",    "Range",          "METERS",     false, 20, 20.0,  +2.0,  BotSeeder::regularCost);
        stat(botbot, 5, "bot_plus", "Maximum Power",  "MULTIPLIER", true,  20,  1.25, +0.05, BotSeeder::botPlusCost);
    }

    // ── Insert helpers ────────────────────────────────────────────────────────

    private long bot(int id, String code, String name, String botPlusAbilityName) {
        jdbc.update(
                "INSERT INTO bot (id, code, name, bot_plus_ability_name) VALUES (?,?,?,?)",
                id, code, name, botPlusAbilityName);
        jdbc.update(
                "INSERT INTO bot_player_state (bot_id) VALUES (?)",
                id);
        return id;
    }

    private void stat(long botId, int sortOrder, String statKey, String label,
                      String valueUnit, boolean isBotPlus, int maxLevel,
                      double baseValue, double delta, CostFn costFn) {
        Long statId = jdbc.queryForObject("""
                INSERT INTO bot_stat (bot_id, stat_key, label, value_unit, is_bot_plus, max_level, sort_order)
                VALUES (?,?,?,?,?,?,?) RETURNING id
                """,
                Long.class,
                botId, statKey, label, valueUnit, isBotPlus ? 1 : 0, maxLevel, sortOrder);
        if (statId == null) return;

        for (int level = 0; level <= maxLevel; level++) {
            double value = roundTwoDecimals(baseValue + delta * level);
            Integer medalsToNext = (level < maxLevel) ? costFn.cost(level) : null;
            jdbc.update(
                    "INSERT INTO bot_stat_level_value (bot_stat_id, level, value, medals_to_next) VALUES (?,?,?,?)",
                    statId, level, value, medalsToNext);
        }

        jdbc.update(
                "INSERT INTO bot_stat_player_level (bot_stat_id, current_level) VALUES (?,0)",
                statId);
    }

    private static double roundTwoDecimals(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
