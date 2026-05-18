package com.pphi.tower.model.battlediagnostics;

/**
 * A single secondary observation. Always collected in full regardless of primary outcome so
 * callers can render a complete multi-signal report.
 *
 * @param label  Short heading (e.g. "Inner Mine Penetration").
 * @param detail Sentence-form finding with embedded numbers.
 */
public record Observation(String label, String detail) { }
