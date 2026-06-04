package com.pphi.tower.repository;

import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.sheets.Currencies;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CurrencySnapshotRepository {

    private final JdbcTemplate jdbc;

    public CurrencySnapshotRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ── Row types ─────────────────────────────────────────────────────────────

    public record Module(String name, String type, int level) {}

    public record CurrencySnapshot(
            LocalDateTime time,
            int cannonShards, int armorShards,
            int generatorShards, int coreShards) {}

    public record ModuleLevelSnapshot(
            LocalDateTime time, String name, String type, int level) {}

    public record SnapshotPair(
            LocalDateTime earlierTime, LocalDateTime laterTime,
            int earlierCannon, int laterCannon,
            int earlierArmor,  int laterArmor,
            int earlierGenerator, int laterGenerator,
            int earlierCore,   int laterCore) {}

    // ── Row mappers ───────────────────────────────────────────────────────────

    private static final RowMapper<CurrencySnapshot> SNAPSHOT_MAPPER = (rs, rn) ->
            new CurrencySnapshot(
                    rs.getTimestamp("snapshot_time").toLocalDateTime(),
                    rs.getInt("cannon_shards"),
                    rs.getInt("armor_shards"),
                    rs.getInt("generator_shards"),
                    rs.getInt("core_shards"));

    private static final RowMapper<ModuleLevelSnapshot> MODULE_MAPPER = (rs, rn) ->
            new ModuleLevelSnapshot(
                    rs.getTimestamp("snapshot_time").toLocalDateTime(),
                    rs.getString("module_name"),
                    rs.getString("module_type"),
                    rs.getInt("level"));

    // ── Writes ────────────────────────────────────────────────────────────────

    public void saveCurrencySnapshot(LocalDateTime snapshotTime, Currencies currencies) {
        double eliteCellsRaw = toRaw(currencies.eliteCells());
        jdbc.update("""
                INSERT INTO currency_snapshots
                (snapshot_time, cannon_shards, armor_shards, generator_shards,
                 core_shards, reroll_shards, gems, stones, medals,
                 elite_cells, tokens, bits)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                snapshotTime,
                currencies.cannonShards(), currencies.armorShards(),
                currencies.generatorShards(), currencies.coreShards(),
                currencies.reRollShards(), currencies.gems(),
                currencies.stones(), currencies.medals(),
                eliteCellsRaw,
                currencies.tokens(), currencies.bits());
    }

    public void saveModuleLevelSnapshots(LocalDateTime snapshotTime, List<Module> modules) {
        for (Module m : modules) {
            jdbc.update("""
                    INSERT INTO module_level_snapshots
                    (snapshot_time, module_name, module_type, level)
                    VALUES (?, ?, ?, ?)
                    """,
                    snapshotTime, m.name(), m.type(), m.level());
        }
    }

    // ── Reads ─────────────────────────────────────────────────────────────────

    public List<SnapshotPair> findSnapshotPairsInWindow(LocalDateTime from, LocalDateTime to) {
        List<CurrencySnapshot> snapshots = jdbc.query("""
                SELECT * FROM currency_snapshots
                WHERE snapshot_time >= ? AND snapshot_time <= ?
                ORDER BY snapshot_time ASC
                """, SNAPSHOT_MAPPER, from, to);

        List<SnapshotPair> pairs = new ArrayList<>();
        for (int i = 0; i < snapshots.size() - 1; i++) {
            CurrencySnapshot a = snapshots.get(i);
            CurrencySnapshot b = snapshots.get(i + 1);
            pairs.add(new SnapshotPair(
                    a.time(), b.time(),
                    a.cannonShards(),    b.cannonShards(),
                    a.armorShards(),     b.armorShards(),
                    a.generatorShards(), b.generatorShards(),
                    a.coreShards(),      b.coreShards()));
        }
        return pairs;
    }

    public Map<LocalDateTime, List<ModuleLevelSnapshot>> findModuleLevelsBetween(
            LocalDateTime from, LocalDateTime to) {
        return jdbc.query("""
                SELECT * FROM module_level_snapshots
                WHERE snapshot_time >= ? AND snapshot_time <= ?
                ORDER BY snapshot_time ASC
                """, MODULE_MAPPER, from, to)
                .stream()
                .collect(Collectors.groupingBy(ModuleLevelSnapshot::time));
    }

    public int countSnapshots() {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM currency_snapshots", Integer.class);
        return count != null ? count : 0;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static double toRaw(TowerNumber tn) {
        if (tn == null || tn.amount() == null) return 0.0;
        if (tn.scaleSuffix() == null) return tn.amount().doubleValue();
        return tn.amount().multiply(tn.scaleSuffix().getScientificNotation()).doubleValue();
    }
}
