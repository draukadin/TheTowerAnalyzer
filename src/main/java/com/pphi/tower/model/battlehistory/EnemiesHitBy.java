package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record EnemiesHitBy(
        @JsonProperty("projectiles")    TowerNumber projectiles,
        @JsonProperty("thorns")         TowerNumber thorns,
        @JsonProperty("orbs")           TowerNumber orbs,
        @JsonProperty("deathRay")       TowerNumber deathRay,
        @JsonProperty("chainLightning") TowerNumber chainLightning,
        @JsonProperty("smartMissiles")  TowerNumber smartMissiles,
        @JsonProperty("innerLandMines") TowerNumber innerLandMines,
        @JsonProperty("poisonSwamp")    TowerNumber poisonSwamp,
        @JsonProperty("deathWave")      TowerNumber deathWave,
        @JsonProperty("blackHole")      TowerNumber blackHole,
        @JsonProperty("chronoField")    TowerNumber chronoField,
        @JsonProperty("landMines")      TowerNumber landMines,
        @JsonProperty("thunderBot")     TowerNumber thunderBot,
        @JsonProperty("flameBot")       TowerNumber flameBot,
        @JsonProperty("attackChip")     TowerNumber attackChip,
        @JsonProperty("orbitalAugment") TowerNumber orbitalAugment) implements Section {

    @JsonCreator
    public EnemiesHitBy { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.ENEMIES_HIT_BY; }
}
