package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record HealthRegenerated(
        TowerNumber lifeSteal,
        TowerNumber towerHealthRegen,
        TowerNumber wallHealthRegen) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.HEALTH_REGENERATED;
    }
}
