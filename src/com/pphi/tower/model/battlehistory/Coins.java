package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

public record Coins(
        TowerNumber coinsEarned,
        TowerNumber coinsPerKill,
        TowerNumber otherCoinBonuses,
        TowerNumber criticalCoin,
        TowerNumber goldenTower,
        TowerNumber goldenCombo,
        TowerNumber deathWave,
        TowerNumber spotlight,
        TowerNumber blackHole,
        TowerNumber orbs,
        TowerNumber goldenBot,
        TowerNumber waveSkip,
        TowerNumber coinsPerWave,
        TowerNumber coinsFetched,
        TowerNumber bountyCoins) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.COINS;
    }
}
