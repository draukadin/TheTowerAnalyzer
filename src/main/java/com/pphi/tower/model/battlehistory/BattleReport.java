package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record BattleReport(
        @JsonProperty("towerEra")        String towerEra,
        @JsonProperty("battleReportDate") java.time.Instant battleReportDate,
        @JsonProperty("gameTime")        java.time.Duration gameTime,
        @JsonProperty("realTime")        java.time.Duration realTime,
        @JsonProperty("tier")            int tier,
        @JsonProperty("wave")            int wave,
        @JsonProperty("killedBy")        String killedBy,
        @JsonProperty("coinsEarned")     TowerNumber coinsEarned,
        @JsonProperty("coinsPerHour")    TowerNumber coinsPerHour,
        @JsonProperty("cellsEarned")     TowerNumber cellsEarned,
        @JsonProperty("cellsPerHour")    TowerNumber cellsPerHour) implements Section {

    @JsonCreator
    public BattleReport { }

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.BATTLE_REPORT;
    }
}
