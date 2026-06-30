package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record HealthRegenerated(
        @JsonProperty("lifeSteal")        TowerNumber lifeSteal,
        @JsonProperty("towerHealthRegen") TowerNumber towerHealthRegen,
        @JsonProperty("wallHealthRegen")  TowerNumber wallHealthRegen,
        @JsonProperty("recoveryPackages") TowerNumber recoveryPackages) implements Section {

    @JsonCreator
    public HealthRegenerated { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.HEALTH_REGENERATED; }
}
