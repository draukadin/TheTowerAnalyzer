package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record EnemiesHitBy(
        TowerNumber projectiles,
        TowerNumber thorns,
        TowerNumber orbs,
        TowerNumber deathRay,
        TowerNumber chainLightning,
        TowerNumber smartMissiles,
        TowerNumber innerLandMines,
        TowerNumber poisonSwamp,
        TowerNumber deathWave,
        TowerNumber blackHole,
        TowerNumber chronoField,
        TowerNumber landMines,
        TowerNumber thunderBot,
        TowerNumber flameBot,
        TowerNumber attackChip,
        TowerNumber orbitalAugment) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.ENEMIES_HIT_BY;
    }
}
