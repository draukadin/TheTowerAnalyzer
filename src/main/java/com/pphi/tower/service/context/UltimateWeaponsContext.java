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
        sb.append("--- ").append(uw.name()).append(" ---\n");
        if (uw.locked()) {
            sb.append("  Status: LOCKED\n\n");
            return;
        }
        sb.append(String.format("  Stat 1: %-10s (stones invested/required: %d / %d)%n",
                uw.statOne(), uw.stonesInvestedOne(), uw.stonesRequiredOne()));
        sb.append(String.format("  Stat 2: %-10s (stones invested/required: %d / %d)%n",
                uw.statTwo(), uw.stonesInvestedTwo(), uw.stonesRequiredTwo()));
        sb.append(String.format("  Stat 3: %-10s (stones invested/required: %d / %d)%n",
                uw.statThree(), uw.stonesInvestedThree(), uw.stonesRequiredThree()));
        if (!uw.uwPlusLocked()) {
            sb.append(String.format("  UW+:    %-10s (stones invested/required: %d / %d)%n",
                    uw.uwPlusStat(), uw.stonesInvestedUwPlus(), uw.stonesRequiredUwPlus()));
        } else {
            sb.append("  UW+: LOCKED\n");
        }
        sb.append("\n");
    }
}
