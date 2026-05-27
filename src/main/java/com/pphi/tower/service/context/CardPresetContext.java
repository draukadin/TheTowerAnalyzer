package com.pphi.tower.service.context;

import com.pphi.tower.model.sheets.cards.CardPresetType;

import java.util.List;

public class CardPresetContext implements ChatContext {

    private final CardPresetType preset;
    private final List<String> cardSlots;

    public CardPresetContext(CardPresetType preset, List<String> cardSlots) {
        this.preset = preset;
        this.cardSlots = cardSlots;
    }

    @Override
    public String getLabel() {
        return switch (preset) {
            case FARMING    -> "Farming Card Preset";
            case TOURNAMENT -> "Tournament Card Preset";
        };
    }

    @Override
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Slot | Card Name |\n");
        sb.append("| :--- | :--- |\n");
        for (int i = 0; i < cardSlots.size(); i++) {
            String name = cardSlots.get(i);
            sb.append(String.format("| %d | %s |%n", i + 1, name.isBlank() ? "(Locked)" : name));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getContent();
    }
}
