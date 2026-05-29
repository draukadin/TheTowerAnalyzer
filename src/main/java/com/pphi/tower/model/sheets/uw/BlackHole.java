package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;
import java.util.function.Function;

public class BlackHole extends AbstractUltimateWeapon {

    public BlackHole(List<ValueRange> valueRanges) {
        super(valueRanges);
    }

    @Override
    protected Function<String, Number> uwPlusStatParser() {
        return Double::parseDouble;
    }

    @Override public String statOneLabel()   { return "Size"; }
    @Override public String statTwoLabel()   { return "Duration"; }
    @Override public String statThreeLabel() { return "Cooldown"; }

    public Number size()      { return statOne(); }
    public Number duration()  { return statTwo(); }
    public Number cooldown()  { return statThree(); }
    public Number consume()   { return uwPlusStat(); }

    @Override
    public String toString() {
        return "BlackHole{" +
                "name='" + name() + '\'' +
                ", locked=" + locked() +
                ", uwPlusConsumeLocked=" + uwPlusLocked() +
                ", size=" + size() +
                ", duration=" + duration() +
                ", cooldown=" + cooldown() +
                ", consume=" + consume() +
                ", stonesInvestedSize=" + stonesInvestedOne() +
                ", stonesInvestedDuration=" + stonesInvestedTwo() +
                ", stonesInvestedCooldown=" + stonesInvestedThree() +
                ", stonesInvestedConsume=" + stonesInvestedUwPlus() +
                ", stonesRequiredSize=" + stonesRequiredOne() +
                ", stonesRequiredDuration=" + stonesRequiredTwo() +
                ", stonesRequiredCooldown=" + stonesRequiredThree() +
                ", stonesRequiredConsume=" + stonesRequiredUwPlus() +
                '}';
    }
}
