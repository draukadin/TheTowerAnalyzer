package com.pphi.tower.model.battlehistory;

public record EnemiesDestroyedBy(
        long projectiles,
        long thorns,
        long landMines,
        long orbs,
        long chainLightning,
        long smartMissiles,
        long innerLandMines,
        long poisonSwamp,
        long deathRay,
        long blackHole,
        long flameBot,
        long other) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.ENEMIES_DESTROYED_BY;
    }
}
