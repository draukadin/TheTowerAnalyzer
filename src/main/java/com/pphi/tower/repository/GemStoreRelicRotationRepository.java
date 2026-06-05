package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GemStoreRelicRotationRepository {

    private final JdbcTemplate jdbc;

    public GemStoreRelicRotationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record RotationEntry(long id, String startDate, String slot, String variant,
                                long relicId, String relicName, String rarity,
                                String bonusStat, double bonusValue) {}

    public List<RotationEntry> getAll() {
        return jdbc.query("""
                SELECT g.id, g.start_date, g.slot, g.variant,
                       r.id AS relic_id, r.name, r.rarity, r.bonus_stat, r.bonus_value
                FROM gem_store_relic_rotation g
                JOIN relic r ON r.id = g.relic_id
                ORDER BY g.start_date DESC, g.variant, g.slot
                """,
                (rs, i) -> new RotationEntry(
                        rs.getLong("id"),
                        rs.getString("start_date"),
                        rs.getString("slot"),
                        rs.getString("variant"),
                        rs.getLong("relic_id"),
                        rs.getString("name"),
                        rs.getString("rarity"),
                        rs.getString("bonus_stat"),
                        rs.getDouble("bonus_value")
                ));
    }

    public List<RotationEntry> getCurrentRotation() {
        return jdbc.query("""
                SELECT g.id, g.start_date, g.slot, g.variant,
                       r.id AS relic_id, r.name, r.rarity, r.bonus_stat, r.bonus_value
                FROM gem_store_relic_rotation g
                JOIN relic r ON r.id = g.relic_id
                WHERE g.start_date = (SELECT MAX(start_date) FROM gem_store_relic_rotation)
                ORDER BY g.variant, g.slot
                """,
                (rs, i) -> new RotationEntry(
                        rs.getLong("id"),
                        rs.getString("start_date"),
                        rs.getString("slot"),
                        rs.getString("variant"),
                        rs.getLong("relic_id"),
                        rs.getString("name"),
                        rs.getString("rarity"),
                        rs.getString("bonus_stat"),
                        rs.getDouble("bonus_value")
                ));
    }

    /**
     * Adds a standard (non-variant) weekly rotation. relicNames order: standard1, standard2, premium1, premium2.
     */
    public void addRotation(String startDate, String standard1, String standard2,
                            String premium1, String premium2) {
        addEntry(startDate, "STANDARD_1", standard1, "");
        addEntry(startDate, "STANDARD_2", standard2, "");
        addEntry(startDate, "PREMIUM_1",  premium1,  "");
        addEntry(startDate, "PREMIUM_2",  premium2,  "");
    }

    public void addEntry(String startDate, String slot, String relicName, String variant) {
        int rows = jdbc.update("""
                INSERT OR IGNORE INTO gem_store_relic_rotation (start_date, slot, relic_id, variant)
                SELECT ?, ?, id, ? FROM relic WHERE name = ?
                """, startDate, slot, variant, relicName);
        if (rows == 0) {
            throw new IllegalArgumentException("Relic not found or entry already exists: " + relicName);
        }
    }
}
