package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record EnemiesDestroyedBy(
        @JsonProperty("projectiles")    long projectiles,
        @JsonProperty("thorns")         long thorns,
        @JsonProperty("landMines")      long landMines,
        @JsonProperty("orbs")           long orbs,
        @JsonProperty("chainLightning") long chainLightning,
        @JsonProperty("smartMissiles")  long smartMissiles,
        @JsonProperty("innerLandMines") long innerLandMines,
        @JsonProperty("poisonSwamp")    long poisonSwamp,
        @JsonProperty("deathRay")       long deathRay,
        @JsonProperty("blackHole")      long blackHole,
        @JsonProperty("flameBot")       long flameBot,
        @JsonProperty("other")          long other) implements Section {

    @JsonCreator
    public EnemiesDestroyedBy { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.ENEMIES_DESTROYED_BY; }
}
