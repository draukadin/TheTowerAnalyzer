package com.pphi.tower.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ModuleSeeder {

    private final JdbcTemplate jdbc;

    public ModuleSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM module_def", Integer.class);
        if (count != null && count > 0) return;

        seedDefs();
        seedPlayerState();
    }

    // ── Static definitions ────────────────────────────────────────────────────

    private void seedDefs() {
        // Cannon modules (sort 1-6)
        def(1, "AD", "Astral Deliverance", "Cannon",
            "Bounce shot's range is increased by 3% of tower's total range. Each bounce increases the projectile's damage by [x]%.", 1,
            "20%", "40%", "60%", "80%");
        def(2, "BA", "Being Annihilator", "Cannon",
            "When you super crit, your next [x] attacks are guaranteed super crits.", 2,
            "3", "4", "5", "6");
        def(3, "DP", "Death Penalty", "Cannon",
            "Chance of [x]% to mark an enemy for death when it spawns, causing the first hit to destroy it.", 3,
            "5%", "8%", "11%", "15%");
        def(4, "HB", "Havoc Bringer", "Cannon",
            "[x]% chance for rend armor to instantly go to max.", 4,
            "10%", "13%", "15%", "20%");
        def(5, "SR", "Shrink Ray", "Cannon",
            "Attacks have a 1% chance to apply a non-stacking effect that decreases the enemy's mass by [x]%.", 5,
            "10%", "20%", "30%", "40%");
        def(6, "AS", "Amplifying Strike", "Cannon",
            "Killing a boss or elite enemy increases Tower damage by 5x for [x]s.", 6,
            "5s", "11s", "18s", "26s");

        // Generator modules (sort 7-12)
        def(7,  "SH",  "Singularity Harness", "Generator",
            "Increase the range of each bot by +[x]m. Enemies hit by the Flame Bot receive double damage.", 7,
            "5", "8", "11", "15");
        def(8,  "GC",  "Galaxy Compressor", "Generator",
            "Collecting a recovery package reduces the cooldown of all Ultimate Weapons except Poison Swamp by [x]s.", 8,
            "10s", "13s", "17s", "20s");
        def(9,  "PH",  "Pulsar Harvester", "Generator",
            "Each time a projectile hits an enemy, there is a [x]% chance that it will reduce the enemy's Health and Attack level by 1 (diminishing returns after 100 reductions).", 9,
            "1.0%", "1.5%", "2.0%", "2.5%");
        def(10, "BHD", "Black Hole Digestor", "Generator",
            "Temporarily get [x]% extra Coins/Kill Bonus for each free upgrade you got on the current wave. Free Upgrades cannot increase Tower Range.", 10,
            "3%", "5%", "7%", "10%");
        def(11, "PF",  "Project Funding", "Generator",
            "Tower damage is multiplied by [x]% of the number of digits in your current cash.", 11,
            "13%", "25%", "50%", "100%");
        def(12, "RB",  "Restorative Bonus", "Generator",
            "Packages grant a 50% attack speed boost for [x]s, decaying for 60 seconds.", 12,
            "15s", "20s", "25s", "30s");

        // Armor modules (sort 13-18)
        def(13, "ACP", "Anti-Cube Portal", "Armor",
            "Enemies take [x]x damage for 7s after they are hit by a shockwave.", 13,
            "10x", "15x", "20x", "25x");
        def(14, "NMP", "Negative Mass Projector", "Armor",
            "If an orb doesn't kill the enemy it will apply a stacking debuff, reducing its damage and speed by [x]% per hit, to a max reduction of 50%.", 14,
            "1.0%", "1.5%", "2.0%", "2.5%");
        def(15, "WHR", "Wormhole Redirector", "Armor",
            "Health Regen can heal up to [x]% of Package Max Recovery.", 15,
            "25%", "50%", "75%", "100%");
        def(16, "SD",  "Space Displacer", "Armor",
            "Landmines have a [x]% chance to spawn as an Inner Land Mine (20 max) instead of a normal mine. These mines autonomously move and organize around the tower.", 16,
            "15%", "20%", "25%", "30%");
        def(17, "SF",  "Sharp Fortitude", "Armor",
            "Increase the Wall's health and regen by [x]. Enemies take +1% increased damage from wall thorns per subsequent hit.", 17,
            "1.25x", "1.5x", "2x", "2.5x");
        def(18, "OA",  "Orbital Augment", "Armor",
            "Orbital Augment adds [x] orbiting Electrons around the tower. Each Electron deals damage equal to 15% of the enemy's remaining health (quarter effective against Bosses and Fleets).", 18,
            "2", "4", "6", "8");

        // Core modules (sort 19-24)
        def(19, "OC",  "Om Chip", "Core",
            "Spotlight will rotate to focus a Boss. Bosses reflect the light around it to nearby enemies, increasing by [x].", 19,
            "2", "4", "7", "15");
        def(20, "HC",  "Harmony Conductor", "Core",
            "[x]% chance of poisoned enemies to miss-attack (bosses chance is halved).", 20,
            "15%", "20%", "25%", "30%");
        def(21, "DC",  "Dimension Core", "Core",
            "Chain lightning have 60% chance of hitting the initial target. Shock chance and multiplier is doubled. If shock is applied again to the same enemy the shock multiplier will add up to a max stack of [x].", 21,
            "5", "10", "15", "20");
        def(22, "MVN", "Multiverse Nexus", "Core",
            "Death Wave, Golden Tower and Black Hole will always activate at the same time, but the cooldown will be the average of those +/-[x]s.", 22,
            "20s", "10s", "1s", "-10s");
        def(23, "MH",  "Magnetic Hook", "Core",
            "[x] Inner Land Mines are fired at Bosses as they enter Tower range. 25% of Elites have Inner Land Mines fired at them as they enter Tower range.", 23,
            "1", "2", "3", "4");
        def(24, "PC",  "Primordial Collapse", "Core",
            "Spawns one additional Black Hole. Damage from enemies within a Black Hole is decreased by [x]%.", 24,
            "50%", "55%", "65%", "80%");
    }

    private void def(int id, String code, String name, String type, String effectTemplate, int sortOrder,
                     String epic, String legendary, String mythic, String ancestral) {
        jdbc.update(
            "INSERT OR IGNORE INTO module_def (id, code, name, type, effect_template, sort_order) VALUES (?,?,?,?,?,?)",
            id, code, name, type, effectTemplate, sortOrder
        );
        jdbc.update("INSERT OR IGNORE INTO module_player_state (module_def_id) VALUES (?)", id);
        jdbc.update("INSERT OR IGNORE INTO module_player_meta (module_def_id) VALUES (?)", id);
        ability(id, "Epic",      epic);
        ability(id, "Legendary", legendary);
        ability(id, "Mythic",    mythic);
        ability(id, "Ancestral", ancestral);
    }

    private void ability(int moduleDefId, String rarity, String value) {
        jdbc.update(
            "INSERT OR IGNORE INTO module_ability_value (module_def_id, rarity, value) VALUES (?,?,?)",
            moduleDefId, rarity, value
        );
    }

    // ── Player state seed (Amplifying Strike — Ancestral 3★, level 151) ──────

    private void seedPlayerState() {
        // AS is id=6
        jdbc.update("""
                INSERT OR IGNORE INTO module_player_state (module_def_id, owned, rarity, stars, level)
                VALUES (6, 1, 'Ancestral', 3, 151)
                ON CONFLICT(module_def_id) DO UPDATE SET
                    owned=1, rarity='Ancestral', stars=3, level=151
                """);

        substat(6, 0, "super_crit_multi",  "Epic");
        substat(6, 1, "attack_range",      "Epic");
        substat(6, 2, "attack_speed",      "Common");
        substat(6, 3, "critical_factor",   "Common");
        substat(6, 4, "critical_chance",   "Common");

        jdbc.update("""
                INSERT OR IGNORE INTO module_player_copy (module_def_id, copy_index, copy_rarity)
                VALUES (6, 0, 'Epic')
                """);
    }

    private void substat(int moduleDefId, int slot, String key, String rarity) {
        jdbc.update("""
                INSERT OR IGNORE INTO module_player_substat (module_def_id, slot_index, substat_key, substat_rarity)
                VALUES (?,?,?,?)
                """, moduleDefId, slot, key, rarity);
    }
}
