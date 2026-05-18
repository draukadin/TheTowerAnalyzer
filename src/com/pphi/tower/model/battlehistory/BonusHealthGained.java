package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record BonusHealthGained(TowerNumber fromDeathWave) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.BONUS_HEALTH_GAINED;
    }
}
