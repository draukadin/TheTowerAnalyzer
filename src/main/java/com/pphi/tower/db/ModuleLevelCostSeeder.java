package com.pphi.tower.db;

import com.pphi.tower.model.ModuleLevelTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ModuleLevelCostSeeder {

    private static final Logger log = LoggerFactory.getLogger(ModuleLevelCostSeeder.class);

    public ModuleLevelCostSeeder(JdbcTemplate jdbc, DatabaseInitializer dbInit) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM module_level_cost", Integer.class);
        if (count != null && count == ModuleLevelTable.MAX_LEVEL) return;

        log.info("Seeding ModuleLevelCost...");
        jdbc.execute("DELETE FROM module_level_cost");

        List<Object[]> batch = new ArrayList<>();
        for (int level = 1; level <= ModuleLevelTable.MAX_LEVEL; level++) {
            batch.add(new Object[]{
                level,
                ModuleLevelTable.shardsForLevel(level),
                (double) ModuleLevelTable.coinsForLevel(level)
            });
        }
        jdbc.batchUpdate(
            "INSERT INTO module_level_cost (level, shard_cost, coin_cost) VALUES (?, ?, ?)",
            batch
        );
    }
}
