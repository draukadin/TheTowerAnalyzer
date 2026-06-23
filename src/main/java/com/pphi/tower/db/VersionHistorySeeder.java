package com.pphi.tower.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class VersionHistorySeeder {

    private static final Logger log = LoggerFactory.getLogger(VersionHistorySeeder.class);

    private final JdbcTemplate jdbc;

    public VersionHistorySeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer versionCount = jdbc.queryForObject("SELECT COUNT(*) FROM tower_version", Integer.class);
        if (versionCount != null && versionCount > 0) return;
        Integer runCount = jdbc.queryForObject("SELECT COUNT(*) FROM runs", Integer.class);
        if (runCount != null && runCount > 0) {
            log.info("Skipping VersionHistory seed — {} existing run(s) found; user has pre-existing data", runCount);
            return;
        }
        log.info("Seeding {}...", this.getClass().getSimpleName().replace("Seeder", ""));
        seedVersions();
        log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
    }

    private void seedVersions() {
        version("1.0.0", "Major", "Initial");
    }

    private void version(String version, String type, String summary) {
        jdbc.update("INSERT OR IGNORE INTO tower_version (version, type, summary) VALUES (?,?,?)",
                version, type, summary);
    }

    private void change(String version, String category, String entityName,
                        String oldValue, String newValue, String notes) {
        jdbc.update("""
                INSERT INTO tower_version_change (version, category, entity_name, old_value, new_value, notes)
                VALUES (?,?,?,?,?,?)
                """, version, category, entityName, oldValue, newValue, notes);
    }
}
