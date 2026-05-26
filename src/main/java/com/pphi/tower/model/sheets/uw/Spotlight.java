package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;
import java.util.function.Function;

public class Spotlight extends AbstractUltimateWeapon {

    public Spotlight(List<ValueRange> valueRanges) {
        super(valueRanges);
    }

    @Override
    protected Function<String, Number> statOneParser() {
        return Double::parseDouble;
    }

    @Override
    protected Function<String, Number> uwPlusStatParser() {
        return Double::parseDouble;
    }

    public Number bonus()       { return statOne(); }
    public Number angle()       { return statTwo(); }
    public Number count()       { return statThree(); }
    public Number lightRange()  { return uwPlusStat(); }

    @Override
    public String toString() {
        return "Spotlight{" +
                "name='" + name() + '\'' +
                ", locked=" + locked() +
                ", uwPlusLightRangeLocked=" + uwPlusLocked() +
                ", bonus=" + bonus() +
                ", angle=" + angle() +
                ", count=" + count() +
                ", lightRange=" + lightRange() +
                ", stonesInvestedBonus=" + stonesInvestedOne() +
                ", stonesInvestedAngle=" + stonesInvestedTwo() +
                ", stonesInvestedCount=" + stonesInvestedThree() +
                ", stonesInvestedLightRange=" + stonesInvestedUwPlus() +
                ", stonesRequiredBonus=" + stonesRequiredOne() +
                ", stonesRequiredAngle=" + stonesRequiredTwo() +
                ", stonesRequiredCount=" + stonesRequiredThree() +
                ", stonesRequiredLightRange=" + stonesRequiredUwPlus() +
                '}';
    }
}
