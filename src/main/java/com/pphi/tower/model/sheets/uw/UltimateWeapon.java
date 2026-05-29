package com.pphi.tower.model.sheets.uw;

public interface UltimateWeapon {

    String name();
    boolean locked();
    boolean uwPlusLocked();
    Number statOne();
    Number statTwo();
    Number statThree();
    default String statOneLabel()   { return "Stat 1"; }
    default String statTwoLabel()   { return "Stat 2"; }
    default String statThreeLabel() { return "Stat 3"; }
    Number uwPlusStat();
    int stonesInvestedOne();
    int stonesInvestedTwo();
    int stonesInvestedThree();
    int stonesInvestedUwPlus();
    int stonesRequiredOne();
    int stonesRequiredTwo();
    int stonesRequiredThree();
    int stonesRequiredUwPlus();
}
