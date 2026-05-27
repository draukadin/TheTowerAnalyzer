package com.pphi.tower.model.sheets;

import java.util.List;

public enum TowerTrackerRanges implements GoogleSheet {

    //Workshops Tracker
    WORKSHOP("Master Sheet", List.of("C1:C50", "D1:D50", "N1:N50"), "workshop-tracker"),
    ENHANCEMENTS("Master Sheet", List.of("P1:P20", "R1:R20", "W1:W20", "X1:x20"), "workshop-tracker"),

    // Player and Stuff Trackers
    TIER_WAVE("Master Sheet", List.of("E1:N23"), "player-tracker"),
    VERSION_HISTORY("TowerVersionTracking", List.of("B:B", "C:C", "D:D"), "player-tracker"),
    CURRENCIES("Master Sheet", List.of("B19:C33"), "player-tracker"),

    // Laboratory Trackers
    MAIN_LABS("Master Sheet", List.of("B1:E22"), "laboratory-tracker"),
    ATTACK_LABS("Master Sheet", List.of("G1:J10"), "laboratory-tracker"),
    DEFENSE_LABS("Master Sheet", List.of("L1:O17"), "laboratory-tracker"),
    UTILITY_LABS("Master Sheet", List.of("Q1:T13"), "laboratory-tracker"),
    ULTIMATE_WEAPON_LABS("Master Sheet", List.of("V1:Y40"), "laboratory-tracker"),
    CARD_LABS("Master Sheet", List.of("AA1:AD41"), "laboratory-tracker"),
    PERK_LABS("Master Sheet", List.of("AF1:AI10"), "laboratory-tracker"),
    BOT_LABS("Master Sheet", List.of("AK1:AN11"), "laboratory-tracker"),
    ENEMY_LABS("Master Sheet", List.of("AO1:AS22"), "laboratory-tracker"),
    MODULE_LABS("Master Sheet", List.of("AT1:AX21"), "laboratory-tracker"),
    BATTLE_CONDITION_LABS("Master Sheet", List.of("AZ1:BC21"), "laboratory-tracker"),

    //Guardians
    GUARDIANS("Master Sheet", List.of("B1:H21"), "guardians"),

    // Bots
    BOTS("Master Sheet", List.of("C1:I27"), "bots"),

    // Modules
    CANNON_SUB_STATS("Substats", List.of("B5:H22"), "modules"),
    ARMOR_SUB_STATS("Substats", List.of("B24:H41"), "modules"),
    GENERATOR_SUB_STATS("Substats", List.of("J5:P18"), "modules"),
    CORE_SUB_STATS("Substats", List.of("J24:P50"), "modules"),
    RARITY_CHANCE("Substats", List.of("B50:H50"), "modules"),

    // Cards
    CARDS("Master Sheet", List.of("B3:D33"), "cards"),
    CARD_PRESET_FARMING("Card Preset", List.of("D5:D32"), "cards"),
    CARD_PRESET_TOURNAMENT("Card Preset", List.of("H5:H32"), "cards"),

    // Relic Trackers
    RELICS("Relics", List.of("M2:N42", "P2:P42"), "relics"),

    // Ultimate Weapons
    CHAIN_LIGHTNING("UW Cost Calculator v3", List.of("B7:O12"), "ultimate-weapons"),
    SMART_MISSILES("UW Cost Calculator v3", List.of("B14:O19"), "ultimate-weapons"),
    DEATH_WAVE("UW Cost Calculator v3", List.of("B21:O26"), "ultimate-weapons"),
    CHRONO_FIELD("UW Cost Calculator v3", List.of("B28:O33"), "ultimate-weapons"),
    INNER_LAND_MINES("UW Cost Calculator v3", List.of("B35:O40"), "ultimate-weapons"),
    GOLDEN_TOWER("UW Cost Calculator v3", List.of("B42:O47"), "ultimate-weapons"),
    POISON_SWAMP("UW Cost Calculator v3", List.of("B49:O54"), "ultimate-weapons"),
    BLACK_HOLE("UW Cost Calculator v3", List.of("B56:O61"), "ultimate-weapons"),
    SPOTLIGHT("UW Cost Calculator v3", List.of("B63:O68"), "ultimate-weapons");

    private final String sheetName;
    private final List<String> ranges;
    private final String sheetKey;

    TowerTrackerRanges(String sheetName, List<String> ranges, String sheetKey) {
        this.sheetName = sheetName;
        this.ranges = ranges;
        this.sheetKey = sheetKey;
    }

    @Override
    public String sheetName() { return sheetName; }

    @Override
    public String sheetId() { return sheetKey; }

    @Override
    public List<String> ranges() { return ranges; }
}
