package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record Cash(
        @JsonProperty("cashEarned")    TowerNumber cashEarned,
        @JsonProperty("goldenTower")   TowerNumber goldenTower,
        @JsonProperty("interestEarned") TowerNumber interestEarned) implements Section {

    @JsonCreator
    public Cash { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.CASH; }
}
