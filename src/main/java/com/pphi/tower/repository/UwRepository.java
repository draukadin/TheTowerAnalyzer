package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UwRepository {

    private final JdbcTemplate jdbc;

    public UwRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    public record UwStatPlayerData(
            int statId,
            String statKey,
            String label,
            int maxLevel,
            int currentLevel,
            int targetLevel,
            double currentValue,
            Integer stonesToNext,
            int stonesInvested,
            int stonesToMax,
            int stonesToTarget
    ) {}

    public record UwPlayerData(
            int uwId,
            String code,
            String name,
            String uwPlusName,
            boolean unlocked,
            boolean uwPlusUnlocked,
            List<UwStatPlayerData> stats
    ) {}

    // ── Reads ─────────────────────────────────────────────────────────────────

    public List<UwPlayerData> getAllUwState() {
        List<Map<String, Object>> rows = jdbc.queryForList("""
                SELECT
                    u.id           AS uw_id,
                    u.code,
                    u.name,
                    u.uw_plus_name,
                    COALESCE(ps.unlocked, 0)          AS unlocked,
                    COALESCE(ps.uw_plus_unlocked, 0)  AS uw_plus_unlocked,
                    s.id           AS stat_id,
                    s.stat_key,
                    s.label,
                    s.max_level,
                    s.sort_order,
                    COALESCE(pl.current_level, 0)     AS current_level,
                    lv.value       AS current_value,
                    lv.stones_to_next,
                    COALESCE(tl.target_level, 0) AS target_level,
                    (SELECT COALESCE(SUM(lv2.stones_to_next), 0)
                     FROM uw_stat_level_value lv2
                     WHERE lv2.uw_stat_id = s.id
                       AND lv2.level < COALESCE(pl.current_level, 0)) AS stones_invested,
                    (SELECT COALESCE(SUM(lv3.stones_to_next), 0)
                     FROM uw_stat_level_value lv3
                     WHERE lv3.uw_stat_id = s.id
                       AND lv3.level >= COALESCE(pl.current_level, 0)
                       AND lv3.stones_to_next IS NOT NULL) AS stones_to_max,
                    (SELECT COALESCE(SUM(lv4.stones_to_next), 0)
                     FROM uw_stat_level_value lv4
                     WHERE lv4.uw_stat_id = s.id
                       AND lv4.level >= COALESCE(pl.current_level, 0)
                       AND lv4.level < COALESCE(tl.target_level, 0)
                       AND lv4.stones_to_next IS NOT NULL) AS stones_to_target
                FROM uw u
                LEFT JOIN uw_player_state ps ON ps.uw_id = u.id
                JOIN uw_stat s ON s.uw_id = u.id
                LEFT JOIN uw_stat_player_level pl ON pl.uw_stat_id = s.id
                LEFT JOIN uw_stat_target_level tl ON tl.uw_stat_id = s.id
                LEFT JOIN uw_stat_level_value lv
                    ON lv.uw_stat_id = s.id
                   AND lv.level = COALESCE(pl.current_level, 0)
                ORDER BY u.id, s.sort_order
                """);

        Map<Integer, UwPlayerData> byId = new LinkedHashMap<>();
        Map<Integer, List<UwStatPlayerData>> statsByUwId = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            int uwId = ((Number) row.get("uw_id")).intValue();

            if (!byId.containsKey(uwId)) {
                byId.put(uwId, new UwPlayerData(
                        uwId,
                        (String) row.get("code"),
                        (String) row.get("name"),
                        (String) row.get("uw_plus_name"),
                        toBoolean(row.get("unlocked")),
                        toBoolean(row.get("uw_plus_unlocked")),
                        new ArrayList<>()
                ));
                statsByUwId.put(uwId, byId.get(uwId).stats());
            }

            Integer stonesToNext = row.get("stones_to_next") != null
                    ? ((Number) row.get("stones_to_next")).intValue()
                    : null;

            statsByUwId.get(uwId).add(new UwStatPlayerData(
                    ((Number) row.get("stat_id")).intValue(),
                    (String) row.get("stat_key"),
                    (String) row.get("label"),
                    ((Number) row.get("max_level")).intValue(),
                    ((Number) row.get("current_level")).intValue(),
                    ((Number) row.get("target_level")).intValue(),
                    ((Number) row.get("current_value")).doubleValue(),
                    stonesToNext,
                    ((Number) row.get("stones_invested")).intValue(),
                    ((Number) row.get("stones_to_max")).intValue(),
                    ((Number) row.get("stones_to_target")).intValue()
            ));
        }

        return new ArrayList<>(byId.values());
    }

    // ── Writes ────────────────────────────────────────────────────────────────

    public void setUnlocked(int uwId, boolean unlocked) {
        jdbc.update("""
                INSERT INTO uw_player_state (uw_id, unlocked, uw_plus_unlocked)
                VALUES (?, ?, 0)
                ON CONFLICT(uw_id) DO UPDATE SET unlocked = excluded.unlocked
                """, uwId, unlocked ? 1 : 0);
    }

    public void setUwPlusUnlocked(int uwId, boolean uwPlusUnlocked) {
        jdbc.update("""
                INSERT INTO uw_player_state (uw_id, unlocked, uw_plus_unlocked)
                VALUES (?, 0, ?)
                ON CONFLICT(uw_id) DO UPDATE SET uw_plus_unlocked = excluded.uw_plus_unlocked
                """, uwId, uwPlusUnlocked ? 1 : 0);
    }

    public void setTargetLevel(int uwStatId, int targetLevel) {
        jdbc.update("""
                INSERT INTO uw_stat_target_level (uw_stat_id, target_level) VALUES (?,?)
                ON CONFLICT(uw_stat_id) DO UPDATE SET target_level = excluded.target_level
                """, uwStatId, targetLevel);
    }

    public void setStatLevel(int uwStatId, int newLevel) {
        Integer oldLevel = jdbc.queryForObject(
                "SELECT current_level FROM uw_stat_player_level WHERE uw_stat_id = ?",
                Integer.class, uwStatId);

        int old = oldLevel != null ? oldLevel : 0;

        jdbc.update(
                "UPDATE uw_stat_player_level SET current_level = ? WHERE uw_stat_id = ?",
                newLevel, uwStatId);

        jdbc.update(
                "INSERT INTO uw_stat_level_history (uw_stat_id, old_level, new_level) VALUES (?,?,?)",
                uwStatId, old, newLevel);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static boolean toBoolean(Object value) {
        if (value == null) return false;
        return ((Number) value).intValue() != 0;
    }
}
