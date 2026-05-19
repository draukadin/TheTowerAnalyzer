package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record DamageBlocked(
        @JsonProperty("defensePercent")         TowerNumber defensePercent,
        @JsonProperty("defenseAbsolute")        TowerNumber defenseAbsolute,
        @JsonProperty("chronoField")            TowerNumber chronoField,
        @JsonProperty("chainThunder")           TowerNumber chainThunder,
        @JsonProperty("flameBot")               TowerNumber flameBot,
        @JsonProperty("primordialCollapse")     TowerNumber primordialCollapse,
        @JsonProperty("negativeMassProjector")  TowerNumber negativeMassProjector) implements Section {

    @JsonCreator
    public DamageBlocked { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.DAMAGE_BLOCKED; }
}
