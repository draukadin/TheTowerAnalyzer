package com.pphi.tower.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class UwSeeder {

    private static final Logger log = LoggerFactory.getLogger(UwSeeder.class);

    private final JdbcTemplate jdbc;

    public UwSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM uw", Integer.class);
        if (count != null && count > 0) return;

        log.info("Seeding {}...", this.getClass().getSimpleName().replace("Seeder", ""));
        seedUws();
        seedAllStats();
        log.info("Finished seeding {}", this.getClass().getSimpleName().replace("Seeder", ""));
    }

    private void seedUws() {
        Object[][] uws = {
            {1, "CL",  "Chain Lightning", "Smite"},
            {2, "SM",  "Smart Missiles",  "Cover Fire"},
            {3, "DW",  "Death Wave",      "Kill Wall"},
            {4, "CF",  "Chrono Field",    "Chrono Loop"},
            {5, "ILM", "Inner Land Mines","Charged Mines"},
            {6, "GT",  "Golden Tower",    "Golden Combo"},
            {7, "PS",  "Poison Swamp",    "Death Creep"},
            {8, "BH",  "Black Hole",      "Consume"},
            {9, "SP",  "Spotlight",       "Light Range"}
        };
        for (Object[] row : uws) {
            jdbc.update("INSERT OR IGNORE INTO uw (id, code, name, uw_plus_name) VALUES (?,?,?,?)", row);
            jdbc.update("INSERT OR IGNORE INTO uw_player_state (uw_id) VALUES (?)", row[0]);
        }
    }

    // ── Stat IDs: uw_id * 10 + sort_order (e.g. CL Damage = 11, CL Smite = 14) ──

    private void seedAllStats() {
        // Chain Lightning
        seedStat(11, 1, "STAT_1",  "Damage",   1, CL_DAMAGE);
        seedStat(12, 1, "STAT_2",  "Quantity", 2, CL_QUANTITY);
        seedStat(13, 1, "STAT_3",  "Chance",   3, CL_CHANCE);
        seedStat(14, 1, "UW_PLUS", "Smite",    4, CL_SMITE);

        // Smart Missiles
        seedStat(21, 2, "STAT_1",  "Damage",     1, SM_DAMAGE);
        seedStat(22, 2, "STAT_2",  "Quantity",   2, SM_QUANTITY);
        seedStat(23, 2, "STAT_3",  "Cooldown",   3, SM_COOLDOWN);
        seedStat(24, 2, "UW_PLUS", "Cover Fire", 4, SM_COVER_FIRE);

        // Death Wave
        seedStat(31, 3, "STAT_1",  "Damage",    1, DW_DAMAGE);
        seedStat(32, 3, "STAT_2",  "Quantity",  2, DW_QUANTITY);
        seedStat(33, 3, "STAT_3",  "Cooldown",  3, DW_COOLDOWN);
        seedStat(34, 3, "UW_PLUS", "Kill Wall", 4, DW_KILL_WALL);

        // Chrono Field
        seedStat(41, 4, "STAT_1",  "Duration",     1, CF_DURATION);
        seedStat(42, 4, "STAT_2",  "-Speed",        2, CF_SPEED);
        seedStat(43, 4, "STAT_3",  "Cooldown",     3, CF_COOLDOWN);
        seedStat(44, 4, "UW_PLUS", "Chrono Loop",  4, CF_CHRONO_LOOP);

        // Inner Land Mines
        seedStat(51, 5, "STAT_1",  "Damage",         1, ILM_DAMAGE);
        seedStat(52, 5, "STAT_2",  "Quantity",        2, ILM_QUANTITY);
        seedStat(53, 5, "STAT_3",  "Cooldown",        3, ILM_COOLDOWN);
        seedStat(54, 5, "UW_PLUS", "Charged Mines",   4, ILM_CHARGED_MINES);

        // Golden Tower
        seedStat(61, 6, "STAT_1",  "Multiplier",    1, GT_MULTIPLIER);
        seedStat(62, 6, "STAT_2",  "Duration",      2, GT_DURATION);
        seedStat(63, 6, "STAT_3",  "Cooldown",      3, GT_COOLDOWN);
        seedStat(64, 6, "UW_PLUS", "Golden Combo",  4, GT_GOLDEN_COMBO);

        // Poison Swamp
        seedStat(71, 7, "STAT_1",  "Damage",       1, PS_DAMAGE);
        seedStat(72, 7, "STAT_2",  "Duration",     2, PS_DURATION);
        seedStat(73, 7, "STAT_3",  "Cooldown",     3, PS_COOLDOWN);
        seedStat(74, 7, "UW_PLUS", "Death Creep",  4, PS_DEATH_CREEP);

        // Black Hole
        seedStat(81, 8, "STAT_1",  "Size",     1, BH_SIZE);
        seedStat(82, 8, "STAT_2",  "Duration", 2, BH_DURATION);
        seedStat(83, 8, "STAT_3",  "Cooldown", 3, BH_COOLDOWN);
        seedStat(84, 8, "UW_PLUS", "Consume",  4, BH_CONSUME);

        // Spotlight
        seedStat(91, 9, "STAT_1",  "Multiplier",   1, SP_MULTIPLIER);
        seedStat(92, 9, "STAT_2",  "Angle",         2, SP_ANGLE);
        seedStat(93, 9, "STAT_3",  "Quantity",      3, SP_QUANTITY);
        seedStat(94, 9, "UW_PLUS", "Light Range",   4, SP_LIGHT_RANGE);
    }

    /**
     * @param levels double[n][2] where [i][0]=stat value, [i][1]=cost to reach level i from i-1.
     *               stones_to_next at level i = levels[i+1][1], null at max level.
     */
    private void seedStat(int statId, int uwId, String statKey, String label, int sortOrder, double[][] levels) {
        int maxLevel = levels.length - 1;
        jdbc.update(
            "INSERT OR IGNORE INTO uw_stat (id, uw_id, stat_key, label, max_level, sort_order) VALUES (?,?,?,?,?,?)",
            statId, uwId, statKey, label, maxLevel, sortOrder
        );
        jdbc.update(
            "INSERT OR IGNORE INTO uw_stat_player_level (uw_stat_id, current_level) VALUES (?,0)",
            statId
        );
        jdbc.update(
            "INSERT OR IGNORE INTO uw_stat_target_level (uw_stat_id, target_level) VALUES (?,0)",
            statId
        );
        for (int i = 0; i < levels.length; i++) {
            Integer stonesToNext = (i < levels.length - 1) ? (int) levels[i + 1][1] : null;
            jdbc.update(
                "INSERT OR IGNORE INTO uw_stat_level_value (uw_stat_id, level, value, stones_to_next) VALUES (?,?,?,?)",
                new Object[]{statId, i, levels[i][0], stonesToNext}
            );
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Reference data — [value, cost_to_reach_this_level_from_previous]
    // ════════════════════════════════════════════════════════════════════════

    // ── Chain Lightning ──────────────────────────────────────────────────────

    private static final double[][] CL_DAMAGE = {
        {2,0},{3,5},{5,11},{9,17},{14,23},{22,29},{32,35},{46,41},{63,47},{85,53},
        {113,61},{148,71},{191,84},{244,100},{309,120},{387,144},{482,174},{596,210},
        {733,252},{898,302},{1094,362},{1328,434},{1607,525},{1937,636},{2329,767},
        {2794,923},{3342,1109},{3990,1295},{4755,1521},{5655,1787},{6715,2103},{7961,2469}
    };

    private static final double[][] CL_QUANTITY = {
        {1,0},{2,30},{3,75},{4,150},{5,400}
    };

    private static final double[][] CL_CHANCE = {
        {5.0,0},{6.5,8},{8.0,26},{9.5,44},{11.0,62},{12.5,80},{14.0,98},{15.5,116},
        {17.0,134},{18.5,152},{20.0,170},{21.5,188},{23.0,206},{24.5,224},{26.0,242},{27.5,260}
    };

    // value = smite chance (%)
    private static final double[][] CL_SMITE = {
        {0.05,0},{0.10,300},{0.15,375},{0.20,475},{0.25,600},{0.30,725},
        {0.35,925},{0.40,1150},{0.45,1450},{0.50,1800},{0.55,2200},{0.60,2650}
    };

    // ── Smart Missiles ───────────────────────────────────────────────────────

    private static final double[][] SM_DAMAGE = {
        {10,0},{11,5},{13,11},{16,17},{20,23},{26,29},{34,35},{43,41},{55,47},{69,53},
        {87,61},{108,71},{134,84},{164,100},{200,120},{243,144},{293,174},{352,210},
        {421,252},{502,302},{597,362},{708,432},{838,528},{989,654},{1165,810},
        {1370,996},{1608,1222},{1886,1488},{2209,1804},{2585,2180},{3021,2636}
    };

    private static final double[][] SM_QUANTITY = {
        {5,0},{6,4},{7,12},{8,35},{9,70},{10,120},{11,180},{12,275},{13,350},
        {14,420},{15,500},{16,600},{17,750},{18,950},{19,1200},{20,1500}
    };

    // value = cooldown in seconds
    private static final double[][] SM_COOLDOWN = {
        {180,0},{170,8},{160,24},{150,40},{140,56},{130,72},{120,88},{110,104},
        {100,120},{90,136},{80,152},{70,168},{60,184},{50,200},{40,216},{30,232},{20,750}
    };

    // value = CD reduction in seconds
    private static final double[][] SM_COVER_FIRE = {
        {13,0},{12,300},{11,375},{10,475},{9,600},{8,725},{7,925},{6,1150},
        {5,1450},{4,1800},{3,2200},{2,2650}
    };

    // ── Death Wave ───────────────────────────────────────────────────────────

    private static final double[][] DW_DAMAGE = {
        {2,0},{3,5},{5,11},{9,17},{14,23},{22,29},{32,35},{46,41},{63,47},{85,53},
        {113,61},{148,71},{191,84},{244,100},{309,120},{387,144},{482,174},{596,210},
        {723,254},{877,308},{1064,374},{1290,452},{1569,558},{1916,694},{2356,880},
        {2919,1126},{3637,1432},{4544,1813},{5678,2269},{7078,2800},{9119,3406}
    };

    private static final double[][] DW_QUANTITY = {
        {1,0},{2,200},{3,500},{4,850},{5,1400}
    };

    // value = cooldown in seconds
    private static final double[][] DW_COOLDOWN = {
        {300,0},{290,8},{280,24},{270,40},{260,56},{250,72},{240,88},{230,104},
        {220,120},{210,136},{200,152},{190,168},{180,184},{170,200},{160,216},
        {150,232},{140,248},{130,264},{120,280},{110,346},{100,512},{90,688},
        {80,874},{70,1070},{60,1276},{50,1492}
    };

    // value = kill multiplier
    private static final double[][] DW_KILL_WALL = {
        {3,0},{4,400},{6,500},{9,610},{13,730},{18,860},{24,1000},{31,1150},
        {39,1300},{48,1500},{58,1700},{69,1950},{81,2200},{94,2450},{108,2700}
    };

    // ── Chrono Field ─────────────────────────────────────────────────────────

    // value = duration in seconds
    private static final double[][] CF_DURATION = {
        {5,0},{6,5},{7,14},{8,23},{9,32},{10,41},{11,50},{12,59},{13,68},{14,77},
        {15,86},{16,95},{17,104},{18,113},{19,122},{20,131},{21,140},{22,149},
        {23,158},{24,167},{25,176},{26,185},{27,194},{28,203},{29,212},{30,221},
        {31,230},{32,239},{33,248},{34,257},{35,266},{36,275},{37,284},{38,293},
        {39,302},{40,311}
    };

    // value = speed reduction (%)
    private static final double[][] CF_SPEED = {
        {20,0},{25,15},{30,25},{35,40},{40,60},{45,120},{50,150},{55,200},
        {60,300},{65,450},{70,650},{75,900}
    };

    // value = cooldown in seconds
    private static final double[][] CF_COOLDOWN = {
        {180,0},{170,10},{160,31},{150,52},{140,73},{130,94},{120,115},{110,136},
        {100,157},{90,178},{80,199},{70,220},{60,241}
    };

    // value = rotation rate in rad/2s
    private static final double[][] CF_CHRONO_LOOP = {
        {0.10,0},{0.15,400},{0.20,500},{0.25,610},{0.30,730},{0.35,860},
        {0.40,1000},{0.45,1150},{0.50,1300},{0.55,1500},{0.60,1700},
        {0.65,1950},{0.70,2200},{0.75,2450}
    };

    // ── Inner Land Mines ─────────────────────────────────────────────────────

    private static final double[][] ILM_DAMAGE = {
        {10,0},{11,5},{13,11},{16,17},{20,23},{26,29},{34,35},{43,41},{55,47},{69,53},
        {87,61},{108,71},{134,84},{164,100},{200,120},{243,144},{293,174},{352,210},
        {421,254},{502,308},{597,374},{708,454},{838,540},{989,632},{1165,730},
        {1370,834},{1608,944},{1886,1060},{2209,1182},{2585,1312},{3021,1448}
    };

    private static final double[][] ILM_QUANTITY = {
        {3,0},{4,50},{5,125},{6,250}
    };

    // value = cooldown in seconds
    private static final double[][] ILM_COOLDOWN = {
        {200,0},{190,8},{180,24},{170,40},{160,56},{150,72},{140,88},{130,104},
        {120,120},{110,136},{100,152},{90,168},{80,184},{70,200},{60,216},{50,232}
    };

    // value = charge rate per second
    private static final double[][] ILM_CHARGED_MINES = {
        {0.50,0},{1.50,300},{2.90,360},{4.70,430},{6.90,510},{9.50,620},
        {12.50,750},{15.90,900},{19.70,1100},{23.90,1350},{28.50,1650},
        {33.50,2000},{38.90,2400},{44.70,2850},{50.90,3350}
    };

    // ── Golden Tower ─────────────────────────────────────────────────────────

    private static final double[][] GT_MULTIPLIER = {
        {5.0,0},{5.8,5},{6.6,13},{7.4,22},{8.2,32},{9.0,43},{9.8,55},{10.6,68},
        {11.4,82},{12.2,98},{13.0,116},{13.8,138},{14.6,162},{15.4,250},{16.2,350},
        {17.0,500},{17.8,700},{18.6,950},{19.4,1250},{20.2,1600},{21.0,2000}
    };

    // value = duration in seconds
    private static final double[][] GT_DURATION = {
        {15,0},{16,5},{17,14},{18,23},{19,32},{20,41},{21,50},{22,59},{23,68},{24,77},
        {25,87},{26,98},{27,110},{28,123},{29,137},{30,152},{31,168},{32,185},
        {33,203},{34,222},{35,242},{36,263},{37,285},{38,308},{39,332},{40,356},
        {41,380},{42,404},{43,428},{44,452},{45,476},{46,530},{47,614},{48,728},
        {49,872},{50,1046},{51,1250},{52,1484},{53,1748}
    };

    // value = cooldown in seconds
    private static final double[][] GT_COOLDOWN = {
        {300,0},{290,10},{280,28},{270,46},{260,64},{250,82},{240,100},{230,118},
        {220,136},{210,154},{200,172},{190,190},{180,208},{170,226},{160,244},
        {150,262},{140,300},{130,368},{120,476},{110,644},{100,872}
    };

    // value = bonus cash/coin chance (%)
    private static final double[][] GT_GOLDEN_COMBO = {
        {0.03,0},{0.06,300},{0.09,360},{0.12,430},{0.15,510},{0.18,620},
        {0.21,750},{0.24,900},{0.27,1100},{0.30,1350},{0.33,1650},{0.36,2050},
        {0.39,2600},{0.42,3300},{0.45,4150}
    };

    // ── Poison Swamp ─────────────────────────────────────────────────────────

    private static final double[][] PS_DAMAGE = {
        {10,0},{11,5},{13,11},{16,17},{20,23},{26,29},{34,35},{43,41},{55,47},{69,53},
        {87,61},{108,71},{134,84},{164,100},{200,120},{243,144},{293,174},{352,210},
        {421,252},{502,302},{597,362},{708,434},{838,525},{989,636},{1165,772},
        {1370,938},{1608,1134},{1886,1360},{2209,1616},{2585,1902},{3021,2228}
    };

    // value = duration in seconds
    private static final double[][] PS_DURATION = {
        {30,0},{35,10},{40,20},{45,35},{50,55},{55,100},{60,120},{65,150},
        {70,200},{75,260},{80,330},{85,410},{90,500},{95,600},{100,710}
    };

    // value = cooldown in seconds
    private static final double[][] PS_COOLDOWN = {
        {125,0},{120,8},{115,26},{110,44},{105,62},{100,80},{95,98},{90,116},
        {85,134},{80,152},{75,170},{70,188},{65,206},{60,224},{55,242},{50,260}
    };

    // value = death creep spawn bonus (%)
    private static final double[][] PS_DEATH_CREEP = {
        {120,0},{190,300},{260,375},{330,475},{400,600},{470,725},{540,925},
        {610,1150},{680,1450},{750,1800},{820,2200},{890,2650},{960,3150},
        {1030,3700},{1110,4300}
    };

    // ── Black Hole ───────────────────────────────────────────────────────────

    // value = radius in meters
    private static final double[][] BH_SIZE = {
        {30,0},{32,5},{34,12},{36,19},{38,26},{40,34},{42,43},{44,53},{46,64},{48,76},
        {50,89},{52,103},{54,118},{56,134},{58,151},{60,169},{62,189},{64,211},
        {66,236},{68,264},{70,295}
    };

    // value = duration in seconds
    private static final double[][] BH_DURATION = {
        {15,0},{16,5},{17,14},{18,23},{19,32},{20,41},{21,50},{22,59},{23,68},{24,77},
        {25,86},{26,95},{27,104},{28,113},{29,122},{30,131},{31,165},{32,224},
        {33,308},{34,417},{35,551},{36,710},{37,894},{38,1103}
    };

    // value = cooldown in seconds
    private static final double[][] BH_COOLDOWN = {
        {200,0},{190,10},{180,28},{170,46},{160,64},{150,82},{140,100},{130,118},
        {120,136},{110,154},{100,172},{90,190},{80,208},{70,226},{60,244},{50,262}
    };

    // value = consume rate (% of wave HP)
    private static final double[][] BH_CONSUME = {
        {0.05,0},{0.10,400},{0.15,500},{0.20,610},{0.25,730},{0.30,860},
        {0.35,1000},{0.40,1150},{0.45,1300},{0.50,1500},{0.55,1700},{0.60,1950},
        {0.65,2200},{0.70,2450},{0.75,2700}
    };

    // ── Spotlight ────────────────────────────────────────────────────────────

    private static final double[][] SP_MULTIPLIER = {
        {8.0,0},{9.4,5},{10.8,13},{12.2,21},{13.6,30},{15.0,40},{16.4,52},{17.8,65},
        {19.2,80},{20.6,95},{22.0,112},{23.4,133},{24.8,150},{26.2,180},{27.6,220},
        {29.0,280},{30.4,320},{31.8,360},{33.2,420},{34.6,500},{36.0,600},{37.4,720},
        {38.8,850},{40.2,1000},{41.6,1175},{43.0,1400}
    };

    // value = angle in degrees
    private static final double[][] SP_ANGLE = {
        {30,0},{31,5},{32,16},{33,27},{34,38},{35,49},{36,60},{37,71},{38,82},{39,93},
        {40,104},{41,115},{42,126},{43,137},{44,148},{45,159},{46,170},{47,181},
        {48,192},{49,203},{50,214},{51,225},{52,236},{53,247},{54,258},{55,269},
        {56,280},{57,291},{58,302},{59,313},{60,324},{61,337},{62,352},{63,369},
        {64,388},{65,409},{66,432},{67,457},{68,484},{69,513},{70,544},{71,577},
        {72,612},{73,649},{74,688},{75,729},{76,772},{77,817},{78,864},{79,913},
        {80,964},{81,1017},{82,1072},{83,1129},{84,1188},{85,1249},{86,1312},
        {87,1377},{88,1444},{89,1513},{90,1584}
    };

    private static final double[][] SP_QUANTITY = {
        {1,0},{2,375},{3,850},{4,2500}
    };

    // value = light range bonus multiplier
    private static final double[][] SP_LIGHT_RANGE = {
        {0.01,0},{0.02,400},{0.03,500},{0.04,610},{0.05,730},{0.06,860},
        {0.07,1000},{0.08,1150},{0.09,1300},{0.10,1500},{0.11,1700},{0.12,1950},
        {0.13,2200},{0.14,2450},{0.15,2700}
    };
}
