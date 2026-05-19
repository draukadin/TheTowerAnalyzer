package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record Coins(
        @JsonProperty("coinsEarned")      TowerNumber coinsEarned,
        @JsonProperty("coinsPerKill")     TowerNumber coinsPerKill,
        @JsonProperty("otherCoinBonuses") TowerNumber otherCoinBonuses,
        @JsonProperty("criticalCoin")     TowerNumber criticalCoin,
        @JsonProperty("goldenTower")      TowerNumber goldenTower,
        @JsonProperty("goldenCombo")      TowerNumber goldenCombo,
        @JsonProperty("deathWave")        TowerNumber deathWave,
        @JsonProperty("spotlight")        TowerNumber spotlight,
        @JsonProperty("blackHole")        TowerNumber blackHole,
        @JsonProperty("orbs")             TowerNumber orbs,
        @JsonProperty("goldenBot")        TowerNumber goldenBot,
        @JsonProperty("waveSkip")         TowerNumber waveSkip,
        @JsonProperty("coinsPerWave")     TowerNumber coinsPerWave,
        @JsonProperty("coinsFetched")     TowerNumber coinsFetched,
        @JsonProperty("bountyCoins")      TowerNumber bountyCoins) implements Section {

    @JsonCreator
    public Coins { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.COINS; }
}
