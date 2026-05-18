package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record TotalEnemies(
        long totalEnemies,
        long basic,
        long fast,
        long tank,
        long ranged,
        long boss,
        long protector,
        long vampires,
        long rays,
        long scatters,
        long saboteur,
        long commander,
        long overcharge,
        TowerNumber summonedEnemies) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.TOTAL_ENEMIES;
    }
}
