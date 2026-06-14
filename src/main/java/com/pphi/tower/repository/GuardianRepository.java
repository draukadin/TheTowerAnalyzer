package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GuardianRepository {

    private final JdbcTemplate                  jdbc;
    private final PendingVersionChangeRepository pendingRepo;

    public GuardianRepository(JdbcTemplate jdbc, PendingVersionChangeRepository pendingRepo) {
        this.jdbc        = jdbc;
        this.pendingRepo = pendingRepo;
    }

    // ── Records ───────────────────────────────────────────────────────────────

    public record ChipStat(
            long statId,
            String statKey,
            String label,
            String valueUnit,
            int maxLevel,
            int sortOrder,
            int currentLevel
    ) {}

    public record ChipDef(
            long id,
            String code,
            String name,
            String source,
            Integer unlockSeason,
            Integer unlockCostTokens,
            boolean acquired,
            List<ChipStat> stats
    ) {}

    public record ChipSlot(int slotNumber, Integer unlockCostTokens, boolean unlocked) {}

    public record LevelValue(int level, double value, Integer bitsToNext) {}

    public record StatLevelValues(long statId, List<LevelValue> levels) {}

    public record Preset(int id, int slot, String name) {}

    public record PresetChip(long chipId, boolean active) {}

    public record PresetStatLevel(long chipStatId, int targetLevel) {}

    public record LevelInput(double value, Integer bitsToNext) {}
    public record StatInput(String statKey, String label, String valueUnit, int sortOrder, List<LevelInput> levels) {}

    // ── Queries ───────────────────────────────────────────────────────────────

    public boolean isGuardianUnlocked() {
        Integer v = jdbc.queryForObject(
                "SELECT unlocked FROM guardian_player_state WHERE id = 1", Integer.class);
        return v != null && v == 1;
    }

    public List<ChipSlot> getSlots() {
        return jdbc.query("""
                SELECT s.slot_number, s.unlock_cost_tokens,
                       COALESCE(ps.unlocked, 0) AS unlocked
                FROM guardian_chip_slot s
                LEFT JOIN guardian_chip_slot_player_state ps ON ps.slot_number = s.slot_number
                ORDER BY s.slot_number
                """,
                (rs, i) -> new ChipSlot(
                        rs.getInt("slot_number"),
                        rs.getObject("unlock_cost_tokens") != null ? rs.getInt("unlock_cost_tokens") : null,
                        rs.getInt("unlocked") == 1));
    }

    public List<ChipDef> getAll() {
        List<ChipDef> chips = jdbc.query("""
                SELECT c.id, c.code, c.name, c.source, c.unlock_season, c.unlock_cost_tokens,
                       COALESCE(ps.acquired, 0) AS acquired
                FROM guardian_chip c
                LEFT JOIN guardian_chip_player_state ps ON ps.chip_id = c.id
                ORDER BY c.id
                """,
                (rs, i) -> new ChipDef(
                        rs.getLong("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("source"),
                        rs.getObject("unlock_season") != null ? rs.getInt("unlock_season") : null,
                        rs.getObject("unlock_cost_tokens") != null ? rs.getInt("unlock_cost_tokens") : null,
                        rs.getInt("acquired") == 1,
                        new java.util.ArrayList<>()));

        Map<Long, List<ChipStat>> statsByChip = new HashMap<>();
        jdbc.query("""
                SELECT cs.id, cs.chip_id, cs.stat_key, cs.label, cs.value_unit,
                       cs.max_level, cs.sort_order,
                       COALESCE(pl.current_level, 0) AS current_level
                FROM guardian_chip_stat cs
                LEFT JOIN guardian_chip_stat_player_level pl ON pl.chip_stat_id = cs.id
                ORDER BY cs.chip_id, cs.sort_order
                """,
                (rs, i) -> {
                    statsByChip.computeIfAbsent(rs.getLong("chip_id"), k -> new java.util.ArrayList<>())
                               .add(new ChipStat(
                                       rs.getLong("id"),
                                       rs.getString("stat_key"),
                                       rs.getString("label"),
                                       rs.getString("value_unit"),
                                       rs.getInt("max_level"),
                                       rs.getInt("sort_order"),
                                       rs.getInt("current_level")));
                    return null;
                });

        chips.forEach(c -> c.stats().addAll(statsByChip.getOrDefault(c.id(), List.of())));
        return chips;
    }

    public List<StatLevelValues> getAllLevelValues() {
        Map<Long, List<LevelValue>> map = new HashMap<>();
        jdbc.query("""
                SELECT chip_stat_id, level, value, bits_to_next
                FROM guardian_chip_stat_level_value
                ORDER BY chip_stat_id, level
                """,
                (rs, i) -> {
                    long statId = rs.getLong("chip_stat_id");
                    map.computeIfAbsent(statId, k -> new java.util.ArrayList<>())
                       .add(new LevelValue(
                               rs.getInt("level"),
                               rs.getDouble("value"),
                               rs.getObject("bits_to_next") != null ? rs.getInt("bits_to_next") : null));
                    return null;
                });
        return map.entrySet().stream()
                .map(e -> new StatLevelValues(e.getKey(), e.getValue()))
                .toList();
    }

    // ── Updates ───────────────────────────────────────────────────────────────

    public void setGuardianUnlocked(boolean unlocked) {
        jdbc.update("""
                INSERT INTO guardian_player_state (id, unlocked) VALUES (1, ?)
                ON CONFLICT(id) DO UPDATE SET unlocked = excluded.unlocked
                """, unlocked ? 1 : 0);
    }

    public void setSlotUnlocked(int slotNumber, boolean unlocked) {
        jdbc.update("""
                INSERT INTO guardian_chip_slot_player_state (slot_number, unlocked) VALUES (?,?)
                ON CONFLICT(slot_number) DO UPDATE SET unlocked = excluded.unlocked
                """, slotNumber, unlocked ? 1 : 0);
    }

    public void setChipAcquired(long chipId, boolean acquired) {
        jdbc.update("""
                INSERT INTO guardian_chip_player_state (chip_id, acquired) VALUES (?,?)
                ON CONFLICT(chip_id) DO UPDATE SET acquired = excluded.acquired
                """, chipId, acquired ? 1 : 0);
    }

    public void setStatLevel(long chipStatId, int level) {
        Integer oldLevel = jdbc.queryForObject(
                "SELECT COALESCE(current_level, 0) FROM guardian_chip_stat_player_level WHERE chip_stat_id = ?",
                Integer.class, chipStatId);

        jdbc.update("""
                INSERT INTO guardian_chip_stat_player_level (chip_stat_id, current_level) VALUES (?,?)
                ON CONFLICT(chip_stat_id) DO UPDATE SET current_level = excluded.current_level
                """, chipStatId, level);

        if (oldLevel != null && level != oldLevel) {
            var row = jdbc.queryForMap("""
                    SELECT s.label, c.name FROM guardian_chip_stat s
                    JOIN guardian_chip c ON c.id = s.chip_id WHERE s.id = ?
                    """, chipStatId);
            String entity = row.get("name") + " " + row.get("label");
            pendingRepo.record("CHIP", entity, String.valueOf(oldLevel), String.valueOf(level), null);
        }
    }

    // ── Presets ───────────────────────────────────────────────────────────────

    public List<Preset> getPresets() {
        return jdbc.query(
                "SELECT id, slot, name FROM guardian_preset ORDER BY slot",
                (rs, i) -> new Preset(rs.getInt("id"), rs.getInt("slot"), rs.getString("name")));
    }

    public int upsertPreset(int slot, String name) {
        jdbc.update("""
                INSERT INTO guardian_preset (slot, name) VALUES (?,?)
                ON CONFLICT(slot) DO UPDATE SET name = excluded.name
                """, slot, name);
        return jdbc.queryForObject(
                "SELECT id FROM guardian_preset WHERE slot = ?", Integer.class, slot);
    }

    public void deletePreset(int presetId) {
        jdbc.update("DELETE FROM guardian_preset WHERE id = ?", presetId);
    }

    public List<PresetChip> getPresetChips(int presetId) {
        return jdbc.query("""
                SELECT chip_id, active
                FROM guardian_preset_chip
                WHERE preset_id = ?
                ORDER BY chip_id
                """,
                (rs, i) -> new PresetChip(rs.getLong("chip_id"), rs.getInt("active") == 1),
                presetId);
    }

    public List<PresetStatLevel> getPresetStatLevels(int presetId) {
        return jdbc.query("""
                SELECT chip_stat_id, target_level
                FROM guardian_preset_stat_level
                WHERE preset_id = ?
                ORDER BY chip_stat_id
                """,
                (rs, i) -> new PresetStatLevel(rs.getLong("chip_stat_id"), rs.getInt("target_level")),
                presetId);
    }

    public void setPresetChips(int presetId, List<PresetChip> chips) {
        jdbc.update("DELETE FROM guardian_preset_chip WHERE preset_id = ?", presetId);
        if (chips.isEmpty()) return;
        jdbc.batchUpdate(
                "INSERT INTO guardian_preset_chip (preset_id, chip_id, active) VALUES (?,?,?)",
                chips.stream()
                        .map(c -> new Object[]{presetId, c.chipId(), c.active() ? 1 : 0})
                        .toList());
    }

    public long createChip(String code, String name, String source,
                           Integer unlockSeason, Integer unlockCostTokens,
                           List<StatInput> stats) {
        Long chipId = jdbc.queryForObject("""
                INSERT INTO guardian_chip (code, name, source, unlock_season, unlock_cost_tokens)
                VALUES (?,?,?,?,?) RETURNING id
                """, Long.class, code, name, source, unlockSeason, unlockCostTokens);
        jdbc.update("INSERT INTO guardian_chip_player_state (chip_id, acquired) VALUES (?,0)", chipId);

        for (StatInput stat : stats) {
            int maxLevel = stat.levels().size() - 1;
            Long statId = jdbc.queryForObject("""
                    INSERT INTO guardian_chip_stat (chip_id, stat_key, label, value_unit, max_level, sort_order)
                    VALUES (?,?,?,?,?,?) RETURNING id
                    """, Long.class,
                    chipId, stat.statKey(), stat.label(), stat.valueUnit(), maxLevel, stat.sortOrder());
            for (int i = 0; i < stat.levels().size(); i++) {
                LevelInput lv = stat.levels().get(i);
                jdbc.update(
                        "INSERT INTO guardian_chip_stat_level_value (chip_stat_id, level, value, bits_to_next) VALUES (?,?,?,?)",
                        statId, i, lv.value(), lv.bitsToNext());
            }
            jdbc.update("INSERT INTO guardian_chip_stat_player_level (chip_stat_id, current_level) VALUES (?,0)", statId);
        }
        return chipId;
    }

    public void setPresetStatLevels(int presetId, List<PresetStatLevel> levels) {
        jdbc.update("DELETE FROM guardian_preset_stat_level WHERE preset_id = ?", presetId);
        if (levels.isEmpty()) return;
        jdbc.batchUpdate(
                "INSERT INTO guardian_preset_stat_level (preset_id, chip_stat_id, target_level) VALUES (?,?,?)",
                levels.stream()
                        .map(l -> new Object[]{presetId, l.chipStatId(), l.targetLevel()})
                        .toList());
    }
}
