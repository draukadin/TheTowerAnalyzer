package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VersionHistoryRepository {

    private final JdbcTemplate jdbc;

    public VersionHistoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record VersionEntry(String version, String type, String summary) {}

    public record VersionChange(long id, String version, String category,
                                String entityName, String oldValue, String newValue, String notes) {}

    public List<VersionEntry> getAllVersions() {
        return jdbc.query("""
                SELECT version, type, summary FROM tower_version
                ORDER BY version DESC
                """,
                (rs, i) -> new VersionEntry(
                        rs.getString("version"),
                        rs.getString("type"),
                        rs.getString("summary")));
    }

    public List<VersionChange> getChangesForVersion(String version) {
        return jdbc.query("""
                SELECT id, version, category, entity_name, old_value, new_value, notes
                FROM tower_version_change
                WHERE version = ?
                ORDER BY id
                """,
                (rs, i) -> new VersionChange(
                        rs.getLong("id"),
                        rs.getString("version"),
                        rs.getString("category"),
                        rs.getString("entity_name"),
                        rs.getString("old_value"),
                        rs.getString("new_value"),
                        rs.getString("notes")),
                version);
    }

    public record NewChange(String category, String entityName,
                            String oldValue, String newValue, String notes) {}

    public void create(String version, String type, List<NewChange> changes) {
        jdbc.update("INSERT INTO tower_version (version, type, summary) VALUES (?,?,?)",
                version, type, buildSummary(changes));
        for (NewChange c : changes) {
            jdbc.update("""
                    INSERT INTO tower_version_change (version, category, entity_name, old_value, new_value, notes)
                    VALUES (?,?,?,?,?,?)
                    """, version, c.category(), c.entityName(), c.oldValue(), c.newValue(), c.notes());
        }
    }

    public void update(String version, String type, List<NewChange> changes) {
        jdbc.update("UPDATE tower_version SET type=?, summary=? WHERE version=?",
                type, buildSummary(changes), version);
        jdbc.update("DELETE FROM tower_version_change WHERE version=?", version);
        for (NewChange c : changes) {
            jdbc.update("""
                    INSERT INTO tower_version_change (version, category, entity_name, old_value, new_value, notes)
                    VALUES (?,?,?,?,?,?)
                    """, version, c.category(), c.entityName(), c.oldValue(), c.newValue(), c.notes());
        }
    }

    private String buildSummary(List<NewChange> changes) {
        return changes.stream()
                .map(c -> c.oldValue() != null
                        ? c.entityName() + " " + c.oldValue() + " → " + c.newValue()
                        : c.entityName() + " " + c.newValue())
                .reduce((a, b) -> a + "; " + b)
                .orElse("");
    }

}
