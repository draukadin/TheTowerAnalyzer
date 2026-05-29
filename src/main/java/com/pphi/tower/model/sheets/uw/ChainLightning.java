package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;
import java.util.function.Function;

public class ChainLightning extends AbstractUltimateWeapon {

    public ChainLightning(List<ValueRange> valueRanges) {
        super(valueRanges);
    }

    @Override
    protected Function<String, Number> statThreeParser() {
        return Double::parseDouble;
    }

    @Override
    protected Function<String, Number> uwPlusStatParser() {
        return Double::parseDouble;
    }

    @Override public String statOneLabel()   { return "Damage"; }
    @Override public String statTwoLabel()   { return "Quantity"; }
    @Override public String statThreeLabel() { return "Chance"; }

    public Number damage()      { return statOne(); }
    public Number quantity()    { return statTwo(); }
    public Number chance()      { return statThree(); }
    public Number smiteChance() { return uwPlusStat(); }

    @Override
    public String toString() {
        return "ChainLightning{" +
                "name='" + name() + '\'' +
                ", locked=" + locked() +
                ", uwPlusSmiteLocked=" + uwPlusLocked() +
                ", damage=" + damage() +
                ", quantity=" + quantity() +
                ", chance=" + chance() +
                ", smiteChance=" + smiteChance() +
                ", stonesInvestedDamage=" + stonesInvestedOne() +
                ", stonesInvestedQuantity=" + stonesInvestedTwo() +
                ", stonesInvestedChance=" + stonesInvestedThree() +
                ", stonesInvestedSmite=" + stonesInvestedUwPlus() +
                ", stonesRequiredDamage=" + stonesRequiredOne() +
                ", stonesRequiredQuantity=" + stonesRequiredTwo() +
                ", stonesRequiredChance=" + stonesRequiredThree() +
                ", stonesRequiredSmite=" + stonesRequiredUwPlus() +
                '}';
    }
}
