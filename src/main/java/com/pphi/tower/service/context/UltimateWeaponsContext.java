package com.pphi.tower.service.context;

import com.pphi.tower.model.sheets.uw.UltimateWeapon;

import java.util.List;

public class UltimateWeaponsContext implements ChatContext {

    private final List<UltimateWeapon> weapons;

    public UltimateWeaponsContext(List<UltimateWeapon> weapons) {
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

    private void appendUW(StringBuilder sb, UltimateWeapon uw) {
        if (uw.locked()) {
            sb.append(String.format("### %s *(LOCKED)*%n%n", uw.name()));
            return;
        }

        sb.append(String.format("### %s%n%n", uw.name()));
        sb.append("| Stat | Value | Stones Invested | Stones Required |\n");
        sb.append("| :--- | :--- | :--- | :--- |\n");
        statRow(sb, "Stat 1", uw.statOne(),   uw.stonesInvestedOne(),   uw.stonesRequiredOne());
        statRow(sb, "Stat 2", uw.statTwo(),   uw.stonesInvestedTwo(),   uw.stonesRequiredTwo());
        statRow(sb, "Stat 3", uw.statThree(), uw.stonesInvestedThree(), uw.stonesRequiredThree());

        if (uw.uwPlusLocked()) {
            sb.append("| UW+ | LOCKED | - | - |\n");
        } else {
            statRow(sb, "UW+", uw.uwPlusStat(), uw.stonesInvestedUwPlus(), uw.stonesRequiredUwPlus());
        }
        sb.append("\n");
    }

    private void statRow(StringBuilder sb, String label, Number value, int invested, int required) {
        sb.append(String.format("| %s | %s | %d | %d |%n", label, value, invested, required));
    }
}
