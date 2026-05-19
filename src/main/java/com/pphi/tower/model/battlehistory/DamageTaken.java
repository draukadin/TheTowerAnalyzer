package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record DamageTaken(
        @JsonProperty("tower") TowerNumber tower,
        @JsonProperty("wall")  TowerNumber wall) implements Section {

    @JsonCreator
    public DamageTaken { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.DAMAGE_TAKEN; }
}
