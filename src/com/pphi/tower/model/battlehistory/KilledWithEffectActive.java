package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record KilledWithEffectActive(
        long goldenTower,
        TowerNumber deathWave,
        long spotlight,
        long amplifyBot,
        long goldenBot,
        long deathPenalty) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.KILLED_WITH_EFFECT_ACTIVE;
    }
}
