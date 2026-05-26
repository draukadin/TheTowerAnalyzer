package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;
import java.util.function.Function;

public class GoldenTower extends AbstractUltimateWeapon {

    public GoldenTower(List<ValueRange> valueRanges) {
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

    public Number bonus()        { return statOne(); }
    public Number duration()     { return statTwo(); }
    public Number cooldown()     { return statThree(); }
    public Number goldenCombo()  { return uwPlusStat(); }

    @Override
    public String toString() {
        return "GoldenTower{" +
                "name='" + name() + '\'' +
                ", locked=" + locked() +
                ", uwPlusGoldenComboLocked=" + uwPlusLocked() +
                ", bonus=" + bonus() +
                ", duration=" + duration() +
                ", cooldown=" + cooldown() +
                ", goldenCombo=" + goldenCombo() +
                ", stonesInvestedBonus=" + stonesInvestedOne() +
                ", stonesInvestedDuration=" + stonesInvestedTwo() +
                ", stonesInvestedCooldown=" + stonesInvestedThree() +
                ", stonesInvestedGoldenCombo=" + stonesInvestedUwPlus() +
                ", stonesRequiredBonus=" + stonesRequiredOne() +
                ", stonesRequiredDuration=" + stonesRequiredTwo() +
                ", stonesRequiredCooldown=" + stonesRequiredThree() +
                ", stonesRequiredGoldenCombo=" + stonesRequiredUwPlus() +
                '}';
    }
}
