package com.pphi.tower.model.battlediagnostics;

import java.util.List;

/**
 * The complete output of one diagnostic pass.
 *
 * @param primaryFailure      Most likely cause of run termination.
 * @param confidence          Strength of evidence for the primary diagnosis.
 * @param explanation         Human-readable narrative of the primary finding.
 * @param swarmKillShare      Fraction of kills from Orbs + Chain Lightning [0–1].
 * @param heavyKillShare      Fraction of kills from Thorns + Land Mines [0–1].
 * @param lifeStealRaw        Absolute life steal value (respects TowerNumber magnitude).
 * @param totalDamageTakenRaw Absolute tower + wall damage taken.
 * @param blockEfficiency     Fraction of gross incoming damage successfully blocked [0–1].
 * @param vampireDensity      Fraction of total enemies that were Vampires [0–1].
 * @param rangedDensity       Fraction of total enemies that were Ranged [0–1].
 * @param observations        Secondary findings; never null, may be empty.
 */
public record DiagnosisResult(
        FailureType primaryFailure,
        Confidence confidence,
        String explanation,
        double swarmKillShare,
        double heavyKillShare,
        double lifeStealRaw,
        double totalDamageTakenRaw,
        double blockEfficiency,
        double vampireDensity,
        double rangedDensity,
        List<Observation> observations) {

    public boolean isHighConfidence() { return confidence == Confidence.HIGH; }

    public String swarmKillShareFormatted()  { return pct(swarmKillShare); }
    public String heavyKillShareFormatted()  { return pct(heavyKillShare); }
    public String blockEfficiencyFormatted() { return pct(blockEfficiency); }
    public String vampireDensityFormatted()  { return pct(vampireDensity); }
    public String rangedDensityFormatted()   { return pct(rangedDensity); }

    private static String pct(double v) { return String.format("%.2f %%", v * 100.0); }
}
