package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

public class SmartMissiles extends AbstractUltimateWeapon {

    public SmartMissiles(List<ValueRange> valueRanges) {
        super(valueRanges);
    }

    public Number damage()     { return statOne(); }
    public Number quantity()   { return statTwo(); }
    public Number cooldown()   { return statThree(); }
    public Number coverFire()  { return uwPlusStat(); }

    @Override
    public String toString() {
        return "SmartMissiles{" +
                "name='" + name() + '\'' +
                ", locked=" + locked() +
                ", uwPlusCoverFireLocked=" + uwPlusLocked() +
                ", damage=" + damage() +
                ", quantity=" + quantity() +
                ", cooldown=" + cooldown() +
                ", coverFire=" + coverFire() +
                ", stonesInvestedDamage=" + stonesInvestedOne() +
                ", stonesInvestedQuantity=" + stonesInvestedTwo() +
                ", stonesInvestedCooldown=" + stonesInvestedThree() +
                ", stonesInvestedCoverFire=" + stonesInvestedUwPlus() +
                ", stonesRequiredDamage=" + stonesRequiredOne() +
                ", stonesRequiredQuantity=" + stonesRequiredTwo() +
                ", stonesRequiredCooldown=" + stonesRequiredThree() +
                ", stonesRequiredCoverFire=" + stonesRequiredUwPlus() +
                '}';
    }
}
