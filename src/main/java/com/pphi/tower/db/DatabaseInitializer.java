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
                    folder              TEXT NOT NULL,
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
        // ALTER TABLE ADD COLUMN is idempotent in SQLite 3.37+; catch the error for older versions.
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
                CREATE INDEX IF NOT EXISTS idx_runs_folder
                ON runs (folder)
                """);
    }
}
