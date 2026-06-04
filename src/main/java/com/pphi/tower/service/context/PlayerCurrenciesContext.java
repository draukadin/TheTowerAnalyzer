package com.pphi.tower.service.context;

import com.pphi.tower.model.sheets.Currencies;

public class PlayerCurrenciesContext implements ChatContext {

    private final Currencies currencies;

    public PlayerCurrenciesContext(Currencies currencies) {
        this.currencies = currencies;
    }

    public Currencies getCurrencies() { return currencies; }

    @Override
    public String getLabel() {
        return "Player Currencies";
    }

    @Override
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Currency | Amount |\n");
        sb.append("| :--- | :--- |\n");
        row(sb, "Coins",               currencies.coins());
        row(sb, "Gems",                currencies.gems());
        row(sb, "Stones",              currencies.stones());
        row(sb, "Medals",              currencies.medals());
        row(sb, "Elite Cells",         currencies.eliteCells());
        row(sb, "Keys",                currencies.keys());
        row(sb, "Tokens",              currencies.tokens());
        row(sb, "Bits",                currencies.bits());
        row(sb, "Tournament Tickets",  currencies.tournamentTickets());
        row(sb, "Module Tickets",      currencies.moduleTickets());
        row(sb, "Cannon Shards",       currencies.cannonShards());
        row(sb, "Armor Shards",        currencies.armorShards());
        row(sb, "Generator Shards",    currencies.generatorShards());
        row(sb, "Core Shards",         currencies.coreShards());
        row(sb, "Reroll Shards",       currencies.reRollShards());
        return sb.toString();
    }

    private void row(StringBuilder sb, String currency, Object amount) {
        sb.append(String.format("| %s | %s |%n", currency, amount));
    }

    @Override
    public String toString() {
        return getContent();
    }
}
