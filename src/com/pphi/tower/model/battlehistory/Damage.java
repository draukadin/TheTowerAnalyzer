package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record Damage(
        TowerNumber damageDealt,
        TowerNumber projectiles,
        TowerNumber rendArmor,
        TowerNumber deathRay,
        TowerNumber thorns,
        TowerNumber orbs,
        TowerNumber landMines,
        TowerNumber chainLightning,
        TowerNumber smartMissiles,
        TowerNumber innerLandMines,
        TowerNumber poisonSwamp,
        TowerNumber deathWave,
        TowerNumber blackHole,
        TowerNumber flameBot,
        TowerNumber attackChip,
        TowerNumber electrons) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.DAMAGE;
    }
}
