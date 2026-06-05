package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RelicRepository {

    private final JdbcTemplate jdbc;

    public RelicRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record RelicData(long id, String name, String rarity, String type,
                            String bonusStat, double bonusValue, String obtainCondition,
                            boolean owned) {}

    public List<RelicData> getAll() {
        return jdbc.query("""
                SELECT r.id, r.name, r.rarity, r.type, r.bonus_stat, r.bonus_value,
                       r.obtain_condition, COALESCE(ps.owned, 0) AS owned
                FROM relic r
                LEFT JOIN relic_player_state ps ON ps.relic_id = r.id
                ORDER BY r.type, r.rarity, r.name
                """,
                (rs, i) -> new RelicData(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("rarity"),
                        rs.getString("type"),
                        rs.getString("bonus_stat"),
                        rs.getDouble("bonus_value"),
                        rs.getString("obtain_condition"),
                        rs.getInt("owned") == 1
                ));
    }

    public void setOwned(long id, boolean owned) {
        jdbc.update("""
                INSERT INTO relic_player_state (relic_id, owned) VALUES (?, ?)
                ON CONFLICT(relic_id) DO UPDATE SET owned = excluded.owned
                """, id, owned ? 1 : 0);
    }

    public String toMarkdownContext() {
        List<RelicData> relics = getAll();
        long owned = relics.stream().filter(RelicData::owned).count();
        StringBuilder sb = new StringBuilder();
        sb.append("## Relics (").append(owned).append(" / ").append(relics.size()).append(" owned)\n\n");
        sb.append("| Name | Rarity | Type | Stat | Value | Owned |\n");
        sb.append("|------|--------|------|------|-------|-------|\n");
        for (RelicData r : relics) {
            sb.append("| ").append(r.name())
              .append(" | ").append(r.rarity())
              .append(" | ").append(r.type())
              .append(" | ").append(r.bonusStat())
              .append(" | ").append(formatValue(r.bonusStat(), r.bonusValue()))
              .append(" | ").append(r.owned() ? "Yes" : "No")
              .append(" |\n");
        }
        return sb.toString();
    }

    private String formatValue(String stat, double value) {
        if ("Bot Range".equals(stat)) return (int) value + "m";
        if ("Wall Rebuild".equals(stat)) return (int) value + "s";
        return Math.round(value * 100) + "%";
    }

    public long create(String name, String rarity, String type, String bonusStat,
                       double bonusValue, String obtainCondition) {
        Long id = jdbc.queryForObject(
                "INSERT INTO relic (name, rarity, type, bonus_stat, bonus_value, obtain_condition) " +
                "VALUES (?,?,?,?,?,?) RETURNING id",
                Long.class, name, rarity, type, bonusStat, bonusValue, obtainCondition);
        if (id == null) throw new RuntimeException("Failed to insert relic");
        jdbc.update("INSERT INTO relic_player_state (relic_id, owned) VALUES (?, 0)", id);
        return id;
    }
}
