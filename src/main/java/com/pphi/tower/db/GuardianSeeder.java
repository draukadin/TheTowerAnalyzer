package com.pphi.tower.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Seeds Guardian chip reference data.
 *
 * Data sourced from Guardians.xlsx (authoritative over wiki text).
 *
 * Chip acquisition costs:
 *   Ally, Attack — BASE (free with guardian unlock; guardian itself costs 200 bits)
 *   Bounty, Fetch — Guild Shop, 200 tokens (no season gate)
 *   Summon — Guild Shop Season 5, 200 tokens
 *   Scout  — Guild Shop Season 7, 200 tokens
 *
 * Chip slot costs (tokens):
 *   Slot 1 — 200 tokens (also grants base chips Attack and Ally)
 *   Slot 2 — 200 tokens
 *   Slot 3 — 300 tokens
 *   Slot 4 — TBD (NULL)
 *
 * Stat upgrade cost formula (bits_to_next at current level N):
 *   lin(K)           = K*(N+1)                  — simple linear
 *   linOff(K,C)      = K*(N+1) + C              — linear with constant offset
 *   odd()            = 2*N+1  (1,3,5,…)         — odd-number sequence
 */
@Component
public class GuardianSeeder {

    private final JdbcTemplate jdbc;

    public GuardianSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM guardian_chip", Integer.class);
        if (count != null && count > 0) return;
        seedSlots();
        seedChips();
    }

    // ── Cost helpers ──────────────────────────────────────────────────────────

    @FunctionalInterface
    interface CostFn { int cost(int level); }

    private static int lin(int k, int level)            { return k * (level + 1); }
    private static int linOff(int k, int c, int level)  { return k * (level + 1) + c; }
    private static int odd(int level)                   { return 2 * level + 1; }

    // ── Slot seeding ──────────────────────────────────────────────────────────

    private void seedSlots() {
        // Slot 1: 200 tokens (also grants base chips Attack and Ally).
        // Slot 2: 200 tokens. Slot 3: 300 tokens. Slot 4: TBD.
        int[] costs = {200, 200, 300, -1};  // -1 = unknown (stored as NULL)
        for (int i = 0; i < 4; i++) {
            int slot = i + 1;
            Integer cost = costs[i] >= 0 ? costs[i] : null;
            jdbc.update("INSERT INTO guardian_chip_slot (slot_number, unlock_cost_tokens) VALUES (?, ?)", slot, cost);
            jdbc.update("INSERT INTO guardian_chip_slot_player_state (slot_number, unlocked) VALUES (?, 0)", slot);
        }
    }

    // ── Chip seeding ──────────────────────────────────────────────────────────

    private void seedChips() {
        long attack = chip(1, "ATTACK", "Attack", "BASE",       null, null);
        long ally   = chip(2, "ALLY",   "Ally",   "BASE",       null, null);
        long bounty = chip(3, "BOUNTY", "Bounty", "GUILD_SHOP", null, 200);
        long fetch  = chip(4, "FETCH",  "Fetch",  "GUILD_SHOP", null, 200);
        long summon = chip(5, "SUMMON", "Summon", "GUILD_SHOP", 5,    200);
        long scout  = chip(6, "SCOUT",  "Scout",  "GUILD_SHOP", 7,    200);

        // ── Attack ────────────────────────────────────────────────────────────
        stat(attack, 1, "percentage", "Percentage", "PERCENT",    19, 0.01, +0.01, l -> lin(25,  l));
        stat(attack, 2, "cooldown",   "Cooldown",   "SECONDS",    90, 120,  -1.0,  l -> lin(1,   l));
        stat(attack, 3, "targets",    "Targets",    "COUNT",       9,   1,  +1.0,  l -> lin(100, l));

        // ── Ally ──────────────────────────────────────────────────────────────
        stat(ally,   1, "recovery_amount", "Recovery Amount", "PERCENT",    49, 0.01, +0.01, l -> linOff(5, 5,  l));
        stat(ally,   2, "max_recovery",    "Max Recovery",    "MULTIPLIER", 89,  1.1, +0.1,  l -> lin(1,        l));
        stat(ally,   3, "cooldown",        "Cooldown",        "SECONDS",    90, 120,  -1.0,  l -> lin(1,        l));

        // ── Bounty ────────────────────────────────────────────────────────────
        stat(bounty, 1, "multiplier", "Multiplier", "MULTIPLIER", 99, 0.01, +0.01, l -> lin(1,   l));
        stat(bounty, 2, "cooldown",   "Cooldown",   "SECONDS",    60, 120,  -1.0,  l -> lin(2,   l));
        stat(bounty, 3, "targets",    "Targets",    "COUNT",       9,   1,  +1.0,  l -> lin(100, l));

        // ── Fetch ─────────────────────────────────────────────────────────────
        stat(fetch,  1, "cooldown",           "Cooldown",           "SECONDS", 60, 120,  -1.0,  l -> lin(2,         l));
        stat(fetch,  2, "find_chance",        "Find Chance",        "PERCENT", 40, 0.10, +0.01, l -> linOff(5, 15,  l));
        stat(fetch,  3, "double_find_chance", "Double Find Chance", "PERCENT", 48, 0.02, +0.01, l -> linOff(5, 5,   l));

        // ── Summon ────────────────────────────────────────────────────────────
        stat(summon, 1, "cooldown",   "Cooldown",  "SECONDS",    70, 140,  -1.0, l -> odd(l));
        stat(summon, 2, "duration",   "Duration",  "SECONDS",    30,   5,  +1.0, l -> linOff(10, 5, l));
        stat(summon, 3, "cash_bonus", "Cash Bonus","MULTIPLIER",  9, 1.0,  +1.0, l -> lin(100, l));

        // ── Scout ─────────────────────────────────────────────────────────────
        stat(scout,  1, "cooldown",    "Cooldown",    "SECONDS",    70, 105,  -1.0,  l -> odd(l));
        stat(scout,  2, "range_bonus", "Range Bonus", "MULTIPLIER", 40,  2.0, +0.1,  l -> linOff(5, 5,  l));
        stat(scout,  3, "duration",    "Duration",    "SECONDS",    30,   5,  +1.0,  l -> lin(10, l));
    }

    // ── Insert helpers ────────────────────────────────────────────────────────

    private long chip(int id, String code, String name, String source,
                      Integer unlockSeason, Integer unlockCostTokens) {
        jdbc.update(
                "INSERT INTO guardian_chip (id, code, name, source, unlock_season, unlock_cost_tokens) VALUES (?,?,?,?,?,?)",
                id, code, name, source, unlockSeason, unlockCostTokens);
        jdbc.update(
                "INSERT INTO guardian_chip_player_state (chip_id, acquired) VALUES (?, 0)",
                id);
        return id;
    }

    private void stat(long chipId, int sortOrder, String statKey, String label,
                      String valueUnit, int maxLevel, double baseValue, double delta,
                      CostFn costFn) {
        Long statId = jdbc.queryForObject("""
                INSERT INTO guardian_chip_stat (chip_id, stat_key, label, value_unit, max_level, sort_order)
                VALUES (?,?,?,?,?,?) RETURNING id
                """,
                Long.class,
                chipId, statKey, label, valueUnit, maxLevel, sortOrder);
        if (statId == null) return;

        for (int level = 0; level <= maxLevel; level++) {
            double value = round2(baseValue + delta * level);
            Integer bitsToNext = (level < maxLevel) ? costFn.cost(level) : null;
            jdbc.update(
                    "INSERT INTO guardian_chip_stat_level_value (chip_stat_id, level, value, bits_to_next) VALUES (?,?,?,?)",
                    statId, level, value, bitsToNext);
        }

        jdbc.update(
                "INSERT INTO guardian_chip_stat_player_level (chip_stat_id, current_level) VALUES (?,0)",
                statId);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
