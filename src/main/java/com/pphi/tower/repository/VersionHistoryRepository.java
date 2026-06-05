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

    public String toMarkdownContext() {
        List<VersionEntry> versions = getAllVersions();
        StringBuilder sb = new StringBuilder();
        sb.append("## Version History\n\n");
        sb.append("| Version | Type | Summary |\n");
        sb.append("|---------|------|---------|\n");
        for (VersionEntry v : versions) {
            sb.append("| ").append(v.version())
              .append(" | ").append(v.type())
              .append(" | ").append(v.summary())
              .append(" |\n");
        }
        return sb.toString();
    }
}
