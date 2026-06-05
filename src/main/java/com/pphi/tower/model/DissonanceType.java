package com.pphi.tower.model;

public enum DissonanceType {

    ATTACK(5),
    DEFENSE(5),
    UTILITY(3),
    ULTIMATED_WEAPONS(5);

    private final int multiplier;

    DissonanceType(int multiplier) {
        this.multiplier = multiplier;
    }

    public int multiplier() {
        return multiplier;
    }
}
