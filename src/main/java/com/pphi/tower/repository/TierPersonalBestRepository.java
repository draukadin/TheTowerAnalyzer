package com.pphi.tower.repository;

import com.pphi.tower.model.DissonanceType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TierPersonalBestRepository {

    private final JdbcTemplate jdbc;

    public TierPersonalBestRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record TierPb(int tier, int wave, int attackWaves, int defenseWaves, int utilityWaves, int uwWaves) {}

    private static final RowMapper<TierPb> ROW_MAPPER = (rs, i) -> new TierPb(
            rs.getInt("tier"),
            rs.getInt("wave"),
            rs.getInt("attack_waves"),
            rs.getInt("defense_waves"),
            rs.getInt("utility_waves"),
            rs.getInt("uw_waves")
    );

    public List<TierPb> findAll() {
        return jdbc.query("SELECT * FROM tier_personal_best ORDER BY tier", ROW_MAPPER);
    }

    public void createTier(int tier) {
        jdbc.update("""
                INSERT INTO tier_personal_best (tier, wave, attack_waves, defense_waves, utility_waves, uw_waves)
                VALUES (?, 0, 0, 0, 0, 0)
                ON CONFLICT(tier) DO NOTHING
                """, tier);
    }

    public void updateWave(int tier, int wave) {
        jdbc.update("UPDATE tier_personal_best SET wave = ? WHERE tier = ?", wave, tier);
    }

    public void updateDissonanceWaves(int tier, DissonanceType type, int waves) {
        String column = switch (type) {
            case ATTACK -> "attack_waves";
            case DEFENSE -> "defense_waves";
            case UTILITY -> "utility_waves";
            case UW -> "uw_waves";
        };
        jdbc.update("UPDATE tier_personal_best SET " + column + " = ? WHERE tier = ?", waves, tier);
    }
}
