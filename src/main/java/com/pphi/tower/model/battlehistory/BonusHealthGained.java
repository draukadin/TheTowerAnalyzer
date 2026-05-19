package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record BonusHealthGained(
        @JsonProperty("fromDeathWave") TowerNumber fromDeathWave) implements Section {

    @JsonCreator
    public BonusHealthGained { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.BONUS_HEALTH_GAINED; }
}
