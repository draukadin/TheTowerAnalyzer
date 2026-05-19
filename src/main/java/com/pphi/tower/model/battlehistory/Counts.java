package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record Counts(
        @JsonProperty("projectileCount")            TowerNumber projectileCount,
        @JsonProperty("landMinesSpawned")           int landMinesSpawned,
        @JsonProperty("thunderBotStuns")            long thunderBotStuns,
        @JsonProperty("wavesSkipped")               int wavesSkipped,
        @JsonProperty("deathDefy")                  int deathDefy,
        @JsonProperty("hitsAbsorbedByEnergyShield") int hitsAbsorbedByEnergyShield,
        @JsonProperty("nuke")                       int nuke,
        @JsonProperty("secondWind")                 int secondWind,
        @JsonProperty("demonMode")                  int demonMode) implements Section {

    @JsonCreator
    public Counts { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.COUNTS; }
}
