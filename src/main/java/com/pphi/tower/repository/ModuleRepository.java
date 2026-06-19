package com.pphi.tower.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ModuleRepository {

    private final JdbcTemplate                  jdbc;
    private final PendingVersionChangeRepository pendingRepo;

    public ModuleRepository(JdbcTemplate jdbc, PendingVersionChangeRepository pendingRepo) {
        this.jdbc        = jdbc;
        this.pendingRepo = pendingRepo;
    }

    // ── DTOs ──────────────────────────────────────────────────────────────────

    public record SubstatData(int slot, String key, String rarity, boolean locked) {}

    public record PresetAssignment(String preset, String slot) {}

    public record ModulePlayerData(
            int id,
            String code,
            String name,
            String type,
            String effectTemplate,
            Map<String, String> abilityValues,
            boolean owned,
            String rarity,
            int stars,
            int level,
            List<SubstatData> substats,
            List<String> copies,
            int shatteredEpics,
            List<PresetAssignment> presets
    ) {}

    // ── Reads ─────────────────────────────────────────────────────────────────

    @Cacheable("modules")
    public List<ModulePlayerData> getAll() {
        List<Map<String, Object>> defRows = jdbc.queryForList("""
                SELECT d.id, d.code, d.name, d.type, d.effect_template, d.sort_order,
                       s.owned, s.rarity, s.stars, s.level,
                       COALESCE(m.shattered_epics, 0) AS shattered_epics
                FROM module_def d
                LEFT JOIN module_player_state s ON s.module_def_id = d.id
                LEFT JOIN module_player_meta  m ON m.module_def_id = d.id
                ORDER BY d.sort_order
                """);

        List<Map<String, Object>> abilityRows = jdbc.queryForList(
                "SELECT module_def_id, rarity, value FROM module_ability_value ORDER BY module_def_id, CASE rarity WHEN 'Epic' THEN 1 WHEN 'Legendary' THEN 2 WHEN 'Mythic' THEN 3 WHEN 'Ancestral' THEN 4 END"
        );
        Map<Integer, Map<String, String>> abilityByModuleId = new LinkedHashMap<>();
        for (Map<String, Object> row : abilityRows) {
            int mid = ((Number) row.get("module_def_id")).intValue();
            abilityByModuleId.computeIfAbsent(mid, k -> new LinkedHashMap<>())
                    .put((String) row.get("rarity"), (String) row.get("value"));
        }

        List<Map<String, Object>> substatRows = jdbc.queryForList(
                "SELECT module_def_id, slot_index, substat_key, substat_rarity, locked FROM module_player_substat ORDER BY module_def_id, slot_index"
        );
        Map<Integer, List<SubstatData>> substatsByModuleId = new HashMap<>();
        for (Map<String, Object> row : substatRows) {
            int mid = ((Number) row.get("module_def_id")).intValue();
            substatsByModuleId.computeIfAbsent(mid, k -> new ArrayList<>()).add(new SubstatData(
                    ((Number) row.get("slot_index")).intValue(),
                    (String) row.get("substat_key"),
                    (String) row.get("substat_rarity"),
                    ((Number) row.get("locked")).intValue() != 0
            ));
        }

        List<Map<String, Object>> copyRows = jdbc.queryForList(
                "SELECT module_def_id, copy_index, copy_rarity FROM module_player_copy ORDER BY module_def_id, copy_index"
        );
        Map<Integer, List<String>> copiesByModuleId = new HashMap<>();
        for (Map<String, Object> row : copyRows) {
            int mid = ((Number) row.get("module_def_id")).intValue();
            copiesByModuleId.computeIfAbsent(mid, k -> new ArrayList<>())
                    .add((String) row.get("copy_rarity"));
        }

        List<Map<String, Object>> presetRows = jdbc.queryForList(
                "SELECT module_def_id, preset, slot FROM module_preset_assignment ORDER BY module_def_id, preset, slot"
        );
        Map<Integer, List<PresetAssignment>> presetsByModuleId = new HashMap<>();
        for (Map<String, Object> row : presetRows) {
            int mid = ((Number) row.get("module_def_id")).intValue();
            presetsByModuleId.computeIfAbsent(mid, k -> new ArrayList<>())
                    .add(new PresetAssignment((String) row.get("preset"), (String) row.get("slot")));
        }

        // Build a map of (preset, slot, type) -> moduleDefId so the UI can detect conflicts
        // This is returned per-module as the full presets list; conflict detection happens client-side.

        List<ModulePlayerData> result = new ArrayList<>();
        for (Map<String, Object> row : defRows) {
            int id = ((Number) row.get("id")).intValue();
            result.add(new ModulePlayerData(
                    id,
                    (String) row.get("code"),
                    (String) row.get("name"),
                    (String) row.get("type"),
                    (String) row.get("effect_template"),
                    abilityByModuleId.getOrDefault(id, Map.of()),
                    toBoolean(row.get("owned")),
                    getString(row, "rarity", "Epic"),
                    getInt(row, "stars", 0),
                    getInt(row, "level", 0),
                    substatsByModuleId.getOrDefault(id, List.of()),
                    copiesByModuleId.getOrDefault(id, List.of()),
                    getInt(row, "shattered_epics", 0),
                    presetsByModuleId.getOrDefault(id, List.of())
            ));
        }
        return result;
    }

    // ── Writes ────────────────────────────────────────────────────────────────

    @CacheEvict(value = "modules", allEntries = true)
    public void updateState(int moduleDefId, boolean owned, String rarity, int stars, int level) {
        Integer oldLevel = jdbc.queryForObject(
                "SELECT level FROM module_player_state WHERE module_def_id = ?",
                Integer.class, moduleDefId);

        jdbc.update("""
                INSERT INTO module_player_state (module_def_id, owned, rarity, stars, level)
                VALUES (?,?,?,?,?)
                ON CONFLICT(module_def_id) DO UPDATE SET
                    owned=excluded.owned, rarity=excluded.rarity, stars=excluded.stars,
                    level=excluded.level
                """, moduleDefId, owned ? 1 : 0, rarity, stars, level);

        if (oldLevel != null && level != oldLevel) {
            String name = jdbc.queryForObject("SELECT name FROM module_def WHERE id = ?", String.class, moduleDefId);
            pendingRepo.record("MODULE", name, String.valueOf(oldLevel), String.valueOf(level), null);
        }
    }

    @CacheEvict(value = "modules", allEntries = true)
    public void setSubstat(int moduleDefId, int slot, String key, String rarity, boolean locked) {
        jdbc.update("""
                INSERT INTO module_player_substat (module_def_id, slot_index, substat_key, substat_rarity, locked)
                VALUES (?,?,?,?,?)
                ON CONFLICT(module_def_id, slot_index) DO UPDATE SET
                    substat_key=excluded.substat_key, substat_rarity=excluded.substat_rarity,
                    locked=excluded.locked
                """, moduleDefId, slot, key, rarity, locked ? 1 : 0);
    }

    @CacheEvict(value = "modules", allEntries = true)
    public void clearSubstat(int moduleDefId, int slot) {
        jdbc.update("DELETE FROM module_player_substat WHERE module_def_id=? AND slot_index=?", moduleDefId, slot);
    }

    @CacheEvict(value = "modules", allEntries = true)
    public void setCopy(int moduleDefId, int copyIndex, String rarity) {
        jdbc.update("""
                INSERT INTO module_player_copy (module_def_id, copy_index, copy_rarity)
                VALUES (?,?,?)
                ON CONFLICT(module_def_id, copy_index) DO UPDATE SET copy_rarity=excluded.copy_rarity
                """, moduleDefId, copyIndex, rarity);
    }

    @CacheEvict(value = "modules", allEntries = true)
    public void clearCopy(int moduleDefId, int copyIndex) {
        jdbc.update("DELETE FROM module_player_copy WHERE module_def_id=? AND copy_index=?", moduleDefId, copyIndex);
    }

    @CacheEvict(value = "modules", allEntries = true)
    public void addPreset(int moduleDefId, String preset, String slot) {
        jdbc.update("""
                INSERT OR IGNORE INTO module_preset_assignment (preset, slot, module_def_id)
                VALUES (?,?,?)
                """, preset, slot, moduleDefId);
    }

    @CacheEvict(value = "modules", allEntries = true)
    public void removePreset(int moduleDefId, String preset, String slot) {
        jdbc.update(
                "DELETE FROM module_preset_assignment WHERE preset=? AND slot=? AND module_def_id=?",
                preset, slot, moduleDefId);
    }

    @CacheEvict(value = "modules", allEntries = true)
    public void setShatteredEpics(int moduleDefId, int count) {
        jdbc.update("""
                INSERT INTO module_player_meta (module_def_id, shattered_epics) VALUES (?,?)
                ON CONFLICT(module_def_id) DO UPDATE SET shattered_epics=excluded.shattered_epics
                """, moduleDefId, count);
    }

    // ── Substat catalog ───────────────────────────────────────────────────────

    public record SubstatDef(String key, String label, String minRarity) {}

    /** All substat definitions for a given module type, ordered as seeded. */
    public List<SubstatDef> getSubstatDefs(String moduleType) {
        return jdbc.query(
                "SELECT key, label, min_rarity FROM module_substat_def WHERE module_type = ? ORDER BY rowid",
                (rs, i) -> new SubstatDef(rs.getString("key"), rs.getString("label"), rs.getString("min_rarity")),
                moduleType);
    }

    /** All substat definitions across all module types, keyed by type. */
    public Map<String, List<SubstatDef>> getAllSubstatDefs() {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT module_type, key, label, min_rarity FROM module_substat_def ORDER BY module_type, rowid");
        Map<String, List<SubstatDef>> result = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String type = (String) row.get("module_type");
            result.computeIfAbsent(type, k -> new ArrayList<>())
                    .add(new SubstatDef((String) row.get("key"), (String) row.get("label"), (String) row.get("min_rarity")));
        }
        return result;
    }

    // ── Effect bans ───────────────────────────────────────────────────────────

    public record BanState(String moduleType, int maxBans, List<String> banned) {}

    private static final Map<String, String> BAN_LAB_NAMES = Map.of(
            "Cannon",    "Cannon Effect Bans",
            "Armor",     "Armor Effect Bans",
            "Generator", "Generator Effect Bans",
            "Core",      "Core Effect Bans"
    );

    /** Current bans and max-ban count (from lab level) for all four module types. */
    public List<BanState> getAllBanStates() {
        List<Map<String, Object>> banRows = jdbc.queryForList(
                "SELECT module_type, substat_key FROM module_substat_ban ORDER BY module_type, substat_key");
        Map<String, List<String>> bannedByType = new HashMap<>();
        for (Map<String, Object> row : banRows) {
            bannedByType.computeIfAbsent((String) row.get("module_type"), k -> new ArrayList<>())
                    .add((String) row.get("substat_key"));
        }

        return BAN_LAB_NAMES.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    String type    = e.getKey();
                    String labName = e.getValue();
                    Integer labLevel = jdbc.queryForObject("""
                            SELECT COALESCE(lps.current_level, 0)
                            FROM lab l
                            LEFT JOIN lab_player_state lps ON lps.lab_id = l.id
                            WHERE l.name = ?
                            """, Integer.class, labName);
                    int maxBans = labLevel != null ? labLevel : 0;
                    return new BanState(type, maxBans, bannedByType.getOrDefault(type, List.of()));
                })
                .toList();
    }

    /** Add a ban for a module type. Caller must verify the ban limit before calling. */
    public void addBan(String moduleType, String substatKey) {
        jdbc.update("""
                INSERT OR IGNORE INTO module_substat_ban (module_type, substat_key) VALUES (?, ?)
                """, moduleType, substatKey);
    }

    /** Remove a ban for a module type. */
    public void removeBan(String moduleType, String substatKey) {
        jdbc.update(
                "DELETE FROM module_substat_ban WHERE module_type = ? AND substat_key = ?",
                moduleType, substatKey);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static boolean toBoolean(Object v) {
        return v != null && ((Number) v).intValue() != 0;
    }

    private static int getInt(Map<String, Object> row, String key, int def) {
        Object v = row.get(key);
        return v != null ? ((Number) v).intValue() : def;
    }

    private static String getString(Map<String, Object> row, String key, String def) {
        Object v = row.get(key);
        return v != null ? (String) v : def;
    }
}
