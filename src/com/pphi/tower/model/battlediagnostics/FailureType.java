package com.pphi.tower.model.battlediagnostics;

/**
 * All recognised failure modes, ordered from most-specific / catastrophic to most ambiguous.
 */
public enum FailureType {

    // --- Specific / HIGH-confidence band ---

    VAMPIRE_DRAIN_LOCK(
            "Vampire Drain Lock",
            "A Vampire neutralised the Life Steal recovery engine. With regeneration suppressed "
                    + "to a fraction of its healthy baseline the tower's effective health decayed until "
                    + "collapse was inevitable."),

    RANGED_SLIP_THROUGH(
            "Ranged Slip-Through",
            "A Ranged enemy found a momentary gap in knockback coverage and locked into its "
                    + "firing animation before projectiles could reset it. This is a targeting queue "
                    + "bottleneck — not sustained chip damage — typically caused by projectile "
                    + "attention being split across a dense field of high-health or Protector-shielded "
                    + "targets, leaving insufficient knockback cadence on the Ranged unit."),

    BOSS_PRESSURE_COLLAPSE(
            "Boss / Protector Pressure",
            "An unusually high density of Bosses or Protectors overwhelmed the single-target "
                    + "defensive layer. Sustained elite-tier punishment eroded the tower faster than "
                    + "regen and blocks could compensate."),

    // --- Mid-confidence band ---

    CROWD_CONTROL_BREACH(
            "Crowd Control Breach",
            "Screen-clearing tools (Orbs / Chain Lightning) did not keep pace with swarm volume. "
                    + "Normal enemies body-blocked projectiles and progressively overwhelmed the perimeter."),

    ELITE_OVERWHELM(
            "Elite / Heavy Overwhelm",
            "Swarm clearance was adequate, but high-density Heavies and Elites repeatedly "
                    + "punished the wall or tower with concentrated damage the AOE layer could not "
                    + "address fast enough."),

    ORB_LAYER_COLLAPSE(
            "Orb Layer Collapse",
            "Orb kill contribution fell well below healthy norms. Even if Chain Lightning "
                    + "partially compensated, losing the primary AOE anchor created dangerous coverage "
                    + "gaps."),

    DEFENCE_SATURATION(
            "Defence Saturation",
            "Incoming damage consistently outpaced the blocking layer (ChronoField, Chain "
                    + "Thunder, FlameBot, etc.). The tower absorbed more raw damage than its mitigation "
                    + "stack was built to handle."),

    // --- Low-confidence / marginal band ---

    INTERCEPT_GAP(
            "Intercept Layer Gap",
            "Smart Missiles contributed very little to the kill share. Enemies that should "
                    + "have been eliminated at range instead closed to short range before dying, "
                    + "increasing sustained pressure on the core."),

    DEATH_RAY_UNDERPERFORMANCE(
            "Death Ray Underperformance",
            "A Death Ray was active but contributed a negligible share of kills, likely due "
                    + "to poor targeting priority or firing into already-dying targets."),

    DEATH_WAVE_DESYNC(
            "Death Wave Desync",
            "Death Wave fired and generated bonus health throughout the run but contributed "
                    + "zero (or near-zero) kills. The burst window is not aligning with enemy clusters "
                    + "— the wave is firing into empty lanes or already-cleared screens. This wastes "
                    + "the primary burst-damage and Elite-softening tool at the exact moments it is "
                    + "most needed."),

    UNKNOWN_DUE_TO_VARIANCE(
            "Undetermined — Within Normal Variance",
            "All measured indicators fall within expected ranges. The cause of death likely "
                    + "requires comparison against a multi-run historical baseline to isolate.");

    private final String displayName;
    private final String description;

    FailureType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName()  { return displayName; }
    public String getDescription()  { return description; }
}
