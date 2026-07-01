package com.pphi.tower.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class WorkshopSeeder {

    private static final Logger log = LoggerFactory.getLogger(WorkshopSeeder.class);

    private final JdbcTemplate jdbc;

    public WorkshopSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM workshop_item", Integer.class);
        if (count == null || count == 0) {
            log.info("Seeding {}...", this.getClass().getSimpleName().replace("Seeder", ""));
            seedWorkshop();
            seedWorkshopPlus();
            log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
        }
        migrateV28_3();
    }

    /**
     * v28.3 raised the level caps on most workshop enhancements. These UPDATEs are idempotent
     * (guarded by {@code max_level < target}) and run on every startup so they reach existing
     * player databases. The added level-cost/value rows are handled by {@link WorkshopCostSeeder}
     * and {@link WorkshopValueSeeder}.
     */
    private void migrateV28_3() {
        raisePlusCap(600, "Damage +", "Rend Armor Mult +", "Critical Factor +", "Damage / Meter +",
                "Super Crit Multi +", "Health +", "Health Regen +", "Defense Absolute +",
                "Land Mine Damage +", "Wall Health +", "Cash Bonus +", "Recovery Package +");
        raisePlusCap(300, "Coin Bonus +", "Cells / Kill Bonus +");
        raisePlusCap(250, "Orb Size +");
        raisePlusCap(150, "Free Upgrades +");
        raisePlusCap(100, "Attack Speed +");
    }

    private void raisePlusCap(int maxLevel, String... names) {
        String placeholders = String.join(",", java.util.Collections.nCopies(names.length, "?"));
        Object[] args = new Object[names.length + 2];
        args[0] = maxLevel; // SET max_level = ?
        args[1] = maxLevel; // AND max_level < ?
        System.arraycopy(names, 0, args, 2, names.length);
        jdbc.update(
                "UPDATE workshop_item SET max_level = ? WHERE is_plus = 1 AND max_level < ? AND name IN (" + placeholders + ")",
                args);
    }

    // ── Workshop (non-plus) ───────────────────────────────────────────────────

    private void seedWorkshop() {
        // Attack unlock groups (cost → items)
        long atkFree  = group(1, 0);
        long atk50    = group(1, 50);
        long atk400   = group(1, 400);
        long atk1500  = group(1, 1_500);
        long atk10k   = group(1, 10_000);
        long atk100M  = group(1, 100_000_000);
        long atk500B  = group(1, 500_000_000_000L);

        item("Damage",              1, 0, 1,  6000, atkFree,  null, null);
        item("Attack Speed",        1, 0, 2,    99, atkFree,  null, null);
        item("Critical Chance",     1, 0, 3,    79, atkFree,  null, null);
        item("Critical Factor",     1, 0, 4,   150, atkFree,  null, null);
        item("Range",               1, 0, 5,    79, atk50,    null, null);
        item("Damage / Meter",      1, 0, 6,   200, atk50,    null, null);
        item("Multishot Chance",    1, 0, 7,    99, atk400,   null, null);
        item("Multishot Targets",   1, 0, 8,     7, atk400,   null, null);
        item("Rapid Fire Chance",   1, 0, 9,    85, atk1500,  null, null);
        item("Rapid Fire Duration", 1, 0, 10,   99, atk1500,  null, null);
        item("Bounce Shot Chance",  1, 0, 11,   85, atk10k,   null, null);
        item("Bounce Shot Targets", 1, 0, 12,    7, atk10k,   null, null);
        item("Bounce Shot Range",   1, 0, 13,   60, atk10k,   null, null);
        item("Super Critical Chance", 1, 0, 14, 100, atk100M, null, null);
        item("Super Critical Mult", 1, 0, 15,  120, atk100M,  null, null);
        item("Rend Armor Chance",   1, 0, 16,  299, atk500B,  null, null);
        item("Rend Armor Mult",     1, 0, 17,  299, atk500B,  null, null);

        // Defense unlock groups
        long defFree   = group(2, 0);
        long def75     = group(2, 75);
        long def500    = group(2, 500);
        long def2k     = group(2, 2_000);
        long def5k     = group(2, 5_000);
        long def15k    = group(2, 15_000);
        long def100k   = group(2, 100_000);
        long def400k   = group(2, 400_000);
        long def15M    = group(2, 1_500_000);
        long def500M   = group(2, 500_000_000);

        item("Health",              2, 0, 1,  6000, defFree,  null, null);
        item("Health Regen",        2, 0, 2,  6000, defFree,  null, null);
        item("Defense %",           2, 0, 3,    99, def75,    null, null);
        item("Defense Absolute",    2, 0, 4,  5000, def75,    null, null);
        item("Thorn Damage",        2, 0, 5,    99, def500,   null, null);
        item("Lifesteal",           2, 0, 6,    80, def2k,    null, null);
        item("Knockback Chance",    2, 0, 7,    80, def5k,    null, null);
        item("Knockback Force",     2, 0, 8,    40, def5k,    null, null);
        item("Orb Speed",           2, 0, 9,    38, def15k,   null, null);
        item("Orbs",                2, 0, 10,    4, def15k,   null, null);
        item("Shockwave Size",      2, 0, 11,   35, def100k,  null, null);
        item("Shockwave Frequency", 2, 0, 12,   40, def100k,  null, null);
        item("Land Mine Chance",    2, 0, 13,   50, def400k,  null, null);
        item("Land Mine Damage",    2, 0, 14,  200, def400k,  null, null);
        item("Land Mine Radius",    2, 0, 15,   50, def400k,  null, null);
        item("Death Defy",          2, 0, 16,   75, def15M,   null, null);
        item("Wall Health",         2, 0, 17, 1800, def500M,  null, null);
        item("Wall Rebuild",        2, 0, 18,  300, def500M,  null, null);

        // Utility unlock groups
        long utlFree  = group(3, 0);
        long utl40    = group(3, 40);
        long utl100   = group(3, 100);
        long utl800   = group(3, 800);
        long utl5k    = group(3, 5_000);
        long utl8k    = group(3, 8_000);
        long utl15M   = group(3, 1_500_000);
        long utl1B    = group(3, 1_000_000_000);

        item("Cash Bonus",               3, 0, 1,  149, utl40,  null, null);
        item("Cash / Wave",              3, 0, 2,  149, utl40,  null, null);
        item("Coin / Kill Bonus",        3, 0, 3,  149, utl100, null, null);
        item("Coin / Wave",              3, 0, 4,  149, utl100, null, null);
        item("Free Attack Upgrade",      3, 0, 5,   99, utl800, null, null);
        item("Free Defense Upgrade",     3, 0, 6,   99, utl800, null, null);
        item("Interest / Wave",          3, 0, 7,   99, utl5k,  null, null);
        item("Free Utility Upgrade",     3, 0, 8,   99, utl8k,  null, null);
        item("Recovery Amount",          3, 0, 9,  300, utl15M, null, null);
        item("Max Amount",               3, 0, 10, 500, utl15M, null, null);
        item("Package Chance",           3, 0, 11,  60, utl15M, null, null);
        item("Enemy Attack Level Skip",  3, 0, 12, 699, utl1B,  null, null);
        item("Enemy Health Level Skip",  3, 0, 13, 699, utl1B,  null, null);
    }

    // ── Workshop+ ─────────────────────────────────────────────────────────────

    private void seedWorkshopPlus() {
        // All three categories share the same phase-1 lab gate.
        // The first item in each category unlocks immediately on lab completion (no spend threshold).
        // Subsequent items require cumulative spend within the same category.
        String lab = "Workshop Enhancements";

        // Attack+
        item("Damage +",          1, 1, 1, 600, null, lab,   null);
        item("Rend Armor Mult +",  1, 1, 2, 600, null, null,  50_000_000_000.0);
        item("Critical Factor +",  1, 1, 3, 600, null, null,  500_000_000_000.0);
        item("Damage / Meter +",   1, 1, 4, 600, null, null,  5_000_000_000_000.0);
        item("Super Crit Multi +", 1, 1, 5, 600, null, null,  50_000_000_000_000.0);
        item("Attack Speed +",     1, 1, 6, 100, null, null,  500_000_000_000_000.0);

        // Defense+
        item("Health +",           2, 1, 1, 600, null, lab,   null);
        item("Health Regen +",     2, 1, 2, 600, null, null,  50_000_000_000.0);
        item("Defense Absolute +", 2, 1, 3, 600, null, null,  500_000_000_000.0);
        item("Land Mine Damage +", 2, 1, 4, 600, null, null,  5_000_000_000_000.0);
        item("Wall Health +",      2, 1, 5, 600, null, null,  50_000_000_000_000.0);
        item("Orb Size +",         2, 1, 6, 250, null, null,  500_000_000_000_000.0);

        // Utility+
        item("Cash Bonus +",        3, 1, 1, 600, null, lab,   null);
        item("Coin Bonus +",        3, 1, 2, 300, null, null,  50_000_000_000.0);
        item("Cells / Kill Bonus +",3, 1, 3, 300, null, null,  500_000_000_000.0);
        item("Free Upgrades +",     3, 1, 4, 150, null, null,  5_000_000_000_000.0);
        item("Recovery Package +",  3, 1, 5, 600, null, null,  50_000_000_000_000.0);
        item("Enemy Level Skips +", 3, 1, 6,  60, null, null,  500_000_000_000_000.0);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private long group(int categoryId, long unlockCost) {
        return jdbc.queryForObject(
                "INSERT INTO workshop_unlock_group (category_id, unlock_cost) VALUES (?,?) RETURNING id",
                Long.class, categoryId, (double) unlockCost);
    }

    private void item(String name, int categoryId, int isPlus, int sortOrder, int maxLevel,
                      Long unlockGroupId, String plusUnlockLabName, Double plusUnlockCumulativeSpend) {
        Long id = jdbc.queryForObject(
                """
                INSERT INTO workshop_item
                    (name, category_id, is_plus, sort_order, max_level,
                     unlock_group_id, plus_unlock_lab_name, plus_unlock_cumulative_spend)
                VALUES (?,?,?,?,?,?,?,?) RETURNING id
                """,
                Long.class,
                name, categoryId, isPlus, sortOrder, maxLevel,
                unlockGroupId, plusUnlockLabName, plusUnlockCumulativeSpend);
        if (id != null) {
            jdbc.update("INSERT INTO workshop_item_state (workshop_item_id) VALUES (?)", id);
        }
        if (unlockGroupId != null) {
            jdbc.update(
                    "INSERT OR IGNORE INTO workshop_unlock_group_state (unlock_group_id) VALUES (?)",
                    unlockGroupId);
        }
    }
}
