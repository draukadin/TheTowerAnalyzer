package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;
import java.util.function.Function;

public class InnerLandMines extends AbstractUltimateWeapon {

    public InnerLandMines(List<ValueRange> valueRanges) {
        super(valueRanges);
    }

    @Override
    protected Function<String, Number> uwPlusStatParser() {
        return Double::parseDouble;
    }

    @Override public String statOneLabel()   { return "Damage"; }
    @Override public String statTwoLabel()   { return "Quantity"; }
    @Override public String statThreeLabel() { return "Cooldown"; }

    public Number damage()        { return statOne(); }
    public Number quantity()      { return statTwo(); }
    public Number cooldown()      { return statThree(); }
    public Number chargedMines()  { return uwPlusStat(); }

    @Override
    public String toString() {
        return "InnerLandMines{" +
                "name='" + name() + '\'' +
                ", locked=" + locked() +
                ", uwPlusChargedMinesLocked=" + uwPlusLocked() +
                ", damage=" + damage() +
                ", quantity=" + quantity() +
                ", cooldown=" + cooldown() +
                ", chargedMines=" + chargedMines() +
                ", stonesInvestedDamage=" + stonesInvestedOne() +
                ", stonesInvestedQuantity=" + stonesInvestedTwo() +
                ", stonesInvestedCooldown=" + stonesInvestedThree() +
                ", stonesInvestedChargedMines=" + stonesInvestedUwPlus() +
                ", stonesRequiredDamage=" + stonesRequiredOne() +
                ", stonesRequiredQuantity=" + stonesRequiredTwo() +
                ", stonesRequiredCooldown=" + stonesRequiredThree() +
                ", stonesRequiredChargedMines=" + stonesRequiredUwPlus() +
                '}';
    }
}
