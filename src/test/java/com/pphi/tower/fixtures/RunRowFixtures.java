package com.pphi.tower.fixtures;

import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

public final class RunRowFixtures {

    private RunRowFixtures() {}

    /**
     * Inserts a farming run row with the minimum required columns populated.
     * battle_epoch_seconds is set to the epoch-day * 86400 so CellIncomeService
     * dead-time gap computation works with these rows.
     */
    public static void insertFarmingRun(
            JdbcTemplate jdbc,
            String id,
            LocalDate battleDate,
            int tier,
            int wave,
            double cellsEarned,
            long realTimeSec,
            String payloadJson) {

        long epochSeconds = battleDate.toEpochDay() * 86_400L;
        double cellsPerHour = realTimeSec > 0
                ? cellsEarned / (realTimeSec / 3600.0)
                : 0.0;

        jdbc.update("""
                INSERT INTO runs (
                    id, filename, run_type, battle_date, tier, wave,
                    cells_earned, real_time_seconds, game_time_seconds,
                    cells_per_hour, coins_per_hour, killed_by, tower_era,
                    payload, battle_epoch_seconds, run_number, content_hash
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                id + ".txt",
                "farming",
                battleDate.toString(),
                tier,
                wave,
                cellsEarned,
                realTimeSec,
                realTimeSec,
                cellsPerHour,
                0.0,
                "Natural",
                "1.0.0",
                payloadJson,
                epochSeconds,
                null,
                id
        );
    }

    /**
     * Inserts a tournament run row. Useful for report controller tests that
     * filter by run_type.
     */
    public static void insertTournamentRun(
            JdbcTemplate jdbc,
            String id,
            LocalDate battleDate,
            int tier,
            int wave,
            String payloadJson) {

        long epochSeconds = battleDate.toEpochDay() * 86_400L;

        jdbc.update("""
                INSERT INTO runs (
                    id, filename, run_type, battle_date, tier, wave,
                    cells_earned, real_time_seconds, game_time_seconds,
                    cells_per_hour, coins_per_hour, killed_by, tower_era,
                    payload, battle_epoch_seconds, run_number, content_hash
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                id + ".txt",
                "tournament",
                battleDate.toString(),
                tier,
                wave,
                0.0,
                3600L,
                3600L,
                0.0,
                0.0,
                "Natural",
                "1.0.0",
                payloadJson,
                epochSeconds,
                null,
                id
        );
    }
}