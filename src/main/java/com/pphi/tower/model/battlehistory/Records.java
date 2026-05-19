package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record Records(
        @JsonProperty("highestCoinsPerMinute")    TowerNumber highestCoinsPerMinute,
        @JsonProperty("largestWaveSkip")          int largestWaveSkip,
        @JsonProperty("mostCoinsFromWaveSkip")    TowerNumber mostCoinsFromWaveSkip,
        @JsonProperty("mostCellsFromWaveSkip")    int mostCellsFromWaveSkip,
        @JsonProperty("largestSmartMissileStack") int largestSmartMissileStack,
        @JsonProperty("largestGoldenCombo")       int largestGoldenCombo,
        @JsonProperty("mostCoinsFromGoldenCombo") TowerNumber mostCoinsFromGoldenCombo,
        @JsonProperty("largestInnerLandmineCharge") TowerNumber largestInnerLandmineCharge) implements Section {

    @JsonCreator
    public Records { }

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.RECORDS;
    }
}
