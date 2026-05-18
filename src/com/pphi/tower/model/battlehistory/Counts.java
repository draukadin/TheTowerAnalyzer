package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record Counts(
        TowerNumber projectileCount,
        int landMinesSpawned,
        long thunderBotStuns,
        int wavesSkipped,
        int deathDefy,
        int hitsAbsorbedByEnergyShield,
        int nuke,
        int secondWind,
        int demonMode) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.COUNTS;
    }
}
