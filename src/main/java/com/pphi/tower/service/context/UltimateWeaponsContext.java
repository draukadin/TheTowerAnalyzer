package com.pphi.tower.service.context;

import com.pphi.tower.repository.UwRepository.UwPlayerData;
import com.pphi.tower.repository.UwRepository.UwStatPlayerData;

import java.util.List;

public class UltimateWeaponsContext implements ChatContext {

    private final List<UwPlayerData> weapons;

    public UltimateWeaponsContext(List<UwPlayerData> weapons) {
        this.weapons = weapons;
    }

    @Override
    public String getLabel() {
        return "Ultimate Weapons";
    }

    @Override
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        weapons.forEach(uw -> appendUW(sb, uw));
        return sb.toString();
    }

    private void appendUW(StringBuilder sb, UwPlayerData uw) {
        if (!uw.unlocked()) {
            sb.append(String.format("### %s *(LOCKED)*%n%n", uw.name()));
            return;
        }

        sb.append(String.format("### %s%n%n", uw.name()));
        sb.append("| Stat | Level | Value | Stones Invested | Stones to Next | Stones to Max |\n");
        sb.append("| :--- | :--- | :--- | ---: | ---: | ---: |\n");

        for (UwStatPlayerData stat : uw.stats()) {
            if ("UW_PLUS".equals(stat.statKey())) {
                if (!uw.uwPlusUnlocked()) {
                    sb.append(String.format("| %s (%s) | — | LOCKED | — | — | — |%n",
                            uw.uwPlusName(), stat.label()));
                } else {
                    appendStatRow(sb, stat.label() + " (UW+)", stat);
                }
            } else {
                appendStatRow(sb, stat.label(), stat);
            }
        }
        sb.append("\n");
    }

    private void appendStatRow(StringBuilder sb, String label, UwStatPlayerData stat) {
        String next = stat.stonesToNext() != null
                ? String.valueOf(stat.stonesToNext())
                : "Max";
        sb.append(String.format("| %s | %d / %d | %s | %d | %s | %d |%n",
                label,
                stat.currentLevel(), stat.maxLevel(),
                stat.currentValue(),
                stat.stonesInvested(),
                next,
                stat.stonesToMax()));
    }

    @Override
    public String toString() {
        return getContent();
    }
}
