package com.pphi.tower.model.sheets;

import java.util.List;

public enum TowerTrackerRanges implements GoogleSheet {

    //Workshops Tracker
    WORKSHOP("Master Sheet", List.of("C1:C50", "D1:D50", "N1:N50"), "1cbP7Uo_b9GZy556LDaPTQNRfGv906CxiGPik0O3PLe0"),
    ENHANCEMENTS("Master Sheet", List.of("P1:P20", "R1:R20", "W1:W20", "X1:x20"), "1cbP7Uo_b9GZy556LDaPTQNRfGv906CxiGPik0O3PLe0"),

    // Player and Stuff Trackers
    TIER_WAVE("Master Sheet", List.of("E1:N23"), "1c7yvzD7_YCEy9PLX3yPxxw164Dx49xBh51v-aSyhD6A"),
    VERSION("TowerVersionTracking", List.of("B:B", "C:C", "D:D"), "1c7yvzD7_YCEy9PLX3yPxxw164Dx49xBh51v-aSyhD6A"),

    // Laboratory Trackers
    MAIN_LABS("Master Sheet", List.of("B1:E22"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    ATTACK_LABS("Master Sheet", List.of("G1:J10"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    DEFENSE_LABS("Master Sheet", List.of("L1:O17"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    UTILITY_LABS("Master Sheet", List.of("Q1:T13"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    ULTIMATE_WEAPON_LABS("Master Sheet", List.of("V1:Y40"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    CARD_LABS("Master Sheet", List.of("AA1:AD41"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    PERK_LABS("Master Sheet", List.of("AF1:AI10"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    BOT_LABS("Master Sheet", List.of("AK1:AN11"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    ENEMY_LABS("Master Sheet", List.of("AO1:AS22"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    MODULE_LABS("Master Sheet", List.of("AT1:AX21"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),
    BATTLE_CONDITION_LABS("Master Sheet", List.of("AZ1:BC21"), "1B0EuK3Qe8Wo5xrW2bWL4l5IjdiuQLcxDnR-eZ8NrEc4"),

    //Guardians
    GUARDIANS("Master Sheet", List.of("B1:H21"), "1G2NtLP__qBXGBhySydsAfCqUy-JG8pUyl8yRP3w7kRI"),

    // Bots
    BOTS("Master Sheet", List.of("C1:I27"), "1O7gLRW-oUm_tBHM2Ja_c9Va0PySKp1_UDuOUd3npsQ8"),

    // Modules
    CANNON_SUB_STATS("Substats", List.of("B5:H22"), "1JROY3TS01YpH74I6zzmbds573_FKUu3mkfiEAFwgg14"),
    ARMOR_SUB_STATS("Substats", List.of("B24:H41"), "1JROY3TS01YpH74I6zzmbds573_FKUu3mkfiEAFwgg14"),
    GENERATOR_SUB_STATS("Substats", List.of("J5:P18"), "1JROY3TS01YpH74I6zzmbds573_FKUu3mkfiEAFwgg14"),
    CORE_SUB_STATS("Substats", List.of("J24:P50"), "1JROY3TS01YpH74I6zzmbds573_FKUu3mkfiEAFwgg14"),
    RARITY_CHANCE("Substats", List.of("B50:H50"), "1JROY3TS01YpH74I6zzmbds573_FKUu3mkfiEAFwgg14"),

    // Relic Trackers
    RELICS("Relics", List.of("M2:N42", "P2:P42"), "1l4n56pWZtKZ8l3fRiTLjnKBc-tezqbhQCjSkTmTXZX8"),

    // Ultimate Weapons
    CHAIN_LIGHTNING("UW Cost Calculator v3", List.of("B7:O12"), "13GXMn0BB-DKSRVj8jAauzy5BKy4XTagbeOPXhM8RuMs"),
    SMART_MISSILES("UW Cost Calculator v3", List.of("B14:O19"), "13GXMn0BB-DKSRVj8jAauzy5BKy4XTagbeOPXhM8RuMs"),
    DEATH_WAVE("UW Cost Calculator v3", List.of("B21:O26"), "13GXMn0BB-DKSRVj8jAauzy5BKy4XTagbeOPXhM8RuMs"),
    CHRONO_FIELD("UW Cost Calculator v3", List.of("B28:O33"), "13GXMn0BB-DKSRVj8jAauzy5BKy4XTagbeOPXhM8RuMs"),
    INNER_LAND_MINES("UW Cost Calculator v3", List.of("B35:O40"), "13GXMn0BB-DKSRVj8jAauzy5BKy4XTagbeOPXhM8RuMs"),
    GOLDEN_TOWER("UW Cost Calculator v3", List.of("B42:O47"), "13GXMn0BB-DKSRVj8jAauzy5BKy4XTagbeOPXhM8RuMs"),
    POISON_SWAMP("UW Cost Calculator v3", List.of("B49:O54"), "13GXMn0BB-DKSRVj8jAauzy5BKy4XTagbeOPXhM8RuMs"),
    BLACK_HOLE("UW Cost Calculator v3", List.of("B56:O61"), "13GXMn0BB-DKSRVj8jAauzy5BKy4XTagbeOPXhM8RuMs"),
    SPOTLIGHT("UW Cost Calculator v3", List.of("B63:O68"), "13GXMn0BB-DKSRVj8jAauzy5BKy4XTagbeOPXhM8RuMs");

    private final String sheetName;
    private final List<String> ranges;
    private final String sheetId;

    TowerTrackerRanges(String sheetName, List<String> ranges, String sheetId) {
        this.sheetName = sheetName;
        this.ranges = ranges;
        this.sheetId = sheetId;
    }

    @Override
    public String sheetName() { return sheetName; }

    @Override
    public String sheetId() { return sheetId; }

    @Override
    public List<String> ranges() { return ranges; }
}
