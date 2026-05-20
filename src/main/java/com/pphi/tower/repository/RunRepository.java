package com.pphi.tower.repository;

import com.pphi.tower.web.dto.ReportSummaryDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
                    rs.getString("folder"),
                    rs.getString("battle_date"),
                    rs.getInt("tier"),
                    rs.getInt("wave"),
                    rs.getString("tower_era"),
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

    public boolean existsByFolderAndFilename(String folder, String filename) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM runs WHERE folder = ? AND filename = ?",
                Integer.class, folder, filename);
        return count != null && count > 0;
    }

    public void insert(String id, String filename, String folder, String battleDate,
                       int tier, int wave, double cellsEarned, long realTimeSeconds,
                       long gameTimeSeconds, double cellsPerHour, double coinsPerHour,
                       String killedBy, String towerEra, String payloadJson,
                       long battleEpochSeconds) {
        jdbc.update("""
                INSERT INTO runs (id, filename, folder, battle_date, tier, wave,
                    cells_earned, real_time_seconds, game_time_seconds,
                    cells_per_hour, coins_per_hour, killed_by, tower_era, payload,
                    battle_epoch_seconds)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id, filename, folder, battleDate, tier, wave,
                cellsEarned, realTimeSeconds, gameTimeSeconds,
                cellsPerHour, coinsPerHour, killedBy, towerEra, payloadJson,
                battleEpochSeconds);
    }

    public List<ReportSummaryDto> findAllSummaries() {
        return jdbc.query(
                "SELECT * FROM runs ORDER BY battle_date DESC",
                SUMMARY_MAPPER);
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

    public List<ReportSummaryDto> findByFolder(String folder) {
        return jdbc.query(
                "SELECT * FROM runs WHERE folder = ? ORDER BY battle_date DESC",
                SUMMARY_MAPPER, folder);
    }
}
