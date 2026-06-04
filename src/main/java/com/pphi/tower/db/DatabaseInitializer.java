package com.pphi.tower.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DatabaseInitializer {

    private final JdbcTemplate jdbc;

    public DatabaseInitializer(JdbcTemplate jdbc) throws IOException {
        this.jdbc = jdbc;
        ensureDbDirectory();
        createSchema();
    }

    private void ensureDbDirectory() throws IOException {
        String appData = System.getenv("APPDATA");
        Path dir = Path.of(appData, "TheTowerAnalyzer");
        Files.createDirectories(dir);
    }

    private void createSchema() {
        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS runs (
                    id                  TEXT PRIMARY KEY,
                    filename            TEXT NOT NULL,
                    run_type            TEXT NOT NULL,
                    battle_date         TEXT NOT NULL,
                    tier                INTEGER NOT NULL,
                    wave                INTEGER NOT NULL,
                    cells_earned        REAL NOT NULL,
                    real_time_seconds   INTEGER NOT NULL,
                    game_time_seconds   INTEGER NOT NULL,
                    cells_per_hour      REAL NOT NULL,
                    coins_per_hour      REAL NOT NULL,
                    killed_by           TEXT,
                    tower_era           TEXT,
                    payload             TEXT NOT NULL
                )
                """);

        // Migration: add battle_epoch_seconds for dead-time gap computation.
        try {
            jdbc.execute("ALTER TABLE runs ADD COLUMN battle_epoch_seconds INTEGER");
        } catch (Exception ignored) {
            // Column already exists — safe to continue.
        }

        jdbc.execute("""
                CREATE INDEX IF NOT EXISTS idx_runs_battle_date
                ON runs (battle_date)
                """);

        jdbc.execute("""
                CREATE INDEX IF NOT EXISTS idx_runs_run_type
                ON runs (run_type)
                """);

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS currency_snapshots (
                    id             INTEGER PRIMARY KEY AUTOINCREMENT,
                    snapshot_time  TIMESTAMP NOT NULL,
                    cannon_shards  INTEGER,
                    armor_shards   INTEGER,
                    generator_shards INTEGER,
                    core_shards    INTEGER,
                    reroll_shards  INTEGER,
                    gems           INTEGER,
                    stones         INTEGER,
                    medals         INTEGER,
                    elite_cells    REAL,
                    tokens         INTEGER,
                    bits           INTEGER
                )
                """);

        jdbc.execute("""
                CREATE INDEX IF NOT EXISTS idx_currency_snapshots_time
                ON currency_snapshots (snapshot_time)
                """);

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS module_level_snapshots (
                    id            INTEGER PRIMARY KEY AUTOINCREMENT,
                    snapshot_time TIMESTAMP NOT NULL,
                    module_name   TEXT NOT NULL,
                    module_type   TEXT NOT NULL,
                    level         INTEGER NOT NULL
                )
                """);

        jdbc.execute("""
                CREATE INDEX IF NOT EXISTS idx_module_level_snapshots_time
                ON module_level_snapshots (snapshot_time)
                """);

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS chat_history (
                    id          INTEGER PRIMARY KEY AUTOINCREMENT,
                    report_id_1 TEXT NOT NULL,
                    report_id_2 TEXT NOT NULL,
                    role        TEXT NOT NULL,
                    message     TEXT NOT NULL,
                    created_at  INTEGER NOT NULL
                )
                """);

        jdbc.execute("""
                CREATE INDEX IF NOT EXISTS idx_chat_history_pair
                ON chat_history (report_id_1, report_id_2)
                """);

        // Migration: add thought_signature for Gemini thinking-model multi-turn support.
        try {
            jdbc.execute("ALTER TABLE chat_history ADD COLUMN thought_signature TEXT");
        } catch (Exception ignored) {
            // Column already exists — safe to continue.
        }
    }
}
