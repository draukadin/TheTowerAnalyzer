package com.pphi.tower.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CardSeeder {

    private static final Logger log = LoggerFactory.getLogger(CardSeeder.class);

    private final JdbcTemplate jdbc;

    public CardSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM card", Integer.class);
        if (count != null && count > 0) return;
        log.info("Seeding {}...", this.getClass().getSimpleName().replace("Seeder", ""));
        seedCards();
        log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
    }

    private void seedCards() {

        // ── Common ────────────────────────────────────────────────────────────

        card("Damage", "COMMON", "Increase tower damage by x #",
             "MULTIPLIER", 1.50, 2.00, 2.40, 2.80, 3.20, 3.60, 4.00,
             null, null,
             "Increases card stat multiplier", 750, "MULTIPLIER",
             1.4, 1.8, 2.2, 2.6, 3.0, 3.4, 3.8, 4.2, 4.6, 5.0);

        card("Attack Speed", "COMMON", "Increase tower attack speed by x #",
             "MULTIPLIER", 1.25, 1.40, 1.55, 1.70, 1.85, 2.00, 2.15,
             null, null,
             "Increases card stat multiplier", 750, "MULTIPLIER",
             1.03, 1.06, 1.09, 1.12, 1.15, 1.18, 1.21, 1.24, 1.27, 1.30);

        card("Health", "COMMON", "Increase tower health by x #",
             "MULTIPLIER", 1.50, 2.00, 2.40, 2.80, 3.20, 3.60, 4.00,
             null, null,
             "Increases card stat multiplier", 750, "MULTIPLIER",
             1.2, 1.4, 1.6, 1.8, 2.0, 2.2, 2.4, 2.6, 2.8, 3.0);

        card("Health Regen", "COMMON", "Increase tower health regen by x # / sec",
             "MULTIPLIER", 1.40, 1.60, 1.80, 2.00, 2.20, 2.40, 2.60,
             null, null,
             "Increases card stat multiplier", 750, "MULTIPLIER",
             1.4, 1.8, 2.2, 2.6, 3.0, 3.4, 3.8, 4.2, 4.6, 5.0);

        card("Range", "COMMON", "Increase tower range by x #",
             "MULTIPLIER", 1.15, 1.20, 1.25, 1.30, 1.35, 1.40, 1.45,
             null, null,
             "Adds damage per meter bonus multiplier", 750, "MULTIPLIER",
             1.2, 1.4, 1.6, 1.8, 2.0, 2.2, 2.4, 2.6, 2.8, 3.0);

        card("Cash", "COMMON", "Increase all cash earned by x #",
             "MULTIPLIER", 1.20, 1.40, 1.60, 1.80, 2.00, 2.20, 2.40,
             null, null,
             "Adds chance for elites to drop reroll dice", 500, "PERCENT",
             0.4, 0.8, 1.2, 1.6, 2.0, 2.4, 2.8, 3.2, 3.6, 4.0);

        card("Coins", "COMMON", "Increase all coins earned by x #",
             "MULTIPLIER", 1.15, 1.20, 1.25, 1.30, 1.35, 1.40, 1.45,
             null, null,
             "Increases card stat multiplier", 1250, "MULTIPLIER",
             1.03, 1.06, 1.09, 1.12, 1.15, 1.18, 1.21, 1.24, 1.27, 1.30);

        card("Slow Aura", "COMMON", "All enemies in tower range speed decreased by #%",
             "PERCENT", 13.0, 16.0, 19.0, 22.0, 25.0, 28.0, 31.0,
             null, null,
             "Reduces enemy attack speed", 1000, "MULTIPLIER",
             1.05, 1.10, 1.15, 1.20, 1.25, 1.30, 1.35, 1.40, 1.45, 1.50);

        card("Critical Chance", "COMMON", "Increase critical chance by +#%",
             "ADDITIVE_PERCENT", 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0,
             null, null,
             "Bonus to Crit Chance, super Crit chance, and Super Crit Factor", 750, "ADDITIVE_PERCENT",
             1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);

        card("Enemy Balance", "COMMON",
             "Increase enemies spawned each wave, cash earned per kill increased by x #",
             "MULTIPLIER", 1.30, 1.40, 1.50, 1.60, 1.70, 1.80, 1.90,
             null, null,
             "Chance for double elite spawn", 1000, "PERCENT",
             6.0, 12.0, 18.0, 24.0, 30.0, 36.0, 42.0, 48.0, 54.0, 60.0);

        card("Extra Defense", "COMMON", "Increase defense percent by +#%",
             "ADDITIVE_PERCENT", 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0,
             null, null,
             "Increases card stat multiplier", 1000, "ADDITIVE_PERCENT",
             0.7, 1.4, 2.1, 2.8, 3.5, 4.2, 4.9, 5.6, 6.3, 7.0);

        card("Fortress", "COMMON", "Increase defense absolute by x#",
             "MULTIPLIER", 1.30, 1.45, 1.60, 1.75, 1.90, 2.05, 2.20,
             null, null,
             "Reduces wall rebuild time", 750, "SECONDS",
             -10.0, -20.0, -30.0, -40.0, -50.0, -60.0, -70.0, -80.0, -90.0, -100.0);

        // ── Rare ──────────────────────────────────────────────────────────────

        card("Free Upgrades", "RARE", "Increases all free upgrade chances per wave by #%",
             "ADDITIVE_PERCENT", 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0,
             null, null,
             "Lock a stat from free ups", 500, "COUNT",
             1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0);

        card("Extra Orb", "RARE",
             "A spinning orb with a speed of # that destroys enemies on contact (except bosses)",
             "MULTIPLIER", 0.30, 0.40, 0.50, 0.60, 0.70, 0.80, 0.90,
             null, null,
             "Orb coin bonus", 750, "MULTIPLIER",
             1.04, 1.08, 1.12, 1.16, 1.20, 1.24, 1.28, 1.32, 1.36, 1.40);

        card("Plasma Cannon", "RARE",
             "Fire one big plasma shot at a boss dropping its health by #%",
             "PERCENT", 30.0, 34.0, 38.0, 42.0, 46.0, 50.0, 54.0,
             null, null,
             "Percent of plasma cannon applied to elites", 1250, "PERCENT",
             5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0);

        card("Critical Coin", "RARE",
             "If a basic enemy dies from a critical shot it has a chance to drop coins of #%",
             "PERCENT", 15.0, 18.0, 21.0, 24.0, 27.0, 30.0, 33.0,
             null, null,
             "Percent chance to drop two coins instead of one", 1000, "PERCENT",
             10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0);

        card("Wave Skip", "RARE",
             "Chance to skip a wave and earning coins and cash equal to the previous wave x1.10 of #%",
             "PERCENT", 9.0, 10.0, 11.0, 13.0, 15.0, 17.0, 19.0,
             null, null,
             "Chance to double wave skip", 1000, "PERCENT",
             10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0, 55.0);

        card("Intro Sprint", "RARE",
             "Waves increase by 10 each time, for the first [x] waves or up to your highest wave. " +
             "A Boss spawns every wave and no coins are earned during Intro Sprint.",
             "COUNT", 20.0, 30.0, 40.0, 50.0, 60.0, 80.0, 100.0,
             null, null,
             "Increases how many waves it's active", 1250, "MULTIPLIER",
             1.8, 3.6, 5.4, 7.2, 9.0, 10.8, 12.6, 14.4, 16.2, 18.0);

        card("Land Mine Stun", "RARE",
             "Land mines have a 40% chance to stun enemies for [x] sec (except bosses)",
             "SECONDS", 1.4, 1.8, 2.2, 2.6, 3.0, 3.4, 3.8,
             7, 250,
             "Chance stunned enemies will miss attacks", 1000, "PERCENT",
             2.5, 5.0, 7.5, 10.0, 12.5, 15.0, 17.5, 20.0, 22.5, 25.0);

        card("Recovery Package Chance", "RARE",
             "Increase recovery package spawn chance by [x]",
             "PERCENT", 15.0, 18.0, 21.0, 24.0, 27.0, 30.0, 33.0,
             2, 750,
             "Packages have a chance to drop common modules", 1000, "PERCENT",
             0.4, 0.8, 1.2, 1.6, 2.0, 2.4, 2.8, 3.2, 3.6, 4.0);

        // ── Epic ──────────────────────────────────────────────────────────────

        card("Death Ray", "EPIC",
             "A powerful ray that destroys enemies on contact (except bosses), with a duration of # sec",
             "SECONDS", 2.3, 2.7, 3.1, 3.5, 3.9, 4.4, 4.9,
             null, null,
             "Death ray partially pierces protector shield", 750, "PERCENT",
             5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0);

        card("Energy Net", "EPIC",
             "Fire a special net at a boss immobilizing it for # sec",
             "SECONDS", 2.5, 2.8, 3.1, 3.4, 3.7, 4.0, 4.3,
             null, null,
             "Damage multi to bosses when trapped and 10s after", 750, "MULTIPLIER",
             2.0, 4.0, 6.0, 8.0, 10.0, 12.0, 14.0, 16.0, 18.0, 20.0);

        card("Super Tower", "EPIC",
             "The tower becomes super for 15 seconds, tower damage increased by x # (30 sec cooldown)",
             "MULTIPLIER", 2.5, 2.9, 3.3, 3.7, 4.1, 4.5, 5.0,
             null, null,
             "35% of super tower bonus is applied to UWs, reduces cooldown", 1000, "SECONDS",
             -3.0, -6.0, -9.0, -12.0, -15.0, -18.0, -21.0, -24.0, -27.0, -30.0);

        card("Second Wind", "EPIC",
             "Revive the tower with half health once per round and creates an invincible shield for [x] sec",
             "SECONDS", 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0,
             null, null,
             "Increases HP regen for 400 waves when triggered", 1000, "MULTIPLIER",
             1.9, 2.8, 3.7, 4.6, 5.5, 6.4, 7.3, 8.2, 9.1, 10.0);

        card("Demon Mode", "EPIC",
             "Once per Round: Activate Demon Mode. Grants 300x Projectile Damage & Invincibility for [x] sec",
             "SECONDS", 180.0, 200.0, 220.0, 240.0, 260.0, 280.0, 300.0,
             null, null,
             "Lingering damage multi for 300 waves", 1000, "MULTIPLIER",
             1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0, 5.5, 6.0);

        card("Energy Shield", "EPIC",
             "Shield that ignores a single attack, replenishes after [x] min",
             "MINUTES", 20.0, 18.0, 16.0, 14.0, 12.0, 10.0, 8.0,
             null, null,
             "Energy shield activates a blast that repels all enemies back by a percent of tower max range " +
             "and destroys all enemy projectiles. The charge times of Rays are reset.",
             1000, "PERCENT",
             5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0);

        card("Wave Accelerator", "EPIC",
             "Reduce the cooldown between waves by [x]",
             "PERCENT", 30.0, 34.0, 38.0, 42.0, 46.0, 50.0, 54.0,
             null, null,
             "Increases spawn rate acceleration", 1000, "PERCENT",
             110.0, 120.0, 130.0, 140.0, 150.0, 160.0, 170.0, 180.0, 190.0, 200.0);

        card("Berserker", "EPIC",
             "Increase damage by [x] of total damage absorbed this round (max of x8 tower damage)",
             "PERCENT", 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4,
             null, null,
             "Increases damage cap from berserk to x500 for a limited time after death defy", 750, "SECONDS",
             30.0, 60.0, 90.0, 120.0, 150.0, 180.0, 210.0, 240.0, 270.0, 300.0);

        card("Ultimate Crit", "EPIC",
             "Ultimate weapons gain a [x] chance to deal critical damage of [Tower critical factor]",
             "PERCENT", 1.0, 1.33, 1.66, 2.0, 2.33, 2.66, 3.0,
             14, 50,
             "Increases UW crit chance", 750, "ADDITIVE_PERCENT",
             0.3, 0.7, 1.0, 1.3, 1.7, 2.0, 2.3, 2.7, 3.0, 3.3);

        card("Nuke", "EPIC",
             "Destroys [x] of enemies.",
             "PERCENT", 25.0, 35.0, 45.0, 55.0, 65.0, 80.0, 100.0,
             11, 10,
             "Reduces enemy attack speed for 300 waves after Nuke use", 750, "PERCENT",
             5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 35.0, 40.0, 45.0, 50.0);

        card("Area of Effect", "EPIC",
             "Increase area of effect damage for Inner Land Mine, Poison Swamp, Smart Missile, " +
             "Flame Bot, and Land Mine by [x]",
             "PERCENT", 5.0, 8.0, 11.0, 14.0, 17.0, 20.0, 25.0,
             20, 80,
             "Increases Area of Effect radius", 1000, "PERCENT",
             2.5, 5.0, 7.5, 10.0, 12.5, 15.0, 17.5, 20.0, 22.5, 25.0);
    }

    private void card(String name, String rarity, String description,
                      String valueUnit,
                      double l1, double l2, double l3, double l4, double l5, double l6, double l7,
                      Integer milestoneUnlockTier, Integer milestoneUnlockWave,
                      String masteryDescription, int masterystoneCost, String masteryValueUnit,
                      double m0, double m1, double m2, double m3, double m4,
                      double m5, double m6, double m7, double m8, double m9) {
        Long id = jdbc.queryForObject("""
                INSERT OR IGNORE INTO card (
                    name, rarity, description,
                    value_unit, level_1, level_2, level_3, level_4, level_5, level_6, level_7,
                    milestone_unlock_tier, milestone_unlock_wave,
                    mastery_description, mastery_stone_cost, mastery_value_unit,
                    mastery_level_0, mastery_level_1, mastery_level_2, mastery_level_3, mastery_level_4,
                    mastery_level_5, mastery_level_6, mastery_level_7, mastery_level_8, mastery_level_9
                ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id
                """,
                Long.class,
                name, rarity, description,
                valueUnit, l1, l2, l3, l4, l5, l6, l7,
                milestoneUnlockTier, milestoneUnlockWave,
                masteryDescription, masterystoneCost, masteryValueUnit,
                m0, m1, m2, m3, m4, m5, m6, m7, m8, m9);
        if (id != null) {
            jdbc.update("INSERT OR IGNORE INTO card_player_state (card_id) VALUES (?)", id);
        }
    }
}
