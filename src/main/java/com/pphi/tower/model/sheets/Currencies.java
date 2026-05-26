package com.pphi.tower.model.sheets;

import com.pphi.tower.model.TowerNumber;

public record Currencies(
        TowerNumber coins,
        int gems,
        int stones,
        int medals,
        TowerNumber eliteCells,
        int keys,
        int tokens,
        int bits,
        int tournamentTickets,
        int moduleTickets,
        int cannonShards,
        int armorShards,
        int generatorShards,
        int coreShards,
        int reRollShards) {
}
