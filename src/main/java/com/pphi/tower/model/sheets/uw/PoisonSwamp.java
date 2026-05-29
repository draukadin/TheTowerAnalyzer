package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;

import java.util.List;

public class PoisonSwamp extends AbstractUltimateWeapon {

    public PoisonSwamp(List<ValueRange> valueRanges) {
        super(valueRanges);
    }

    @Override public String statOneLabel()   { return "Damage"; }
    @Override public String statTwoLabel()   { return "Duration"; }
    @Override public String statThreeLabel() { return "Cooldown"; }

    public Number damage()      { return statOne(); }
    public Number duration()    { return statTwo(); }
    public Number cooldown()    { return statThree(); }
    public Number deathCreep()  { return uwPlusStat(); }

    @Override
    public String toString() {
        return "PoisonSwamp{" +
                "name='" + name() + '\'' +
                ", locked=" + locked() +
                ", uwPlusDeathCreepLocked=" + uwPlusLocked() +
                ", damage=" + damage() +
                ", duration=" + duration() +
                ", cooldown=" + cooldown() +
                ", deathCreep=" + deathCreep() +
                ", stonesInvestedDamage=" + stonesInvestedOne() +
                ", stonesInvestedDuration=" + stonesInvestedTwo() +
                ", stonesInvestedCooldown=" + stonesInvestedThree() +
                ", stonesInvestedDeathCreep=" + stonesInvestedUwPlus() +
                ", stonesRequiredDamage=" + stonesRequiredOne() +
                ", stonesRequiredDuration=" + stonesRequiredTwo() +
                ", stonesRequiredCooldown=" + stonesRequiredThree() +
                ", stonesRequiredDeathCreep=" + stonesRequiredUwPlus() +
                '}';
    }
}
