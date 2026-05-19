package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record Currencies(
        @JsonProperty("cellsEarned")        TowerNumber cellsEarned,
        @JsonProperty("gems")               long gems,
        @JsonProperty("adGems")             long adGems,
        @JsonProperty("gemBlockTapped")     long gemBlockTapped,
        @JsonProperty("fetchGems")          long fetchGems,
        @JsonProperty("medals")             long medals,
        @JsonProperty("reRollShardsEarned") TowerNumber reRollShardsEarned,
        @JsonProperty("reRollShardsFetched") TowerNumber reRollShardsFetched,
        @JsonProperty("cannonShards")       long cannonShards,
        @JsonProperty("armorShards")        long armorShards,
        @JsonProperty("generatorShards")    long generatorShards,
        @JsonProperty("coreShards")         long coreShards,
        @JsonProperty("commonModules")      long commonModules,
        @JsonProperty("rareModules")        long rareModules) implements Section {

    @JsonCreator
    public Currencies { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.CURRENCIES; }
}
