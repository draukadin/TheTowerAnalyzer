package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Utility(
        @JsonProperty("recoveryPackages")        int recoveryPackages,
        @JsonProperty("freeAttackUpgrades")      int freeAttackUpgrades,
        @JsonProperty("freeDefenseUpgrades")     int freeDefenseUpgrades,
        @JsonProperty("freeUtilityUpgrades")     int freeUtilityUpgrades,
        @JsonProperty("enemyAttackLevelSkipped") int enemyAttackLevelSkipped,
        @JsonProperty("enemyHealthLevelSkipped") int enemyHealthLevelSkipped) implements Section {

    @JsonCreator
    public Utility { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.UTILITY; }
}
