package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BotRepository {

    /** Medal costs (medals) to unlock each successive bot: 1st, 2nd, 3rd, 4th, 5th. */
    public static final int[] UNLOCK_COSTS = {100, 300, 600, 900, 1200};

    private final JdbcTemplate                  jdbc;
    private final PendingVersionChangeRepository pendingRepo;

    public BotRepository(JdbcTemplate jdbc, PendingVersionChangeRepository pendingRepo) {
        this.jdbc        = jdbc;
        this.pendingRepo = pendingRepo;
    }

    // ── Records ───────────────────────────────────────────────────────────────

    public record BotStat(
            long statId,
            String statKey,
            String label,
            String valueUnit,
            boolean isBotPlus,
            int maxLevel,
            int sortOrder,
            int currentLevel
    ) {}

    public record BotDef(
            long id,
            String code,
            String name,
            String botPlusAbilityName,
            boolean unlocked,
            boolean botPlusUnlocked,
            Integer unlockOrder,
            List<BotStat> stats
    ) {}

    public record LevelValue(int level, double value, Integer medalsToNext) {}

    public record Preset(int id, int slot, String name) {}

    public record PresetBotUnlock(long botId, boolean unlocked, boolean botPlusUnlocked) {}

    public record PresetStatLevel(long botStatId, int targetLevel) {}

    // ── Queries ───────────────────────────────────────────────────────────────

    public List<BotDef> getAll() {
        // Fetch bots and stats in separate queries to avoid nested connection acquisition
        // (calling getStats() inside a row mapper would deadlock with pool-size=1).
        List<BotDef> bots = jdbc.query("""
                SELECT b.id, b.code, b.name, b.bot_plus_ability_name,
                       COALESCE(ps.unlocked, 0)          AS unlocked,
                       COALESCE(ps.bot_plus_unlocked, 0) AS bot_plus_unlocked,
                       ps.unlock_order
                FROM bot b
                LEFT JOIN bot_player_state ps ON ps.bot_id = b.id
                ORDER BY b.id
                """,
                (rs, i) -> new BotDef(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("bot_plus_ability_name"),
                        rs.getInt("unlocked") == 1,
                        rs.getInt("bot_plus_unlocked") == 1,
                        rs.getObject("unlock_order") != null ? rs.getInt("unlock_order") : null,
                        new java.util.ArrayList<>()));

        Map<Long, List<BotStat>> statsByBot = new HashMap<>();
        jdbc.query("""
                SELECT bs.id, bs.bot_id, bs.stat_key, bs.label, bs.value_unit, bs.is_bot_plus,
                       bs.max_level, bs.sort_order,
                       COALESCE(pl.current_level, 0) AS current_level
                FROM bot_stat bs
                LEFT JOIN bot_stat_player_level pl ON pl.bot_stat_id = bs.id
                ORDER BY bs.bot_id, bs.sort_order
                """,
                (rs, i) -> {
                    statsByBot.computeIfAbsent(rs.getLong("bot_id"), k -> new java.util.ArrayList<>())
                              .add(new BotStat(
                                      rs.getLong("id"),
                                      rs.getString("stat_key"),
                                      rs.getString("label"),
                                      rs.getString("value_unit"),
                                      rs.getInt("is_bot_plus") == 1,
                                      rs.getInt("max_level"),
                                      rs.getInt("sort_order"),
                                      rs.getInt("current_level")));
                    return null;
                });

        bots.forEach(b -> b.stats().addAll(statsByBot.getOrDefault(b.id(), List.of())));
        return bots;
    }

    public List<LevelValue> getLevelValues(long botStatId) {
        return jdbc.query("""
                SELECT level, value, medals_to_next
                FROM bot_stat_level_value
                WHERE bot_stat_id = ?
                ORDER BY level
                """,
                (rs, i) -> new LevelValue(
                        rs.getInt("level"),
                        rs.getDouble("value"),
                        rs.getObject("medals_to_next") != null ? rs.getInt("medals_to_next") : null),
                botStatId);
    }

    public record StatLevelValues(long statId, List<LevelValue> levels) {}

    public List<StatLevelValues> getAllLevelValues() {
        Map<Long, List<LevelValue>> map = new HashMap<>();
        jdbc.query("""
                SELECT bot_stat_id, level, value, medals_to_next
                FROM bot_stat_level_value
                ORDER BY bot_stat_id, level
                """,
                (rs, i) -> {
                    long statId = rs.getLong("bot_stat_id");
                    map.computeIfAbsent(statId, k -> new java.util.ArrayList<>())
                       .add(new LevelValue(
                               rs.getInt("level"),
                               rs.getDouble("value"),
                               rs.getObject("medals_to_next") != null ? rs.getInt("medals_to_next") : null));
                    return null;
                });
        return map.entrySet().stream()
                .map(e -> new StatLevelValues(e.getKey(), e.getValue()))
                .toList();
    }

    // ── Updates ───────────────────────────────────────────────────────────────

    public void setUnlocked(long botId, boolean unlocked, Integer unlockOrder) {
        jdbc.update("""
                INSERT INTO bot_player_state (bot_id, unlocked, unlock_order) VALUES (?,?,?)
                ON CONFLICT(bot_id) DO UPDATE SET unlocked = excluded.unlocked,
                                                  unlock_order = excluded.unlock_order
                """, botId, unlocked ? 1 : 0, unlockOrder);
    }

    public void setBotPlusUnlocked(long botId, boolean botPlusUnlocked) {
        jdbc.update("""
                INSERT INTO bot_player_state (bot_id, bot_plus_unlocked) VALUES (?,?)
                ON CONFLICT(bot_id) DO UPDATE SET bot_plus_unlocked = excluded.bot_plus_unlocked
                """, botId, botPlusUnlocked ? 1 : 0);
    }

    public void setStatLevel(long botStatId, int level) {
        Integer oldLevel = jdbc.queryForObject(
                "SELECT COALESCE(current_level, 0) FROM bot_stat_player_level WHERE bot_stat_id = ?",
                Integer.class, botStatId);

        jdbc.update("""
                INSERT INTO bot_stat_player_level (bot_stat_id, current_level) VALUES (?,?)
                ON CONFLICT(bot_stat_id) DO UPDATE SET current_level = excluded.current_level
                """, botStatId, level);

        if (oldLevel != null && level != oldLevel) {
            var row = jdbc.queryForMap("""
                    SELECT s.label, b.name FROM bot_stat s JOIN bot b ON b.id = s.bot_id WHERE s.id = ?
                    """, botStatId);
            String entity = row.get("name") + " " + row.get("label");
            pendingRepo.record("BOT", entity, String.valueOf(oldLevel), String.valueOf(level), null);
        }
    }

    // ── Presets ───────────────────────────────────────────────────────────────

    public List<Preset> getPresets() {
        return jdbc.query(
                "SELECT id, slot, name FROM bot_preset ORDER BY slot",
                (rs, i) -> new Preset(rs.getInt("id"), rs.getInt("slot"), rs.getString("name")));
    }

    public int upsertPreset(int slot, String name) {
        jdbc.update("""
                INSERT INTO bot_preset (slot, name) VALUES (?,?)
                ON CONFLICT(slot) DO UPDATE SET name = excluded.name
                """, slot, name);
        return jdbc.queryForObject(
                "SELECT id FROM bot_preset WHERE slot = ?", Integer.class, slot);
    }

    public void deletePreset(int presetId) {
        jdbc.update("DELETE FROM bot_preset WHERE id = ?", presetId);
    }

    public List<PresetBotUnlock> getPresetUnlocks(int presetId) {
        return jdbc.query("""
                SELECT bot_id, unlocked, bot_plus_unlocked
                FROM bot_preset_unlock
                WHERE preset_id = ?
                ORDER BY bot_id
                """,
                (rs, i) -> new PresetBotUnlock(
                        rs.getLong("bot_id"),
                        rs.getInt("unlocked") == 1,
                        rs.getInt("bot_plus_unlocked") == 1),
                presetId);
    }

    public List<PresetStatLevel> getPresetStatLevels(int presetId) {
        return jdbc.query("""
                SELECT bot_stat_id, target_level
                FROM bot_preset_stat_level
                WHERE preset_id = ?
                ORDER BY bot_stat_id
                """,
                (rs, i) -> new PresetStatLevel(rs.getLong("bot_stat_id"), rs.getInt("target_level")),
                presetId);
    }

    public void setPresetUnlocks(int presetId, List<PresetBotUnlock> unlocks) {
        jdbc.update("DELETE FROM bot_preset_unlock WHERE preset_id = ?", presetId);
        if (unlocks.isEmpty()) return;
        jdbc.batchUpdate(
                "INSERT INTO bot_preset_unlock (preset_id, bot_id, unlocked, bot_plus_unlocked) VALUES (?,?,?,?)",
                unlocks.stream()
                        .map(u -> new Object[]{presetId, u.botId(), u.unlocked() ? 1 : 0, u.botPlusUnlocked() ? 1 : 0})
                        .toList());
    }

    public void setPresetStatLevels(int presetId, List<PresetStatLevel> levels) {
        jdbc.update("DELETE FROM bot_preset_stat_level WHERE preset_id = ?", presetId);
        if (levels.isEmpty()) return;
        jdbc.batchUpdate(
                "INSERT INTO bot_preset_stat_level (preset_id, bot_stat_id, target_level) VALUES (?,?,?)",
                levels.stream()
                        .map(l -> new Object[]{presetId, l.botStatId(), l.targetLevel()})
                        .toList());
    }
}
