package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record Currencies(
        TowerNumber cellsEarned,
        long gems,
        long adGems,
        long gemBlockTapped,
        long fetchGems,
        long medals,
        TowerNumber reRollShardsEarned,
        TowerNumber reRollShardsFetched,
        long cannonShards,
        long armorShards,
        long generatorShards,
        long coreShards,
        long commonModules,
        long rareModules) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.CURRENCIES;
    }
}
