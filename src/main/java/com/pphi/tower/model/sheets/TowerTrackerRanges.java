package com.pphi.tower.model.sheets;

import java.util.List;

public enum TowerTrackerRanges implements GoogleSheet {

    //Workshops Tracker
    WORKSHOP("Master Sheet", List.of("C1:C50", "D1:D50", "N1:N50"), "workshop-tracker"),
    ENHANCEMENTS("Master Sheet", List.of("P1:P20", "R1:R20", "W1:W20", "X1:X20"), "workshop-tracker"),

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
    LAB_SLOT_ONE_PLANNING("Lab Planner", List.of("C2:G15"), "laboratory-tracker"),
    LAB_SLOT_TWO_PLANNING("Lab Planner", List.of("K2:O15"), "laboratory-tracker"),
    LAB_SLOT_THREE_PLANNING("Lab Planner", List.of("S2:W15"), "laboratory-tracker"),
    LAB_SLOT_FOUR_PLANNING("Lab Planner", List.of("C28:G41"), "laboratory-tracker"),
    LAB_SLOT_FIVE_PLANNING("Lab Planner", List.of("K28:O41"), "laboratory-tracker"),

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

    // Lab Tier List
    LAB_TIER_LIST("The Tier List v28.2", List.of("B:F"), "lab-tier-list");

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
