package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record DamageTaken(
        TowerNumber tower,
        TowerNumber wall) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.DAMAGE_TAKEN;
    }
}
