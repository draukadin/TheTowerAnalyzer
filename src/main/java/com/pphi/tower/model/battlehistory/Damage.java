package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record Damage(
        @JsonProperty("damageDealt")    TowerNumber damageDealt,
        @JsonProperty("projectiles")    TowerNumber projectiles,
        @JsonProperty("rendArmor")      TowerNumber rendArmor,
        @JsonProperty("deathRay")       TowerNumber deathRay,
        @JsonProperty("thorns")         TowerNumber thorns,
        @JsonProperty("orbs")           TowerNumber orbs,
        @JsonProperty("landMines")      TowerNumber landMines,
        @JsonProperty("chainLightning") TowerNumber chainLightning,
        @JsonProperty("smartMissiles")  TowerNumber smartMissiles,
        @JsonProperty("innerLandMines") TowerNumber innerLandMines,
        @JsonProperty("poisonSwamp")    TowerNumber poisonSwamp,
        @JsonProperty("deathWave")      TowerNumber deathWave,
        @JsonProperty("blackHole")      TowerNumber blackHole,
        @JsonProperty("flameBot")       TowerNumber flameBot,
        @JsonProperty("attackChip")     TowerNumber attackChip,
        @JsonProperty("electrons")      TowerNumber electrons) implements Section {

    @JsonCreator
    public Damage { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.DAMAGE; }
}
