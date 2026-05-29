package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

public class DeathWave extends AbstractUltimateWeapon {

    public DeathWave(List<ValueRange> valueRanges) {
        super(valueRanges);
    }

    @Override public String statOneLabel()   { return "Damage"; }
    @Override public String statTwoLabel()   { return "Quantity"; }
    @Override public String statThreeLabel() { return "Cooldown"; }

    public Number damage()    { return statOne(); }
    public Number quantity()  { return statTwo(); }
    public Number cooldown()  { return statThree(); }
    public Number killWall()  { return uwPlusStat(); }

    @Override
    public String toString() {
        return "DeathWave{" +
                "name='" + name() + '\'' +
                ", locked=" + locked() +
                ", uwPlusKillWallLocked=" + uwPlusLocked() +
                ", damage=" + damage() +
                ", quantity=" + quantity() +
                ", cooldown=" + cooldown() +
                ", killWall=" + killWall() +
                ", stonesInvestedDamage=" + stonesInvestedOne() +
                ", stonesInvestedQuantity=" + stonesInvestedTwo() +
                ", stonesInvestedCooldown=" + stonesInvestedThree() +
                ", stonesInvestedKillWall=" + stonesInvestedUwPlus() +
                ", stonesRequiredDamage=" + stonesRequiredOne() +
                ", stonesRequiredQuantity=" + stonesRequiredTwo() +
                ", stonesRequiredCooldown=" + stonesRequiredThree() +
                ", stonesRequiredKillWall=" + stonesRequiredUwPlus() +
                '}';
    }
}
