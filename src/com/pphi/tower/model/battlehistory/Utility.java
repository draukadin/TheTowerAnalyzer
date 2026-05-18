package com.pphi.tower.model.battlehistory;

public record Utility(
        int recoveryPackages,
        int freeAttackUpgrades,
        int freeDefenseUpgrades,
        int freeUtilityUpgrades,
        int enemyAttackLevelSkipped,
        int enemyHealthLevelSkipped) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.UTILITY;
    }
}
