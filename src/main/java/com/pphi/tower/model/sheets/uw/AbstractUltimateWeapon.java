package com.pphi.tower.model.sheets.uw;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.util.UltimateWeaponUtils;

import java.util.List;
import java.util.function.Function;

public abstract class AbstractUltimateWeapon implements UltimateWeapon {

    private String name;
    private boolean locked;
    private boolean uwPlusLocked;
    private Number statOne;
    private Number statTwo;
    private Number statThree;
    private Number uwPlusStat;
    private int stonesInvestedOne;
    private int stonesInvestedTwo;
    private int stonesInvestedThree;
    private int stonesInvestedUwPlus;
    private int stonesRequiredOne;
    private int stonesRequiredTwo;
    private int stonesRequiredThree;
    private int stonesRequiredUwPlus;

    protected Function<String, Number> statOneParser()    { return Double::parseDouble; }
    protected Function<String, Number> statTwoParser()    { return Double::parseDouble; }
    protected Function<String, Number> statThreeParser()  { return Double::parseDouble; }
    protected Function<String, Number> uwPlusStatParser() { return Double::parseDouble; }

    protected AbstractUltimateWeapon(List<ValueRange> valueRanges) {
        try {
            this.name                = UltimateWeaponUtils.getName(valueRanges);
            this.locked              = UltimateWeaponUtils.isLocked(valueRanges);
            this.statOne             = UltimateWeaponUtils.getStatOne(valueRanges, statOneParser());
            this.statTwo             = UltimateWeaponUtils.getStatTwo(valueRanges, statTwoParser());
            this.statThree           = UltimateWeaponUtils.getStatThree(valueRanges, statThreeParser());
            this.uwPlusLocked        = UltimateWeaponUtils.isUwPlusLocked(valueRanges);
            this.uwPlusStat          = UltimateWeaponUtils.uwPlusStat(valueRanges, uwPlusStatParser());
            this.stonesInvestedOne   = UltimateWeaponUtils.stonedInvestedOne(valueRanges);
            this.stonesRequiredOne   = UltimateWeaponUtils.stonedRequiredOne(valueRanges);
            this.stonesInvestedTwo   = UltimateWeaponUtils.stonedInvestedTwo(valueRanges);
            this.stonesRequiredTwo   = UltimateWeaponUtils.stonedRequiredTwo(valueRanges);
            this.stonesInvestedThree = UltimateWeaponUtils.stonedInvestedThree(valueRanges);
            this.stonesRequiredThree = UltimateWeaponUtils.stonedRequiredThree(valueRanges);
            this.stonesInvestedUwPlus = UltimateWeaponUtils.stonedInvestedUwPlus(valueRanges);
            this.stonesRequiredUwPlus = UltimateWeaponUtils.stonedRequiredUwPlus(valueRanges);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to parse " + getClass().getSimpleName() + " from sheet data: " + e.getMessage(), e);
        }
    }

    @Override public String name()               { return name; }
    @Override public boolean locked()            { return locked; }
    @Override public boolean uwPlusLocked()      { return uwPlusLocked; }
    @Override public Number statOne()            { return statOne; }
    @Override public Number statTwo()            { return statTwo; }
    @Override public Number statThree()          { return statThree; }
    @Override public Number uwPlusStat()         { return uwPlusStat; }
    @Override public int stonesInvestedOne()     { return stonesInvestedOne; }
    @Override public int stonesInvestedTwo()     { return stonesInvestedTwo; }
    @Override public int stonesInvestedThree()   { return stonesInvestedThree; }
    @Override public int stonesInvestedUwPlus()  { return stonesInvestedUwPlus; }
    @Override public int stonesRequiredOne()     { return stonesRequiredOne; }
    @Override public int stonesRequiredTwo()     { return stonesRequiredTwo; }
    @Override public int stonesRequiredThree()   { return stonesRequiredThree; }
    @Override public int stonesRequiredUwPlus()  { return stonesRequiredUwPlus; }
}
