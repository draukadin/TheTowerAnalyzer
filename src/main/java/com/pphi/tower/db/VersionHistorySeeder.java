package com.pphi.tower.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class VersionHistorySeeder {

    private final JdbcTemplate jdbc;

    public VersionHistorySeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM tower_version", Integer.class);
        if (count != null && count > 0) return;
        seedVersions();
    }

    private void seedVersions() {
        version("0.1.0", "Major", "Pre Modules to 140s");
        version("1.0.0", "Major", "Initial");
        version("1.1.0", "Minor", "Modules Upgraded 101 → 140s");
        change("1.1.0", "MODULE", "Modules", "101", "140s", null);

        version("1.2.0", "Minor", "Spotlight Qty One 1 → 2 beams");
        change("1.2.0", "UW", "Spotlight Quantity", "1", "2", "beams");

        version("1.3.0", "Minor", "Chain Thunder Lvl 1; Def% 25 → 26");
        change("1.3.0", "UW", "Chain Thunder", null, "1", null);
        change("1.3.0", "LAB", "Defense %", "25", "26", null);

        version("2.0.0", "Major", "Coin Bonus+ 20 → 21");
        change("2.0.0", "LAB", "Coin Bonus+", "20", "21", null);

        version("2.0.1", "Patch", "Coin Bonus+ 21 → 22");
        change("2.0.1", "LAB", "Coin Bonus+", "21", "22", null);

        version("2.0.2", "Patch", "Cell / Kill Bonus 19 → 20");
        change("2.0.2", "LAB", "Cell / Kill Bonus", "19", "20", null);

        version("2.0.3", "Patch", "Cell / Kill Bonus 20 → 21");
        change("2.0.3", "LAB", "Cell / Kill Bonus", "20", "21", null);

        version("2.1.0", "Minor", "Attack Speed Lab 61 → 62");
        change("2.1.0", "LAB", "Attack Speed", "61", "62", null);

        version("2.2.0", "Minor", "Death Wave Effect Wave Quantity 1 → 2");
        change("2.2.0", "UW", "Death Wave Effect Wave Quantity", "1", "2", null);

        version("2.2.1", "Patch", "Armor 148 → 149 & Core 148 → 149; Summon CD 93 → 92 seconds; Coin Bonus+ Lvl 22 → 23");
        change("2.2.1", "MODULE", "Armor Module", "148", "149", null);
        change("2.2.1", "MODULE", "Core Module", "148", "149", null);
        change("2.2.1", "LAB", "Summon Cooldown", "93", "92", "seconds");
        change("2.2.1", "LAB", "Coin Bonus+", "22", "23", null);

        version("2.2.2", "Patch", "Generator Module 148 → 149; Cell / Kill Bonus 21 → 22");
        change("2.2.2", "MODULE", "Generator Module", "148", "149", null);
        change("2.2.2", "LAB", "Cell / Kill Bonus", "21", "22", null);

        version("2.2.3", "Patch", "Attack Speed Lab 62 → 63; Coin Bonus+ 23 → 24");
        change("2.2.3", "LAB", "Attack Speed", "62", "63", null);
        change("2.2.3", "LAB", "Coin Bonus+", "23", "24", null);

        version("2.2.4", "Patch", "Summon CD 92 → 91 seconds");
        change("2.2.4", "LAB", "Summon Cooldown", "92", "91", "seconds");

        version("2.2.5", "Patch", "Cell / Kill Bonus 22 → 23; Attack Speed Lab 63 → 64");
        change("2.2.5", "LAB", "Cell / Kill Bonus", "22", "23", null);
        change("2.2.5", "LAB", "Attack Speed", "63", "64", null);

        version("2.2.6", "Patch", "Cannon Module 149 → 150; Cell / Kill Bonus 23 → 24; DW Armor Stripping 0 → 1");
        change("2.2.6", "MODULE", "Cannon Module", "149", "150", null);
        change("2.2.6", "LAB", "Cell / Kill Bonus", "23", "24", null);
        change("2.2.6", "UW", "Death Wave Armor Stripping", "0", "1", null);

        version("2.2.7", "Patch", "Armor Module 147 → 148");
        change("2.2.7", "MODULE", "Armor Module", "147", "148", null);

        version("2.2.8", "Patch", "T11 Attack Dissonance (W4856) Tournament Attack 1.20 → 1.22");
        change("2.2.8", "OTHER", "Tournament Attack Dissonance", "1.20", "1.22", "T11 W4856");

        version("2.2.9", "Patch", "T11 UW Dissonance (W2805) Tournament UW 1.20 → 1.21; Chain Thunder 1 → 2");
        change("2.2.9", "OTHER", "Tournament UW Dissonance", "1.20", "1.21", "T11 W2805");
        change("2.2.9", "UW", "Chain Thunder", "1", "2", null);

        version("2.2.10", "Patch", "Chrono Field 14 → 15");
        change("2.2.10", "UW", "Chrono Field Duration", "14", "15", null);

        version("2.2.11", "Patch", "Attack Speed Lab 64 → 65");
        change("2.2.11", "LAB", "Attack Speed", "64", "65", null);

        version("2.2.12", "Patch", "Coin Bonus+ 24 → 25; Cell / Kill Bonus 24 → 25; ELS+ 0 → 4; Lightning Amplifier - Scatter 1 → 2");
        change("2.2.12", "LAB", "Coin Bonus+", "24", "25", null);
        change("2.2.12", "LAB", "Cell / Kill Bonus", "24", "25", null);
        change("2.2.12", "LAB", "Enemy Level Skip+", "0", "4", null);
        change("2.2.12", "UW", "Lightning Amplifier Scatter", "1", "2", null);

        version("2.2.13", "Patch", "Dangerous Tricks Relic (2% Attack Speed); Critical Factor+ 52 → 53; Core Module 148 → 149");
        change("2.2.13", "RELIC", "Dangerous Tricks", null, "2% Attack Speed", "Guild Season 9 relic added");
        change("2.2.13", "LAB", "Critical Factor+", "52", "53", null);
        change("2.2.13", "MODULE", "Core Module", "148", "149", null);

        version("2.2.14", "Patch", "Generator 149 → 150; Summon Cooldown 91 → 90; ELS+ 4 → 5; Viral Infection Relic 2% Health; T11 UW Dissonance (W4524) Tournament UW 1.21 → 1.22");
        change("2.2.14", "MODULE", "Generator Module", "149", "150", null);
        change("2.2.14", "LAB", "Summon Cooldown", "91", "90", null);
        change("2.2.14", "LAB", "Enemy Level Skip+", "4", "5", null);
        change("2.2.14", "RELIC", "Viral Infection", null, "2% Health", "Standard relic added");
        change("2.2.14", "OTHER", "Tournament UW Dissonance", "1.21", "1.22", "T11 W4524");

        version("2.2.15", "Patch", "EHLS 1 → 3; Death Wave Armor Stripping 1 → 2; Chrono Field Duration 15 → 16");
        change("2.2.15", "LAB", "Enemy Health Level Skip", "1", "3", null);
        change("2.2.15", "UW", "Death Wave Armor Stripping", "1", "2", null);
        change("2.2.15", "UW", "Chrono Field Duration", "15", "16", null);

        version("3.0.0", "Major", "Chain Lightning Quantity 3 → 4; Black Hole Duration 18 → 26; Spot Light Angle 31 → 35; ELS+ 4 → 5");
        change("3.0.0", "UW", "Chain Lightning Quantity", "3", "4", null);
        change("3.0.0", "UW", "Black Hole Duration", "18", "26", null);
        change("3.0.0", "UW", "Spotlight Angle", "31", "35", null);
        change("3.0.0", "LAB", "Enemy Level Skip+", "4", "5", null);

        version("3.0.1", "Patch", "Damage+ 52 → 53; Super Crit Multi+ 52 → 53; Attack Speed 65 → 66; EHLS 3 → 6");
        change("3.0.1", "LAB", "Damage+", "52", "53", null);
        change("3.0.1", "LAB", "Super Crit Multi+", "52", "53", null);
        change("3.0.1", "LAB", "Attack Speed", "65", "66", null);
        change("3.0.1", "LAB", "Enemy Health Level Skip", "3", "6", null);

        version("3.0.2", "Patch", "EHLS 6 → 7; Chrono Field Duration 16 → 17; EHLS/EALS 269 → 270; Health+/Wall Health+/Health Regen+ 46 → 47");
        change("3.0.2", "LAB", "Enemy Health Level Skip", "6", "7", null);
        change("3.0.2", "UW", "Chrono Field Duration", "16", "17", null);
        change("3.0.2", "LAB", "Enemy Health Level Skip / Enemy Attack Level Skip", "269", "270", null);
        change("3.0.2", "LAB", "Health+ / Wall Health+ / Health Regen+", "46", "47", null);

        version("3.0.3", "Patch", "Health+/Wall Health+/Health Regen+ 47 → 48; EHLS 7 → 8; ELS+ 5 → 6");
        change("3.0.3", "LAB", "Health+ / Wall Health+ / Health Regen+", "47", "48", null);
        change("3.0.3", "LAB", "Enemy Health Level Skip", "7", "8", null);
        change("3.0.3", "LAB", "Enemy Level Skip+", "5", "6", null);

        version("3.0.4", "Patch", "EHLS 8 → 9; Health+/Wall Health+/Health Regen+ 48 → 49; Gold Bot Bonus 7.0x → 7.2x");
        change("3.0.4", "LAB", "Enemy Health Level Skip", "8", "9", null);
        change("3.0.4", "LAB", "Health+ / Wall Health+ / Health Regen+", "48", "49", null);
        change("3.0.4", "LAB", "Gold Bot Bonus", "7.0x", "7.2x", null);

        version("3.1.0", "Minor", "Attack Speed 66 → 67; EHLS 9 → 10; Chrono Field CoolDown 170 → 160; Health+/Wall Health+/Health Regen+ 49 → 51; Armor Module 148 → 149");
        change("3.1.0", "LAB", "Attack Speed", "66", "67", null);
        change("3.1.0", "LAB", "Enemy Health Level Skip", "9", "10", null);
        change("3.1.0", "UW", "Chrono Field Cooldown", "170", "160", null);
        change("3.1.0", "LAB", "Health+ / Wall Health+ / Health Regen+", "49", "51", null);
        change("3.1.0", "MODULE", "Armor Module", "148", "149", null);

        version("3.1.1", "Patch", "EHLS 10 → 11");
        change("3.1.1", "LAB", "Enemy Health Level Skip", "10", "11", null);

        version("3.1.2", "Patch", "Chrono Field Duration 17 → 18; Recovery Package Change 0 → 1; Health+/Wall Health+/Health Regen+ 51 → 52");
        change("3.1.2", "UW", "Chrono Field Duration", "17", "18", null);
        change("3.1.2", "LAB", "Recovery Package", "0", "1", null);
        change("3.1.2", "LAB", "Health+ / Wall Health+ / Health Regen+", "51", "52", null);

        version("3.1.3", "Patch", "Recovery Package Change 1 → 2; EHLS 11 → 12");
        change("3.1.3", "LAB", "Recovery Package", "1", "2", null);
        change("3.1.3", "LAB", "Enemy Health Level Skip", "11", "12", null);

        version("3.1.4", "Patch", "Health+/Wall Health+/Health Regen+ 51 → 52; Recovery Package Change 2 → 3");
        change("3.1.4", "LAB", "Health+ / Wall Health+ / Health Regen+", "51", "52", null);
        change("3.1.4", "LAB", "Recovery Package", "2", "3", null);

        version("3.1.5", "Patch", "Recovery Package Change 3 → 5; EHLS 12 → 13; Health+/Wall Health+/Health Regen+ 52 → 53");
        change("3.1.5", "LAB", "Recovery Package", "3", "5", null);
        change("3.1.5", "LAB", "Enemy Health Level Skip", "12", "13", null);
        change("3.1.5", "LAB", "Health+ / Wall Health+ / Health Regen+", "52", "53", null);

        version("3.1.6", "Patch", "Health+/Wall Health+/Health Regen+ 53 → 54; Attack Speed 67 → 68; Recovery Package Change 5 → 6; EHLS 13 → 14");
        change("3.1.6", "LAB", "Health+ / Wall Health+ / Health Regen+", "53", "54", null);
        change("3.1.6", "LAB", "Attack Speed", "67", "68", null);
        change("3.1.6", "LAB", "Recovery Package", "5", "6", null);
        change("3.1.6", "LAB", "Enemy Health Level Skip", "13", "14", null);
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
