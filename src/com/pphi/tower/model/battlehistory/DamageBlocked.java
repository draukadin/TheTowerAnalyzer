package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record DamageBlocked(
        TowerNumber defensePercent,
        TowerNumber defenseAbsolute,
        TowerNumber chronoField,
        TowerNumber chainThunder,
        TowerNumber flameBot,
        TowerNumber primordialCollapse,
        TowerNumber negativeMassProjector) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.DAMAGE_BLOCKED;
    }
}
