package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pphi.tower.model.TowerNumber;

public record TotalEnemies(
        @JsonProperty("totalEnemies")    long totalEnemies,
        @JsonProperty("basic")           long basic,
        @JsonProperty("fast")            long fast,
        @JsonProperty("tank")            long tank,
        @JsonProperty("ranged")          long ranged,
        @JsonProperty("boss")            long boss,
        @JsonProperty("protector")       long protector,
        @JsonProperty("vampires")        long vampires,
        @JsonProperty("rays")            long rays,
        @JsonProperty("scatters")        long scatters,
        @JsonProperty("saboteur")        long saboteur,
        @JsonProperty("commander")       long commander,
        @JsonProperty("overcharge")      long overcharge,
        @JsonProperty("summonedEnemies") TowerNumber summonedEnemies) implements Section {

    @JsonCreator
    public TotalEnemies { }

    @Override
    public SectionHeader sectionHeader() { return SectionHeader.TOTAL_ENEMIES; }
}
