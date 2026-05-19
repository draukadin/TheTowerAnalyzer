package com.pphi.tower.model.battlediagnostics;

public enum Confidence {
    /** Multiple independent signals converge on the same cause. */
    HIGH,
    /** A primary threshold was crossed but corroborating evidence is partial. */
    MEDIUM,
    /** A marginal threshold crossing; baseline comparison is recommended. */
    LOW
}
