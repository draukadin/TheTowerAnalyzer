package com.pphi.tower.repository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    @Cacheable("relics")
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

    @Caching(evict = {
        @CacheEvict(value = "relics", allEntries = true),
        @CacheEvict(value = "lab-multipliers", allEntries = true),
        @CacheEvict(value = "lab-slots", allEntries = true)
    })
    public void setOwned(long id, boolean owned) {
        jdbc.update("""
                INSERT INTO relic_player_state (relic_id, owned) VALUES (?, ?)
                ON CONFLICT(relic_id) DO UPDATE SET owned = excluded.owned
                """, id, owned ? 1 : 0);
    }

    @Caching(evict = {
        @CacheEvict(value = "relics", allEntries = true),
        @CacheEvict(value = "lab-multipliers", allEntries = true),
        @CacheEvict(value = "lab-slots", allEntries = true)
    })
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

    @Caching(evict = {
        @CacheEvict(value = "relics", allEntries = true),
        @CacheEvict(value = "lab-multipliers", allEntries = true),
        @CacheEvict(value = "lab-slots", allEntries = true)
    })
    public void update(long id, String name, String rarity, String type, String bonusStat,
                       double bonusValue, String obtainCondition) {
        jdbc.update("""
                UPDATE relic SET name = ?, rarity = ?, type = ?, bonus_stat = ?,
                       bonus_value = ?, obtain_condition = ?
                WHERE id = ?
                """, name, rarity, type, bonusStat, bonusValue, obtainCondition, id);
    }

    @Caching(evict = {
        @CacheEvict(value = "relics", allEntries = true),
        @CacheEvict(value = "lab-multipliers", allEntries = true),
        @CacheEvict(value = "lab-slots", allEntries = true)
    })
    public void delete(long id) {
        jdbc.update("DELETE FROM relic_player_state WHERE relic_id = ?", id);
        jdbc.update("DELETE FROM relic WHERE id = ?", id);
    }

    /** True when another relic (id != excludeId) already uses this name. Pass excludeId &lt; 0 for create. */
    public boolean nameExists(String name, long excludeId) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM relic WHERE name = ? AND id <> ?", Integer.class, name, excludeId);
        return n != null && n > 0;
    }

    /** True when the relic is referenced by the Gem Store rotation (built-in reference data). */
    public boolean isInGemStoreRotation(long id) {
        Integer n = jdbc.queryForObject(
                "SELECT COUNT(*) FROM gem_store_relic_rotation WHERE relic_id = ?", Integer.class, id);
        return n != null && n > 0;
    }
}
