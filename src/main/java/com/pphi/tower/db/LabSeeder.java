package com.pphi.tower.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LabSeeder {

    private final JdbcTemplate jdbc;

    public LabSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM lab", Integer.class);
        if (count != null && count > 0) return;
        seedLabs();
    }

    private void seedLabs() {
        // MAIN RESEARCHES
        lab("Game Speed",                           "Main",             7,   7,  7);
        lab("Starting Cash",                        "Main",            10,  10, 99);
        lab("Workshop Attack Discount",             "Main",            23,  23, 99);
        lab("Workshop Defense Discount",            "Main",            41,  41, 99);
        lab("Workshop Utility Discount",            "Main",            22,  22, 99);
        lab("Labs Coin Discount",                   "Main",            49,  99, 99);
        lab("Labs Speed",                           "Main",            99,  99, 99);
        lab("Buy Multiplier",                       "Main",             4,   4,  4);
        lab("More Round Stats",                     "Main",             1,   1,  1);
        lab("Target Priority",                      "Main",             2,   2,  2);
        lab("Card Presets",                         "Main",             1,   1,  1);
        lab("Workshop Respec",                      "Main",             1,   1,  1);
        lab("Reroll Daily Mission",                 "Main",             1,   1,  1);
        lab("Workshop Enhancements",                "Main",             1,   1,  1);
        lab("Enhancement Attack - Coin Discount",   "Main",             0,   1, 100);
        lab("Enhancement Defense - Coin Discount",  "Main",             0,   1, 100);
        lab("Enhancement Utility - Coin Discount",  "Main",             0,   1, 100);
        lab("Dissonant Echo - Attack",              "Main",             0,   1, 20);
        lab("Dissonant Echo - Defense",             "Main",             0,   1, 20);
        lab("Dissonant Echo - Utility",             "Main",             0,   1, 20);
        lab("Dissonant Echo - Ultimate Weapons",    "Main",             0,   1, 20);

        // ATTACK
        lab("Damage",                               "Attack",          61, 100, 100);
        lab("Attack Speed",                         "Attack",          67,  99,  99);
        lab("Critical Factor",                      "Attack",          57,  99,  99);
        lab("Range",                                "Attack",           1,   1,  80);
        lab("Damage / Meter",                       "Attack",          58,  58,  99);
        lab("Super Crit Chance",                    "Attack",          10,  15,  50);
        lab("Super Crit Multi",                     "Attack",          11,  15,  40);
        lab("Max Rend Armor Multiplier",            "Attack",           9,   9,  30);
        lab("Light Speed Shots",                    "Attack",           1,   1,   1);

        // DEFENSE
        lab("Health",                               "Defense",         47, 100, 100);
        lab("Health Regen",                         "Defense",         57, 100, 100);
        lab("Defense Absolute",                     "Defense",          2,   2, 100);
        lab("Defense %",                            "Defense",         26,  50,  50);
        lab("Orbs Speed",                           "Defense",         10,  20,  20);
        lab("Land Mine Damage",                     "Defense",         14,  14,  20);
        lab("Land Mine Decay",                      "Defense",         10,  10,  35);
        lab("Shockwave Size",                       "Defense",          7,  20,  20);
        lab("Orb Boss Hit",                         "Defense",          0,  10,  10);
        lab("Wall Health",                          "Defense",         50,  50,  50);
        lab("Wall Rebuild",                         "Defense",          6,  20,  20);
        lab("Wall Regen",                           "Defense",         20,  30,  30);
        lab("Wall Thorns",                          "Defense",         15,  20,  20);
        lab("Wall Invincibility",                   "Defense",          0,  10,  10);
        lab("Wall Fortification",                   "Defense",         32,  60,  60);
        lab("Garlic Thorns",                        "Defense",         10,  10,  10);

        // UTILITY
        lab("Cash Bonus",                           "Utility",         24,  24,  99);
        lab("Cash / Wave",                          "Utility",         12,  12,  99);
        lab("Coins / Kill Bonus",                   "Utility",         63,  99,  99);
        lab("Coins / Wave",                         "Utility",         25,  25,  99);
        lab("Interest",                             "Utility",         10,  10,  99);
        lab("Max Interest",                         "Utility",          8,   8,  15);
        lab("Package After Boss",                   "Utility",          1,   1,   1);
        lab("Recovery Package Amount",              "Utility",          0,   0,  20);
        lab("Recovery Package Max",                 "Utility",          0,   0,  20);
        lab("Recovery Package Chance",              "Utility",          3,  20,  20);
        lab("Enemy Attack Level Skip",              "Utility",         20,  20,  20);
        lab("Enemy Health Level Skip",              "Utility",         12,  20,  20);

        // ULTIMATE WEAPONS
        lab("Missile Despawn Time",                 "Ultimate Weapons", 0,   0,  20);
        lab("Missiles Explosion",                   "Ultimate Weapons", 1,   1,   1);
        lab("Missile Radius",                       "Ultimate Weapons",10,  10,  20);
        lab("Chrono Field Duration",                "Ultimate Weapons",18,  30,  30);
        lab("Chrono Field Damage Reduction",        "Ultimate Weapons", 1,   1,   1);
        lab("Chrono Field Reduction %",             "Ultimate Weapons", 1,  30,  30);
        lab("Swamp Radius",                         "Ultimate Weapons", 0,   0,  30);
        lab("Swamp Stun",                           "Ultimate Weapons", 0,   0,   1);
        lab("Swamp Stun Chance",                    "Ultimate Weapons", 0,   0,  30);
        lab("Swamp Stun Time",                      "Ultimate Weapons", 0,   0,  30);
        lab("Golden Tower Bonus",                   "Ultimate Weapons",25,  25,  25);
        lab("Golden Tower Duration",                "Ultimate Weapons",20,  20,  20);
        lab("Chain Lightning Shock",                "Ultimate Weapons", 1,   1,   1);
        lab("Shock Chance",                         "Ultimate Weapons",21,  30,  30);
        lab("Shock Multiplier",                     "Ultimate Weapons",14,  14,  14);
        lab("Death Wave Health",                    "Ultimate Weapons",30,  30,  30);
        lab("Death Wave Coin Bonus",                "Ultimate Weapons",20,  20,  20);
        lab("Inner Mine Blast Radius",              "Ultimate Weapons", 0,   0,  20);
        lab("Inner Mine Rotation Speed",            "Ultimate Weapons", 0,   0,  20);
        lab("Chrono Field Range",                   "Ultimate Weapons", 9,  20,  20);
        lab("Missile Amplifier",                    "Ultimate Weapons", 3,   3,  25);
        lab("Missile Barrage",                      "Ultimate Weapons", 1,   1,   1);
        lab("Missile Barrage Quantity",             "Ultimate Weapons", 0,   0,   6);
        lab("Inner Mine Stun",                      "Ultimate Weapons", 0,   0,   1);
        lab("Black Hole Damage",                    "Ultimate Weapons",10,  10,  10);
        lab("Extra Black Hole",                     "Ultimate Weapons", 1,   1,   1);
        lab("Black Hole Coin Bonus",                "Ultimate Weapons",20,  20,  20);
        lab("Spotlight Coin Bonus",                 "Ultimate Weapons",20,  20,  20);
        lab("Spotlight Missiles",                   "Ultimate Weapons", 2,   2,  18);
        lab("Black Hole Disable Ranged Enemies",    "Ultimate Weapons", 1,   1,   1);
        lab("Recharge Missile Barrage",             "Ultimate Weapons", 0,   0,   7);
        lab("Swamp Rend",                           "Ultimate Weapons", 0,   0,  30);
        lab("Swamp Rend - Additional Enemies",      "Ultimate Weapons", 0,   0,   6);
        lab("Chain Thunder",                        "Ultimate Weapons", 2,  10,  30);
        lab("Lightning Amplifier - Scatter",        "Ultimate Weapons", 2,  10,  30);
        lab("Death Wave Cells Bonus",               "Ultimate Weapons",20,  20,  20);
        lab("Death Wave Damage Amplifier",          "Ultimate Weapons", 6,  30,  30);
        lab("Death Wave Armor Stripping",           "Ultimate Weapons", 2,   2,  10);
        lab("Inner Land Mine - Chrono Jump",        "Ultimate Weapons", 0,   0,  10);

        // CARDS
        lab("Second Wind Blast",                    "Cards",            0, null,  4);
        lab("Double Death Ray",                     "Cards",            0, null, 30);
        lab("Extra Orb Adjuster",                   "Cards",            1,    1,  1);
        lab("Extra Extra Orbs",                     "Cards",            2,    2,  2);
        lab("Energy Shield Extra Hit",              "Cards",            1,    1,  2);
        lab("Super Tower Bonus",                    "Cards",            0, null, 30);
        lab("Recharge Second Wind",                 "Cards",            0, null,  7);
        lab("Recharge Demon Mode",                  "Cards",            0, null,  7);
        lab("Recharge Nuke",                        "Cards",            1,    1,  7);
        lab("Damage Mastery",                       "Cards",            0, null,  9);
        lab("Attack Speed Mastery",                 "Cards",            0, null,  9);
        lab("Health Mastery",                       "Cards",            0, null,  9);
        lab("Health Regen Mastery",                 "Cards",            0, null,  9);
        lab("Range Mastery",                        "Cards",            0, null,  9);
        lab("Cash Mastery",                         "Cards",            0, null,  9);
        lab("Coins Mastery",                        "Cards",            0, null,  9);
        lab("Slow Aura Mastery",                    "Cards",            0, null,  9);
        lab("Critical Chance Mastery",              "Cards",            0, null,  9);
        lab("Enemy Balance Mastery",                "Cards",            0, null,  9);
        lab("Extra Defense Mastery",                "Cards",            0, null,  9);
        lab("Fortress Mastery",                     "Cards",            0, null,  9);
        lab("Free Upgrades Mastery",                "Cards",            0, null,  9);
        lab("Extra Orb Mastery",                    "Cards",            0, null,  9);
        lab("Plasma Cannon Mastery",                "Cards",            0, null,  9);
        lab("Critical Coin Mastery",                "Cards",            0, null,  9);
        lab("Wave Skip Mastery",                    "Cards",            0, null,  9);
        lab("Intro Sprint Mastery",                 "Cards",            0, null,  9);
        lab("Land Mine Stun Mastery",               "Cards",            0, null,  9);
        lab("Recovery Package Chance Mastery",      "Cards",            0, null,  9);
        lab("Death Ray Mastery",                    "Cards",            0, null,  9);
        lab("Energy Net Mastery",                   "Cards",            0, null,  9);
        lab("Super Tower Mastery",                  "Cards",            0, null,  9);
        lab("Second Wind Mastery",                  "Cards",            0, null,  9);
        lab("Demon Mode Mastery",                   "Cards",            0, null,  9);
        lab("Energy Shield Mastery",                "Cards",            0, null,  9);
        lab("Wave Accelerator Mastery",             "Cards",            0, null,  9);
        lab("Berserker Mastery",                    "Cards",            0, null,  9);
        lab("Ultimate Crit Mastery",                "Cards",            0, null,  9);
        lab("Nuke Mastery",                         "Cards",            0, null,  9);
        lab("Area of Effect Mastery",               "Cards",            0, null,  9);

        // PERKS
        lab("Unlock Perks",                         "Perks",            1,   1,   1);
        lab("Waves Required",                       "Perks",           13,  20, 100);
        lab("Auto Pick Perks",                      "Perks",            1,   1,   1);
        lab("Standard Perks Bonus",                 "Perks",           20,  25,  25);
        lab("Perk Option Quantity",                 "Perks",            2,   2,   2);
        lab("First Perk Choice",                    "Perks",            1,   1,   1);
        lab("Ban Perks",                            "Perks",            5,   5,   8);
        lab("Improve Trade-off Perks",              "Perks",           10,  10,  10);
        lab("Auto Pick Ranking",                    "Perks",           14,  14,  32);

        // BOTS
        lab("Flame Bot - Cooldown",                 "Bots",            13,  25,  25);
        lab("Thunder Bot - Cooldown",               "Bots",             0,   0,  25);
        lab("Gold Bot - Cooldown",                  "Bots",             3,   8,  25);
        lab("Amp Bot - Cooldown",                   "Bots",             0,  25,  25);
        lab("Bot Bot - Cooldown",                   "Bots",             0,   0,  25);
        lab("Flame Bot - Burn Stack",               "Bots",             0,   0,   5);
        lab("Thunder Bot - Linger Time",            "Bots",             0,   0,  20);
        lab("Gold Bot - Duration",                  "Bots",             0,  20,  20);
        lab("Amp Bot - Duration",                   "Bots",             0,  20,  20);
        lab("Bot Bot - Duration",                   "Bots",             0,   0,  20);

        // ENEMIES
        lab("Common Enemy Health",                  "Enemies",          0, null, 30);
        lab("Common Enemy Attack",                  "Enemies",          0, null, 30);
        lab("Fast Enemy Health",                    "Enemies",          0, null, 30);
        lab("Fast Enemy Attack",                    "Enemies",          0, null, 30);
        lab("Fast Enemy Speed",                     "Enemies",          0, null, 30);
        lab("Tank Enemy Health",                    "Enemies",          0, null, 30);
        lab("Tank Enemy Attack",                    "Enemies",          0, null, 30);
        lab("Ranged Enemy Health",                  "Enemies",          0, null, 30);
        lab("Ranged Enemy Attack",                  "Enemies",          0, null, 30);
        lab("Boss Health",                          "Enemies",          0, null, 30);
        lab("Boss Attack",                          "Enemies",          0, null, 30);
        lab("Protector Health",                     "Enemies",          0, null, 30);
        lab("Protector Radius",                     "Enemies",          0, null, 30);
        lab("Protector Damage Reduction",           "Enemies",          0, null, 20);
        lab("Ray Enemy Attack",                     "Enemies",          0, null, 30);
        lab("Ray Enemy Health",                     "Enemies",          0, null, 30);
        lab("Vampire Enemy Attack",                 "Enemies",          0, null, 30);
        lab("Vampire Enemy Health",                 "Enemies",          0, null, 30);
        lab("Scatter Enemy Attack",                 "Enemies",          0, null, 30);
        lab("Scatter Enemy Health",                 "Enemies",          0, null, 30);
        lab("Ranged Enemy Range",                   "Enemies",          0, null, 30);

        // MODULES
        lab("Common Drop Chance",                   "Modules",         10,  10,  10);
        lab("Reroll Shards",                        "Modules",         55, 100, 100);
        lab("Daily Mission Shards",                 "Modules",         50,  50,  50);
        lab("Module Shards Cost",                   "Modules",         30,  30,  30);
        lab("Module Coin Cost",                     "Modules",         30,  30,  30);
        lab("Rare Drop Chance",                     "Modules",         10,  10,  10);
        lab("Unmerge Module",                       "Modules",          1,   1,   1);
        lab("Shatter Shards",                       "Modules",          0,   5,   5);
        lab("Cannon Effect Bans",                   "Modules",          1,   2,   4);
        lab("Armor Effect Bans",                    "Modules",          2,   2,   4);
        lab("Generator Effect Bans",                "Modules",          1,   1,   3);
        lab("Core Effect Bans",                     "Modules",          0,   1,   7);
        lab("Assist Module Substats - Cannon",      "Modules",          0, null, 30);
        lab("Assist Module Substats - Armor",       "Modules",          0, null, 30);
        lab("Assist Module Substats - Generator",   "Modules",          0, null, 30);
        lab("Assist Module Substats - Core",        "Modules",          0, null, 30);
        lab("Assist Module Bonus - Cannon",         "Modules",          0, null, 30);
        lab("Assist Module Bonus - Armor",          "Modules",          0, null, 30);
        lab("Assist Module Bonus - Generator",      "Modules",          0, null, 30);
        lab("Assist Module Bonus - Core",           "Modules",          0, null, 30);

        // BATTLE CONDITION
        lab("Battle Condition Reduction",           "Battle Condition", 0, null, 10);
        lab("Knockback Resistance",                 "Battle Condition", 0, null, 20);
        lab("Thorns Resistance",                    "Battle Condition", 0, null, 20);
        lab("Orb Resistance",                       "Battle Condition", 0, null, 20);
        lab("Plasma Cannon Resistance",             "Battle Condition", 0, null, 20);
        lab("Death Ray Resistance",                 "Battle Condition", 0, null, 20);
        lab("Armored Enemies",                      "Battle Condition", 0, null, 20);
        lab("More Enemies",                         "Battle Condition", 0, null, 20);
        lab("Enemy Speed",                          "Battle Condition", 0, null, 20);
        lab("Enemy Attack Speed",                   "Battle Condition", 0, null, 20);
        lab("Fast's Ultimate",                      "Battle Condition", 0, null, 10);
        lab("Ranged Ultimate",                      "Battle Condition", 0, null, 10);
        lab("Boss's Ultimate",                      "Battle Condition", 0, null, 10);
        lab("Basic's Ultimate",                     "Battle Condition", 0, null, 10);
        lab("Tank's Ultimate",                      "Battle Condition", 0, null, 10);
        lab("Protector's Ultimate",                 "Battle Condition", 0, null, 10);
        lab("Ultimate Weapon Durations",            "Battle Condition", 0, null, 10);
        lab("Death Defy Down",                      "Battle Condition", 0, null, 10);
        lab("Energy Shields Down",                  "Battle Condition", 0, null, 10);
        lab("Enemy Level Skip Reduction",           "Battle Condition", 0, null, 10);
    }

    private void lab(String name, String category, int currentLevel, Integer targetLevel, int maxLevel) {
        Long id = jdbc.queryForObject(
                "INSERT INTO lab (name, category, max_level) VALUES (?,?,?) RETURNING id",
                Long.class, name, category, maxLevel);
        if (id == null) return;
        jdbc.update(
                "INSERT INTO lab_player_state (lab_id, current_level, target_level) VALUES (?,?,?)",
                id, currentLevel, targetLevel);
    }
}
