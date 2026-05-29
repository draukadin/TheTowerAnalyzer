package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

public class ChronoField extends AbstractUltimateWeapon {

    public ChronoField(List<ValueRange> valueRanges) {
        super(valueRanges);
    }

    @Override public String statOneLabel()   { return "Duration"; }
    @Override public String statTwoLabel()   { return "Speed Reduction"; }
    @Override public String statThreeLabel() { return "Cooldown"; }

    public Number duration()        { return statOne(); }
    public Number speedReduction()  { return statTwo(); }
    public Number cooldown()        { return statThree(); }
    public Number chronoLoop()      { return uwPlusStat(); }

    @Override
    public String toString() {
        return "ChronoField{" +
                "name='" + name() + '\'' +
                ", locked=" + locked() +
                ", uwPlusChronoLoopLocked=" + uwPlusLocked() +
                ", duration=" + duration() +
                ", speedReduction=" + speedReduction() +
                ", cooldown=" + cooldown() +
                ", chronoLoop=" + chronoLoop() +
                ", stonesInvestedDuration=" + stonesInvestedOne() +
                ", stonesInvestedSpeedReduction=" + stonesInvestedTwo() +
                ", stonesInvestedCooldown=" + stonesInvestedThree() +
                ", stonesInvestedChronoLoop=" + stonesInvestedUwPlus() +
                ", stonesRequiredDuration=" + stonesRequiredOne() +
                ", stonesRequiredSpeedReduction=" + stonesRequiredTwo() +
                ", stonesRequiredCooldown=" + stonesRequiredThree() +
                ", stonesRequiredChronoLoop=" + stonesRequiredUwPlus() +
                '}';
    }
}
