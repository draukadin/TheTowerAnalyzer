package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class TournamentRepository {

    private final JdbcTemplate jdbc;

    public TournamentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record BattleConditionData(long id, String name, String acronym, String category) {}

    public record TournamentData(long id, String date, String league, List<BattleConditionData> conditions) {}

    public List<BattleConditionData> getAllConditions() {
        return jdbc.query("""
                SELECT id, name, acronym, category
                FROM battle_condition
                ORDER BY category, name
                """,
                (rs, i) -> new BattleConditionData(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("acronym"),
                        rs.getString("category")
                ));
    }

    public List<TournamentData> getAll() {
        List<TournamentData> tournaments = jdbc.query("""
                SELECT id, date, league FROM tournament ORDER BY date DESC, league
                """,
                (rs, i) -> new TournamentData(
                        rs.getLong("id"),
                        rs.getString("date"),
                        rs.getString("league"),
                        null
                ));

        return tournaments.stream()
                .map(t -> new TournamentData(t.id(), t.date(), t.league(), getConditions(t.id())))
                .toList();
    }

    private List<BattleConditionData> getConditions(long tournamentId) {
        return jdbc.query("""
                SELECT bc.id, bc.name, bc.acronym, bc.category
                FROM tournament_condition tc
                JOIN battle_condition bc ON bc.id = tc.condition_id
                WHERE tc.tournament_id = ?
                ORDER BY bc.category, bc.name
                """,
                (rs, i) -> new BattleConditionData(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("acronym"),
                        rs.getString("category")
                ),
                tournamentId);
    }

    @Transactional
    public long save(String date, String league, List<Long> conditionIds) {
        List<Long> found = jdbc.query(
                "SELECT id FROM tournament WHERE date = ? AND league = ?",
                (rs, i) -> rs.getLong("id"), date, league);
        Long existing = found.isEmpty() ? null : found.get(0);

        long tournamentId;
        if (existing != null) {
            tournamentId = existing;
            jdbc.update("DELETE FROM tournament_condition WHERE tournament_id = ?", tournamentId);
        } else {
            Long id = jdbc.queryForObject(
                    "INSERT INTO tournament (date, league) VALUES (?, ?) RETURNING id",
                    Long.class, date, league);
            if (id == null) throw new RuntimeException("Failed to insert tournament");
            tournamentId = id;
        }

        for (long conditionId : conditionIds) {
            jdbc.update("""
                    INSERT INTO tournament_condition (tournament_id, condition_id) VALUES (?, ?)
                    """, tournamentId, conditionId);
        }

        return tournamentId;
    }

    public long createCondition(String name, String acronym, String category) {
        Long id = jdbc.queryForObject("""
                INSERT INTO battle_condition (name, acronym, category) VALUES (?, ?, ?) RETURNING id
                """, Long.class, name, acronym, category);
        if (id == null) throw new RuntimeException("Failed to insert battle_condition");
        return id;
    }

    public void delete(long id) {
        jdbc.update("DELETE FROM tournament WHERE id = ?", id);
    }

    public List<TournamentData> findByConditions(List<Long> conditionIds) {
        if (conditionIds.isEmpty()) return List.of();

        String placeholders = "?,".repeat(conditionIds.size());
        placeholders = placeholders.substring(0, placeholders.length() - 1);

        String sql = String.format("""
                SELECT t.id, t.date, t.league
                FROM tournament t
                JOIN tournament_condition tc ON tc.tournament_id = t.id
                WHERE tc.condition_id IN (%s)
                GROUP BY t.id
                HAVING COUNT(DISTINCT tc.condition_id) = %d
                ORDER BY t.date DESC
                """, placeholders, conditionIds.size());

        List<TournamentData> tournaments = jdbc.query(sql,
                (rs, i) -> new TournamentData(
                        rs.getLong("id"),
                        rs.getString("date"),
                        rs.getString("league"),
                        null
                ),
                conditionIds.toArray());

        return tournaments.stream()
                .map(t -> new TournamentData(t.id(), t.date(), t.league(), getConditions(t.id())))
                .toList();
    }
}
