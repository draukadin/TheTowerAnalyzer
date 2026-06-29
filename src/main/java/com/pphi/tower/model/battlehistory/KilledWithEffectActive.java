package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record KilledWithEffectActive(
        @JsonProperty("goldenTower")  long goldenTower,
        @JsonProperty("deathWave")    TowerNumber deathWave,
        @JsonProperty("spotlight")    long spotlight,
        @JsonProperty("amplifyBot")   long amplifyBot,
        @JsonProperty("goldenBot")    long goldenBot,
        @JsonProperty("deathPenalty") long deathPenalty,
        @JsonProperty("blackHole")    long blackHole,
        @JsonProperty("orbs")         long orbs) implements Section {

    @JsonCreator
    public KilledWithEffectActive { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.KILLED_WITH_EFFECT_ACTIVE; }
}
