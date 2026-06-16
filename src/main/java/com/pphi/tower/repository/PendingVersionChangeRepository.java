package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PendingVersionChangeRepository {

    private final JdbcTemplate jdbc;

    public PendingVersionChangeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record PendingChange(long id, String category, String entityName,
                                String oldValue, String newValue, String notes,
                                String createdAt) {}

    public List<PendingChange> getAll() {
        return jdbc.query("""
                SELECT id, category, entity_name, old_value, new_value, notes, created_at
                FROM pending_version_change
                ORDER BY created_at, id
                """,
                (rs, i) -> new PendingChange(
                        rs.getLong("id"),
                        rs.getString("category"),
                        rs.getString("entity_name"),
                        rs.getString("old_value"),
                        rs.getString("new_value"),
                        rs.getString("notes"),
                        rs.getString("created_at")));
    }

    public void record(String category, String entityName, String oldValue, String newValue, String notes) {
        jdbc.update("""
                INSERT INTO pending_version_change (category, entity_name, old_value, new_value, notes)
                VALUES (?,?,?,?,?)
                """, category, entityName, oldValue, newValue, notes);
    }

    public void deleteById(long id) {
        jdbc.update("DELETE FROM pending_version_change WHERE id=?", id);
    }

    public void deleteAll() {
        jdbc.update("DELETE FROM pending_version_change");
    }
}
