package com.pphi.tower.model.sheets.uw;

public interface UltimateWeapon {

    String name();
    boolean locked();
    boolean uwPlusLocked();
    Number statOne();
    Number statTwo();
    Number statThree();
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
