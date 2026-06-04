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

        // ── Ultimate Weapons ──────────────────────────────────────────────────

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS uw (
                    id           INTEGER PRIMARY KEY,
                    code         TEXT NOT NULL UNIQUE,
                    name         TEXT NOT NULL,
                    uw_plus_name TEXT NOT NULL
                )
                """);

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS uw_stat (
                    id         INTEGER PRIMARY KEY,
                    uw_id      INTEGER NOT NULL REFERENCES uw(id),
                    stat_key   TEXT    NOT NULL,
                    label      TEXT    NOT NULL,
                    max_level  INTEGER NOT NULL,
                    sort_order INTEGER NOT NULL DEFAULT 0,
                    UNIQUE(uw_id, stat_key)
                )
                """);

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS uw_stat_level_value (
                    uw_stat_id     INTEGER NOT NULL REFERENCES uw_stat(id),
                    level          INTEGER NOT NULL,
                    value          REAL    NOT NULL,
                    stones_to_next INTEGER,
                    PRIMARY KEY (uw_stat_id, level)
                )
                """);

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS uw_player_state (
                    uw_id            INTEGER NOT NULL REFERENCES uw(id) PRIMARY KEY,
                    unlocked         INTEGER NOT NULL DEFAULT 0,
                    uw_plus_unlocked INTEGER NOT NULL DEFAULT 0
                )
                """);

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS uw_stat_player_level (
                    uw_stat_id    INTEGER NOT NULL REFERENCES uw_stat(id) PRIMARY KEY,
                    current_level INTEGER NOT NULL DEFAULT 0
                )
                """);

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS uw_stat_level_history (
                    id         INTEGER  PRIMARY KEY AUTOINCREMENT,
                    uw_stat_id INTEGER  NOT NULL REFERENCES uw_stat(id),
                    old_level  INTEGER  NOT NULL,
                    new_level  INTEGER  NOT NULL,
                    changed_at DATETIME NOT NULL DEFAULT (datetime('now'))
                )
                """);

        jdbc.execute("""
                CREATE INDEX IF NOT EXISTS idx_uw_stat_level_history_stat
                ON uw_stat_level_history (uw_stat_id, changed_at)
                """);

        jdbc.execute("""
                CREATE TABLE IF NOT EXISTS uw_stat_target_level (
                    uw_stat_id   INTEGER NOT NULL REFERENCES uw_stat(id) PRIMARY KEY,
                    target_level INTEGER NOT NULL DEFAULT 0
                )
                """);
    }
}
