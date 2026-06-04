package com.pphi.tower.repository;

import com.pphi.tower.model.TowerEra;
import com.pphi.tower.web.dto.ReportSummaryDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class RunRepository {

    private final JdbcTemplate jdbc;

    public RunRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<ReportSummaryDto> SUMMARY_MAPPER = (rs, rowNum) ->
            new ReportSummaryDto(
                    rs.getString("id"),
                    rs.getString("filename"),
                    rs.getString("run_type"),
                    rs.getString("battle_date"),
                    rs.getInt("tier"),
                    rs.getInt("wave"),
                    TowerEra.parse(rs.getString("tower_era")),
                    rs.getString("killed_by"),
                    rs.getDouble("cells_earned"),
                    rs.getDouble("cells_per_hour"),
                    rs.getDouble("coins_per_hour"),
                    rs.getLong("real_time_seconds"),
                    rs.getLong("game_time_seconds"),
                    rs.getLong("battle_epoch_seconds"));

    public boolean existsById(String id) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM runs WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    public void insert(String id, String filename, String runType, String battleDate,
                       int tier, int wave, double cellsEarned, long realTimeSeconds,
                       long gameTimeSeconds, double cellsPerHour, double coinsPerHour,
                       String killedBy, TowerEra towerEra, String payloadJson,
                       long battleEpochSeconds) {
        jdbc.update("""
                INSERT INTO runs (id, filename, run_type, battle_date, tier, wave,
                    cells_earned, real_time_seconds, game_time_seconds,
                    cells_per_hour, coins_per_hour, killed_by, tower_era, payload,
                    battle_epoch_seconds)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, filename, runType, battleDate, tier, wave,
                cellsEarned, realTimeSeconds, gameTimeSeconds,
                cellsPerHour, coinsPerHour, killedBy,
                towerEra != null ? towerEra.toString() : null,
                payloadJson, battleEpochSeconds);
    }

    public List<ReportSummaryDto> findAllSummaries() {
        List<ReportSummaryDto> rows = jdbc.query("SELECT * FROM runs", SUMMARY_MAPPER);
        rows.sort(BY_VERSION_DESC_DATE_DESC);
        return rows;
    }

    public Optional<ReportSummaryDto> findSummaryById(String id) {
        List<ReportSummaryDto> rows = jdbc.query(
                "SELECT * FROM runs WHERE id = ?", SUMMARY_MAPPER, id);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public Optional<String> findPayloadById(String id) {
        List<String> results = jdbc.query(
                "SELECT payload FROM runs WHERE id = ?",
                (rs, rowNum) -> rs.getString("payload"), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<ReportSummaryDto> findByDateWindow(LocalDate from, LocalDate to) {
        return jdbc.query(
                "SELECT * FROM runs WHERE battle_date >= ? AND battle_date <= ? ORDER BY battle_date ASC",
                SUMMARY_MAPPER,
                from.toString(), to.toString());
    }

    public record ShardRunRow(
            String id, String battleDate, int tier, int wave,
            TowerEra towerEra, long realTimeSeconds, String payload) {}

    public List<ShardRunRow> findFarmingRunsWithPayload(LocalDate from, LocalDate to) {
        return jdbc.query("""
                SELECT id, battle_date, tier, wave, tower_era, real_time_seconds, payload
                FROM runs
                WHERE battle_date >= ? AND battle_date <= ?
                  AND LOWER(run_type) NOT IN ('tournament','dissonance','event')
                ORDER BY battle_date ASC
                """,
                (rs, rn) -> new ShardRunRow(
                        rs.getString("id"),
                        rs.getString("battle_date"),
                        rs.getInt("tier"),
                        rs.getInt("wave"),
                        TowerEra.parse(rs.getString("tower_era")),
                        rs.getLong("real_time_seconds"),
                        rs.getString("payload")),
                from.toString(), to.toString());
    }

    public List<ShardRunRow> findAllRunsWithPayload(LocalDate from, LocalDate to) {
        return jdbc.query("""
                SELECT id, battle_date, tier, wave, tower_era, real_time_seconds, payload
                FROM runs
                WHERE battle_date >= ? AND battle_date <= ?
                ORDER BY battle_date ASC
                """,
                (rs, rn) -> new ShardRunRow(
                        rs.getString("id"),
                        rs.getString("battle_date"),
                        rs.getInt("tier"),
                        rs.getInt("wave"),
                        TowerEra.parse(rs.getString("tower_era")),
                        rs.getLong("real_time_seconds"),
                        rs.getString("payload")),
                from.toString(), to.toString());
    }

    public List<ReportSummaryDto> findByRunType(String runType) {
        List<ReportSummaryDto> rows = jdbc.query(
                "SELECT * FROM runs WHERE run_type = ?", SUMMARY_MAPPER, runType);
        rows.sort(BY_VERSION_DESC_DATE_DESC);
        return rows;
    }

    // -------------------------------------------------------------------------
    // Sorting
    // -------------------------------------------------------------------------

    private static final TowerEra ZERO = new TowerEra(0, 0, 0);

    private static final Comparator<ReportSummaryDto> BY_VERSION_DESC_DATE_DESC = (a, b) -> {
        int v = (b.towerEra() != null ? b.towerEra() : ZERO)
                .compareTo(a.towerEra() != null ? a.towerEra() : ZERO);
        if (v != 0) return v;
        String da = a.battleDate() != null ? a.battleDate() : "";
        String db = b.battleDate() != null ? b.battleDate() : "";
        return db.compareTo(da);
    };
}
