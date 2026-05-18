package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record Records(
        TowerNumber highestCoinsPerMinute,
        int largestWaveSkip,
        TowerNumber mostCoinsFromWaveSkip,
        int mostCellsFromWaveSkip,
        int largestSmartMissileStack,
        int largestGoldenCombo,
        TowerNumber mostCoinsFromGoldenCombo,
        TowerNumber largestInnerLandmineCharge) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.RECORDS;
    }
}
