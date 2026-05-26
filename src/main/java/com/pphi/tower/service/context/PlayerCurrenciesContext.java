package com.pphi.tower.service.context;

import com.pphi.tower.model.sheets.Currencies;

public class PlayerCurrenciesContext implements ChatContext {

    private final Currencies currencies;

    public PlayerCurrenciesContext(Currencies currencies) {
        this.currencies = currencies;
    }

    @Override
    public String getLabel() {
        return "Player Currencies";
    }

    @Override
    public String getContent() {
        return "Coins: "               + currencies.coins()              + "\n" +
               "Gems: "                + currencies.gems()               + "\n" +
               "Stones: "              + currencies.stones()             + "\n" +
               "Medals: "              + currencies.medals()             + "\n" +
               "Elite Cells: "         + currencies.eliteCells()         + "\n" +
               "Keys: "                + currencies.keys()               + "\n" +
               "Tokens: "              + currencies.tokens()             + "\n" +
               "Bits: "                + currencies.bits()               + "\n" +
               "Tournament Tickets: "  + currencies.tournamentTickets()  + "\n" +
               "Module Tickets: "      + currencies.moduleTickets()      + "\n" +
               "Cannon Shards: "       + currencies.cannonShards()       + "\n" +
               "Armor Shards: "        + currencies.armorShards()        + "\n" +
               "Generator Shards: "    + currencies.generatorShards()    + "\n" +
               "Core Shards: "         + currencies.coreShards()         + "\n" +
               "Reroll Shards: "       + currencies.reRollShards()       + "\n";
    }
}
