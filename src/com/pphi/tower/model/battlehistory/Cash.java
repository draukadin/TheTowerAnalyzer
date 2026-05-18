package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record Cash(
        TowerNumber cashEarned,
        TowerNumber goldenTower,
        TowerNumber interestEarned) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.CASH;
    }
}
