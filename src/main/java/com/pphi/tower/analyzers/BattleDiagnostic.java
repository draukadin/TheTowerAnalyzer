package com.pphi.tower.analyzers;

import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.battlediagnostics.Confidence;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlediagnostics.FailureType;
import com.pphi.tower.model.battlediagnostics.Observation;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.model.battlehistory.BattleReport;
import com.pphi.tower.model.battlehistory.BonusHealthGained;
import com.pphi.tower.model.battlehistory.Cash;
import com.pphi.tower.model.battlehistory.Coins;
import com.pphi.tower.model.battlehistory.Counts;
import com.pphi.tower.model.battlehistory.Damage;
import com.pphi.tower.model.battlehistory.DamageBlocked;
import com.pphi.tower.model.battlehistory.DamageTaken;
import com.pphi.tower.model.battlehistory.EnemiesDestroyedBy;
import com.pphi.tower.model.battlehistory.EnemiesHitBy;
import com.pphi.tower.model.battlehistory.HealthRegenerated;
import com.pphi.tower.model.battlehistory.KilledWithEffectActive;
import com.pphi.tower.model.battlehistory.Records;
import com.pphi.tower.model.battlehistory.Section;
import com.pphi.tower.model.battlehistory.SectionHeader;
import com.pphi.tower.model.battlehistory.TotalEnemies;
import com.pphi.tower.model.battlehistory.Utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BattleDiagnostic {

    // =========================================================================
    // Thresholds — all in one place for easy tuning
    // =========================================================================

    // --- Life Steal / Vampire ---
    /** Raw life steal value (absolute) below which a Vampire drain lock is considered confirmed.
     *  Derived from observed 26.98 T suppressed vs 642 T healthy baseline. */
    private static final double VAMPIRE_LIFESTEAL_CEILING    = 30.0  * 1_000_000_000_000.0; // 30 T
    /** Fraction of total enemies that must be Vampires for HIGH-confidence drain lock. */
    private static final double VAMPIRE_DENSITY_HIGH         = 0.03;

    // --- Kill share thresholds ---
    /** Orbs + Chain Lightning share of total kills. Below this → CC breach. */
    private static final double CC_KILL_SHARE_MIN            = 0.55;
    /**
     * Thorns + Land Mines share of total kills. Above this → Elite overwhelm.
     * NOTE: high mine kills alone are NOT a failure signal — mines killing enemies at the inner
     * ring means the outer layer is doing its job. This threshold only fires when it coincides
     * with other elite-pressure signals (e.g. killedBy elite type).
     */
    private static final double HEAVY_KILL_SHARE_MAX         = 0.40;
    /** Orb-only share below which the primary AOE anchor is considered collapsed. */
    private static final double ORB_KILL_SHARE_MIN           = 0.25;
    /** Smart Missile share below which the intercept layer is considered underperforming. */
    private static final double SMART_MISSILE_KILL_SHARE_MIN = 0.05;
    /** Death Ray share below which its output is considered negligible for a run it participated in. */
    private static final double DEATH_RAY_KILL_SHARE_MIN     = 0.03;
    /** Poison Swamp share above which it is carrying disproportionate load. */
    private static final double SWAMP_HIGH_SHARE             = 0.10;
    /** Inner land mine fraction of all mine kills above which enemies are penetrating the outer ring. */
    private static final double INNER_MINE_BREACH_RATIO      = 0.50;

    // --- Enemy composition ---
    /**
     * Ranged enemy fraction of cumulative total enemies above which Ranged knockback saturation
     * pressure is elevated. Because TotalEnemies counts are run-long cumulative figures (not
     * concurrent), this threshold reflects sustained Ranged presence across the run rather than
     * any point-in-time screen density.
     */
    private static final double RANGED_DENSITY_HIGH          = 0.15;
    /**
     * Boss + Protector fraction of cumulative total enemies. Used only as a corroborating
     * signal, NOT as a standalone threshold, because cumulative counts cannot represent
     * concurrent screen pressure under the ~150-enemy spawn cap.
     */
    private static final double BOSS_DENSITY_CORROBORATION   = 0.02;

    // --- Damage / survivability ---
    /** Wall damage fraction of total damage taken above which the wall is the dominant sponge.
     *  NOTE: wall dominance is expected and healthy in late-game wall-regen builds. This is used
     *  only to emit an informational observation, never as a primary failure trigger. */
    private static final double WALL_DAMAGE_SHARE_HIGH       = 0.60;
    /** Blocking fraction of gross incoming damage below which defences are under-built. */
    private static final double BLOCK_EFFICIENCY_MIN         = 0.30;
    /**
     * Life steal fraction of total regen below which the "Low Life Steal Reliance" observation
     * fires. Suppressed when wall regen is the dominant regen source (wall-regen builds are
     * expected to have a low life steal fraction; flagging it is misleading in that context).
     */
    private static final double LIFESTEAL_REGEN_SHARE_MIN    = 0.30;
    /**
     * Wall regen fraction of total regen above which this is classified as a wall-regen build.
     * When true, the low life steal observation is suppressed to avoid false positives.
     */
    private static final double WALL_REGEN_BUILD_THRESHOLD   = 0.70;

    // --- Efficiency ---
    /** Projectile hits-per-kill above which DPS is under-scaling for the current tier. */
    private static final double PROJ_HITS_PER_KILL_HIGH      = 8.0;
    /** Death Wave kill share below which its activation timing is poor. */
    private static final double DEATH_WAVE_ACTIVATION_MIN    = 0.10;
    /** Golden Tower coin fraction of total coins below which the golden economy is weak. */
    private static final double GOLDEN_TOWER_COIN_SHARE_MIN  = 0.10;

    public DiagnosisResult analyzeReport(BattleHistory history) {
        Objects.requireNonNull(history, "history must not be null");

        // ── Pull all available sections ───────────────────────────────────────
        BattleReport report      = section(history, SectionHeader.BATTLE_REPORT,             BattleReport.class);
        EnemiesDestroyedBy destroyedBy = section(history, SectionHeader.ENEMIES_DESTROYED_BY,      EnemiesDestroyedBy.class);
        EnemiesHitBy hitBy       = section(history, SectionHeader.ENEMIES_HIT_BY,            EnemiesHitBy.class);
        HealthRegenerated      healthRegen = section(history, SectionHeader.HEALTH_REGENERATED,        HealthRegenerated.class);
        KilledWithEffectActive kwea        = section(history, SectionHeader.KILLED_WITH_EFFECT_ACTIVE, KilledWithEffectActive.class);
        TotalEnemies           totals      = section(history, SectionHeader.TOTAL_ENEMIES,             TotalEnemies.class);
        Damage                 damage      = section(history, SectionHeader.DAMAGE,                    Damage.class);
        DamageTaken            dmgTaken    = section(history, SectionHeader.DAMAGE_TAKEN,              DamageTaken.class);
        DamageBlocked          dmgBlocked  = section(history, SectionHeader.DAMAGE_BLOCKED,            DamageBlocked.class);
        BonusHealthGained      bonusHealth = section(history, SectionHeader.BONUS_HEALTH_GAINED,       BonusHealthGained.class);
        Counts                 counts      = section(history, SectionHeader.COUNTS,                    Counts.class);
        Coins                  coins       = section(history, SectionHeader.COINS,                     Coins.class);
        Cash                   cash        = section(history, SectionHeader.CASH,                      Cash.class);
        Records                records     = section(history, SectionHeader.RECORDS,                   Records.class);
        Utility                utility     = section(history, SectionHeader.UTILITY,                   Utility.class);
        // Currencies: available but not used in diagnostics
        // section(history, SectionHeader.CURRENCIES, Currencies.class)

        // ── Derived kill metrics ──────────────────────────────────────────────
        long totalKills   = totalKills(destroyedBy);
        long orbKills     = getLong(destroyedBy, EnemiesDestroyedBy::orbs);
        long clKills      = getLong(destroyedBy, EnemiesDestroyedBy::chainLightning);
        long thornKills   = getLong(destroyedBy, EnemiesDestroyedBy::thorns);
        long mineKills    = getLong(destroyedBy, EnemiesDestroyedBy::landMines);
        long missileKills = getLong(destroyedBy, EnemiesDestroyedBy::smartMissiles);
        long rayKills     = getLong(destroyedBy, EnemiesDestroyedBy::deathRay);

        double swarmShare   = share(orbKills + clKills,     totalKills);
        double heavyShare   = share(thornKills + mineKills, totalKills);
        double orbShare     = share(orbKills,               totalKills);
        double missileShare = share(missileKills,           totalKills);
        double rayShare     = share(rayKills,               totalKills);

        // ── Derived regen metrics ─────────────────────────────────────────────
        double lifeStealRaw  = healthRegen != null ? toRawDouble(healthRegen.lifeSteal())        : 0.0;
        double towerRegenRaw = healthRegen != null ? toRawDouble(healthRegen.towerHealthRegen()) : 0.0;
        double wallRegenRaw  = healthRegen != null ? toRawDouble(healthRegen.wallHealthRegen())  : 0.0;
        double totalRegenRaw = lifeStealRaw + towerRegenRaw + wallRegenRaw;

        // ── Derived damage metrics ────────────────────────────────────────────
        double towerDmgRaw   = dmgTaken != null ? toRawDouble(dmgTaken.tower()) : 0.0;
        double wallDmgRaw    = dmgTaken != null ? toRawDouble(dmgTaken.wall())  : 0.0;
        double totalDmgTaken = towerDmgRaw + wallDmgRaw;

        double blockedRaw = 0.0;
        if (dmgBlocked != null) {
            blockedRaw = toRawDouble(dmgBlocked.defensePercent())
                    + toRawDouble(dmgBlocked.defenseAbsolute())
                    + toRawDouble(dmgBlocked.chronoField())
                    + toRawDouble(dmgBlocked.chainThunder())
                    + toRawDouble(dmgBlocked.flameBot())
                    + toRawDouble(dmgBlocked.primordialCollapse())
                    + toRawDouble(dmgBlocked.negativeMassProjector());
        }
        // Block efficiency = blocked / (blocked + taken) — fraction of gross incoming stopped
        double totalGrossIncoming = blockedRaw + totalDmgTaken;
        double blockEfficiency    = totalGrossIncoming > 0 ? blockedRaw / totalGrossIncoming : 0.0;

        // ── Derived enemy composition metrics ────────────────────────────────
        long   totalEnemyCount = totals != null ? totals.totalEnemies() : 0L;
        double vampireDensity  = totals != null ? share(totals.vampires(),                          totalEnemyCount) : 0.0;
        double rangedDensity   = totals != null ? share(totals.ranged(),                            totalEnemyCount) : 0.0;
        double bossDensity     = totals != null ? share(totals.boss() + totals.protector(),         totalEnemyCount) : 0.0;

        // ── Killed-by string ─────────────────────────────────────────────────
        String killedBy = report != null ? nullSafe(report.killedBy()) : "";

        // ── Derived damage composition metrics (used by Vampire and DW checks) ─
        // Orbs deal zero damage to Elites — the elite-killing burden falls entirely on
        // projectiles and Chain Lightning. Compute their shares of total damage output.
        double totalDamageDealt = damage != null ? toRawDouble(damage.damageDealt()) : 0.0;
        double projDmgShare     = (damage != null && totalDamageDealt > 0)
                ? toRawDouble(damage.projectiles())    / totalDamageDealt : 0.0;
        double clDmgShare       = (damage != null && totalDamageDealt > 0)
                ? toRawDouble(damage.chainLightning()) / totalDamageDealt : 0.0;
        double eliteKillerShare = projDmgShare + clDmgShare; // fraction of output that can damage Elites

        // ── Death Wave desync signal ──────────────────────────────────────────
        // Desync = Death Wave fired (bonus health generated) but delivered near-zero kills.
        double dwBonusHealthRaw  = bonusHealth != null ? toRawDouble(bonusHealth.fromDeathWave()) : 0.0;
        double dwKweaRaw         = kwea        != null ? toRawDouble(kwea.deathWave())            : 0.0;
        double dwKillShare       = totalKills  > 0     ? dwKweaRaw / totalKills                   : 0.0;
        boolean deathWaveActive  = dwBonusHealthRaw > 0; // proxy: bonus health only accrues when DW fires
        boolean deathWaveDesynced = deathWaveActive && dwKillShare < DEATH_WAVE_ACTIVATION_MIN;

        // ── Collect secondary observations (always exhaustive) ────────────────
        List<Observation> observations = new ArrayList<>();
        collectObservations(
                observations,
                destroyedBy, hitBy, healthRegen, kwea, totals, damage,
                dmgTaken, dmgBlocked, bonusHealth, counts, coins, cash, records, utility,
                totalKills, totalEnemyCount,
                swarmShare, heavyShare, orbShare, missileShare, rayShare,
                lifeStealRaw, towerRegenRaw, wallRegenRaw, totalRegenRaw,
                towerDmgRaw, wallDmgRaw, totalDmgTaken,
                blockedRaw, blockEfficiency, totalGrossIncoming,
                vampireDensity, rangedDensity, bossDensity,
                dwBonusHealthRaw, dwKillShare, deathWaveActive,
                eliteKillerShare, projDmgShare, clDmgShare,
                killedBy);

        // =========================================================================
        // PRIMARY DIAGNOSTIC CHECKS
        // Ordered: most-specific / highest-evidence first. First match wins.
        // =========================================================================

        // ── CHECK 1: Vampire Drain Lock ───────────────────────────────────────
        // Core signals: killed by Vampire + life steal suppressed below survival threshold.
        // Confidence raised by: vampire density (more frequent drain events) and elite-killer
        // share (low projDmg + CL = Vampire outlasted the tower's ability to burn it down).
        if ("Vampire".equalsIgnoreCase(killedBy) && lifeStealRaw < VAMPIRE_LIFESTEAL_CEILING) {
            boolean lowEliteKillerOutput = eliteKillerShare < 0.20; // Orbs = 0 vs Elites; proj+CL carries all Elite DPS
            Confidence conf = vampireDensity >= VAMPIRE_DENSITY_HIGH ? Confidence.HIGH : Confidence.MEDIUM;
            return result(
                    FailureType.VAMPIRE_DRAIN_LOCK, conf,
                    String.format(
                            "Life Steal was suppressed to %s — a fraction of a healthy run's baseline. "
                                    + "The Vampire drained tower recovery faster than the tower could burn it down%s. "
                                    + "%s"
                                    + "%s",
                            formatRaw(lifeStealRaw),
                            vampireDensity >= VAMPIRE_DENSITY_HIGH
                                    ? String.format(" (Vampires were %.1f %% of cumulative spawns)",
                                    vampireDensity * 100.0)
                                    : "",
                            lowEliteKillerOutput
                                    ? String.format("Orbs deal zero damage to Elites, so the entire Elite-killing "
                                            + "burden fell on projectiles (%.1f %% of damage output) and Chain Lightning "
                                            + "(%.1f %%). At %.1f %% combined Elite-capable output, raw DPS was "
                                            + "insufficient to burn through the Vampire's late-wave health pool before "
                                            + "its drain outpaced recovery. ",
                                    projDmgShare * 100.0, clDmgShare * 100.0, eliteKillerShare * 100.0)
                                    : "",
                            deathWaveDesynced
                                    ? "Death Wave fired throughout the run but delivered 0 % of kills — "
                                    + "its burst window was desynced from enemy clusters. Timed correctly "
                                    + "into a Vampire cluster, Death Wave would have provided the Elite "
                                    + "damage spike needed to break the drain cycle."
                                    : ""),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // ── CHECK 2: Boss / Protector Pressure ───────────────────────────────
        // Requires killedBy an elite type. Boss density from TotalEnemies is cumulative (run-long),
        // not concurrent, so it is used only as a corroborating signal to raise confidence —
        // it cannot be the sole trigger.
        boolean killedByElite = "Boss".equalsIgnoreCase(killedBy)
                || "Protector".equalsIgnoreCase(killedBy)
                || "Commander".equalsIgnoreCase(killedBy);
        if (killedByElite) {
            Confidence conf = (totals != null && bossDensity >= BOSS_DENSITY_CORROBORATION)
                    ? Confidence.HIGH : Confidence.MEDIUM;
            return result(
                    FailureType.BOSS_PRESSURE_COLLAPSE, conf,
                    String.format(
                            "The final blow came from a %s. Sustained elite-tier damage "
                                    + "(wall absorbed %.1f %% of total incoming) overwhelmed the "
                                    + "single-target defensive layer before regen could compensate.%s",
                            killedBy,
                            totalDmgTaken > 0 ? (wallDmgRaw / totalDmgTaken) * 100.0 : 0.0,
                            (totals != null && bossDensity >= BOSS_DENSITY_CORROBORATION)
                                    ? String.format(" Bosses and Protectors represented %.1f %% of "
                                            + "cumulative enemy spawns — an elevated presence throughout the run.",
                                    bossDensity * 100.0)
                                    : ""),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // ── CHECK 3: Ranged Slip-Through ─────────────────────────────────────
        // Killed by Ranged with healthy swarm clearance. This is NOT sustained chip damage —
        // it is a targeting queue bottleneck: projectile attention was divided across enough
        // high-health/Protector-shielded targets that knockback cadence on the Ranged unit
        // lapsed, allowing it to lock into its firing animation.
        // High ranged density corroborates sustained knockback pressure across the run.
        // Protector count corroborates shielding congestion that split projectile targeting.
        if ("Ranged".equalsIgnoreCase(killedBy) && swarmShare >= CC_KILL_SHARE_MIN) {
            boolean highRangedDensity    = rangedDensity >= RANGED_DENSITY_HIGH;
            boolean protectorPresence    = totals != null && totals.protector() > 0;
            Confidence conf = (highRangedDensity || protectorPresence) ? Confidence.HIGH : Confidence.MEDIUM;
            return result(
                    FailureType.RANGED_SLIP_THROUGH, conf,
                    String.format(
                            "Crowd control was healthy (%.1f %% swarm kill share) but a Ranged "
                                    + "enemy delivered the final blow. This is a targeting queue bottleneck: "
                                    + "projectile attention was split across %s, leaving insufficient "
                                    + "knockback cadence to keep the Ranged unit locked out of its "
                                    + "firing animation.%s",
                            swarmShare * 100.0,
                            buildCongestionDescription(highRangedDensity, protectorPresence,
                                    rangedDensity, totals),
                            highRangedDensity
                                    ? String.format(" Ranged enemies were %.1f %% of cumulative "
                                            + "spawns — sustained knockback demand throughout the run.",
                                    rangedDensity * 100.0)
                                    : ""),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // ── CHECK 4: Orb Layer Collapse ───────────────────────────────────────
        // More specific than the broad CC breach — checked first.
        if (orbShare < ORB_KILL_SHARE_MIN && totalKills > 0) {
            Confidence conf = swarmShare < CC_KILL_SHARE_MIN ? Confidence.HIGH : Confidence.MEDIUM;
            return result(
                    FailureType.ORB_LAYER_COLLAPSE, conf,
                    String.format(
                            "Orbs accounted for only %.1f %% of kills — well below the %.0f %% threshold. "
                                    + "The primary AOE anchor failed to maintain coverage%s.",
                            orbShare * 100.0, ORB_KILL_SHARE_MIN * 100.0,
                            swarmShare < CC_KILL_SHARE_MIN
                                    ? String.format(", and Chain Lightning could not compensate "
                                    + "(combined CC share: %.1f %%)", swarmShare * 100.0)
                                    : " (Chain Lightning partially compensated but gaps remained)"),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // ── CHECK 5: Crowd Control Breach ────────────────────────────────────
        if (swarmShare < CC_KILL_SHARE_MIN) {
            return result(
                    FailureType.CROWD_CONTROL_BREACH, Confidence.MEDIUM,
                    String.format(
                            "Combined Orb + Chain Lightning kill share dropped to %.1f %% (threshold "
                                    + "%.0f %%). Normal swarms were not being cleared fast enough, allowing "
                                    + "body-blocking and a progressive perimeter breach.",
                            swarmShare * 100.0, CC_KILL_SHARE_MIN * 100.0),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // ── CHECK 6: Elite / Heavy Overwhelm ─────────────────────────────────
        // High heavy kill share (Thorns + Mines) reflects WHERE enemies are being killed, not
        // necessarily WHY the tower died. Inner mines stopping penetrating enemies is healthy.
        // Only trigger this if the kill share is extreme AND corroborated by killedBy enemy type,
        // to avoid false positives on builds with legitimately high mine output.
        boolean killedByHeavy = "Tank".equalsIgnoreCase(killedBy)
                || "Fast".equalsIgnoreCase(killedBy)
                || "Scatter".equalsIgnoreCase(killedBy)
                || "Ray".equalsIgnoreCase(killedBy);
        if (heavyShare > HEAVY_KILL_SHARE_MAX && killedByHeavy) {
            return result(
                    FailureType.ELITE_OVERWHELM, Confidence.MEDIUM,
                    String.format(
                            "Thorns + Land Mines accounted for %.1f %% of kills (threshold > %.0f %%), "
                                    + "and the final blow came from a %s. High-health enemies were "
                                    + "consistently breaching the outer AOE layer and surviving into "
                                    + "the inner mine ring before being stopped.",
                            heavyShare * 100.0, HEAVY_KILL_SHARE_MAX * 100.0, killedBy),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // ── CHECK 7: Defence Saturation ──────────────────────────────────────
        if (totalGrossIncoming > 0 && blockEfficiency < BLOCK_EFFICIENCY_MIN) {
            return result(
                    FailureType.DEFENCE_SATURATION, Confidence.MEDIUM,
                    String.format(
                            "The blocking layer stopped only %.1f %% of gross incoming damage "
                                    + "(threshold %.0f %%). Raw damage reached the tower and wall faster than "
                                    + "mitigation could compensate — the defensive build is under-scaled for "
                                    + "this tier.",
                            blockEfficiency * 100.0, BLOCK_EFFICIENCY_MIN * 100.0),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // NOTE: Wall dominance (wall absorbing >60% of damage) is NOT a primary failure type.
        // In late-game wall-regen builds, this is the expected and healthy architecture.
        // It is captured as an informational observation in collectObservations() instead.

        // ── CHECK 9: Intercept Gap (Smart Missiles) ───────────────────────────
        if (missileKills > 0 && missileShare < SMART_MISSILE_KILL_SHARE_MIN) {
            return result(
                    FailureType.INTERCEPT_GAP, Confidence.LOW,
                    String.format(
                            "Smart Missiles were active but contributed only %.1f %% of kills. Enemies "
                                    + "that should have been intercepted at range reached the inner perimeter "
                                    + "before dying, increasing sustained core pressure.",
                            missileShare * 100.0),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // ── CHECK 10: Death Wave Desync ───────────────────────────────────────
        // Death Wave was active (bonus health generated) but delivered near-zero kills.
        // This means the burst window is firing into empty lanes or already-cleared screens —
        // the primary Elite-softening tool is wasted at the moments it matters most.
        // Only fires when no other primary cause was found, as a targeted improvement signal.
        if (deathWaveDesynced) {
            return result(
                    FailureType.DEATH_WAVE_DESYNC, Confidence.MEDIUM,
                    String.format(
                            "Death Wave fired throughout the run (generating %s of bonus health) "
                                    + "but %.1f %% of kills occurred during an active Death Wave window. "
                                    + "The burst is desynced from enemy clusters — it is firing into empty "
                                    + "lanes or already-cleared screens rather than into dense Elite packs. "
                                    + "Aligning Death Wave with late-wave cluster arrivals would provide "
                                    + "the Elite burst damage the build currently lacks.",
                            formatRaw(dwBonusHealthRaw), dwKillShare * 100.0),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // ── CHECK 11: Death Ray Underperformance ──────────────────────────────
        if (rayKills > 0 && rayShare < DEATH_RAY_KILL_SHARE_MIN) {
            return result(
                    FailureType.DEATH_RAY_UNDERPERFORMANCE, Confidence.LOW,
                    String.format(
                            "The Death Ray fired throughout the run but achieved only %.1f %% kill share. "
                                    + "Poor targeting priority likely directed it at low-value or already-dying "
                                    + "targets instead of Elites.",
                            rayShare * 100.0),
                    swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                    blockEfficiency, vampireDensity, rangedDensity, observations);
        }

        // ── FALLTHROUGH ───────────────────────────────────────────────────────
        return result(
                FailureType.UNKNOWN_DUE_TO_VARIANCE, Confidence.LOW,
                "All measured indicators are within expected ranges. A multi-run historical "
                        + "baseline is required to isolate the cause of this run's failure.",
                swarmShare, heavyShare, lifeStealRaw, totalDmgTaken,
                blockEfficiency, vampireDensity, rangedDensity, observations);
    }

    // =========================================================================
    // Secondary observation collection
    // =========================================================================

    private static void collectObservations(
            List<Observation> obs,
            EnemiesDestroyedBy destroyedBy,
            EnemiesHitBy hitBy,
            HealthRegenerated healthRegen,
            KilledWithEffectActive kwea,
            TotalEnemies totals,
            Damage damage,
            DamageTaken dmgTaken,
            DamageBlocked dmgBlocked,
            BonusHealthGained bonusHealth,
            Counts counts,
            Coins coins,
            Cash cash,
            Records records,
            Utility utility,
            long totalKills, long totalEnemyCount,
            double swarmShare, double heavyShare, double orbShare,
            double missileShare, double rayShare,
            double lifeStealRaw, double towerRegenRaw, double wallRegenRaw, double totalRegenRaw,
            double towerDmgRaw, double wallDmgRaw, double totalDmgTaken,
            double blockedRaw, double blockEfficiency, double totalGrossIncoming,
            double vampireDensity, double rangedDensity, double bossDensity,
            double dwBonusHealthRaw, double dwKillShare, boolean deathWaveActive,
            double eliteKillerShare, double projDmgShare, double clDmgShare,
            String killedBy) {

        // ── Kill share summary ────────────────────────────────────────────────
        if (totalKills > 0 && destroyedBy != null) {
            obs.add(new Observation("Kill Share Overview",
                    String.format(
                            "Swarm (Orbs+CL): %.1f %% | Heavy (Thorns+Mines): %.1f %% | "
                                    + "Orbs alone: %.1f %% | Smart Missiles: %.1f %% | Death Ray: %.1f %%",
                            swarmShare * 100.0, heavyShare * 100.0, orbShare * 100.0,
                            missileShare * 100.0, rayShare * 100.0)));

            // Poison Swamp
            long swampKills = destroyedBy.poisonSwamp();
            if (swampKills > 0) {
                double swampShare = share(swampKills, totalKills);
                if (swampShare > SWAMP_HIGH_SHARE) {
                    obs.add(new Observation("Poison Swamp Carrying Load",
                            String.format("Poison Swamp delivered %.1f %% of kills — unusually high. "
                                            + "It may be compensating for a gap in another layer.",
                                    swampShare * 100.0)));
                }
            }

            // Black Hole
            long bhKills = destroyedBy.blackHole();
            if (bhKills > 0) {
                obs.add(new Observation("Black Hole Kill Share",
                        String.format("%.1f %% of total kills.", share(bhKills, totalKills) * 100.0)));
            }

            // Flame Bot
            long fbKills = destroyedBy.flameBot();
            if (fbKills > 0) {
                obs.add(new Observation("Flame Bot Kill Share",
                        String.format("%.1f %% of total kills.", share(fbKills, totalKills) * 100.0)));
            }

            // Inner mine penetration indicator
            long outerMines = destroyedBy.landMines();
            long innerMines = destroyedBy.innerLandMines();
            if ((outerMines + innerMines) > 0) {
                double innerRatio = share(innerMines, outerMines + innerMines);
                if (innerRatio > INNER_MINE_BREACH_RATIO) {
                    // Note: inner mine kills mean enemies are reaching the inner ring, but also
                    // that they are being stopped there — this is a structural depth observation,
                    // not necessarily a failure signal on its own.
                    obs.add(new Observation("Deep Perimeter Penetration",
                            String.format("%.0f %% of mine kills came from inner mines. High-health "
                                    + "enemies (Tanks, Elites) are surviving the outer AOE layer and "
                                    + "being stopped at the inner ring. This is expected under heavy "
                                    + "elite pressure — concerning only if the inner ring is also "
                                    + "being overwhelmed.", innerRatio * 100.0)));
                }
            }

            // Uncategorised kills
            if (destroyedBy.other() > 0) {
                double otherShare = share(destroyedBy.other(), totalKills);
                if (otherShare > 0.05) {
                    obs.add(new Observation("Uncategorised Kill Source",
                            String.format("%.1f %% of kills are unattributed — may indicate a weapon "
                                    + "type not yet tracked by the parser.", otherShare * 100.0)));
                }
            }
        }

        // ── Enemy composition ─────────────────────────────────────────────────
        if (totals != null && totalEnemyCount > 0) {
            obs.add(new Observation("Enemy Composition",
                    String.format(
                            "Basic: %.1f %% | Fast: %.1f %% | Tank: %.1f %% | Ranged: %.1f %% | "
                                    + "Boss: %.1f %% | Vampire: %.1f %% | Protector: %.1f %% | "
                                    + "Saboteur: %.1f %% | Commander: %.1f %%",
                            share(totals.basic(),     totalEnemyCount) * 100.0,
                            share(totals.fast(),      totalEnemyCount) * 100.0,
                            share(totals.tank(),      totalEnemyCount) * 100.0,
                            rangedDensity * 100.0,
                            share(totals.boss(),      totalEnemyCount) * 100.0,
                            vampireDensity * 100.0,
                            share(totals.protector(), totalEnemyCount) * 100.0,
                            share(totals.saboteur(),  totalEnemyCount) * 100.0,
                            share(totals.commander(), totalEnemyCount) * 100.0)));

            if (totals.saboteur() > 0) {
                obs.add(new Observation("Saboteurs Present",
                        String.format("%d Saboteur(s) appeared. These enemies can disable tower "
                                + "systems — their presence during key waves may have contributed "
                                + "directly to failure.", totals.saboteur())));
            }
            if (totals.overcharge() > 0) {
                obs.add(new Observation("Overcharge Enemies",
                        String.format("%d Overcharge enemy(s) appeared — high-voltage threats that "
                                + "can spike incoming damage unexpectedly.", totals.overcharge())));
            }
            if (totals.rays() > 0) {
                obs.add(new Observation("Ray Enemies",
                        String.format("%d Ray enemy(s) in this run.", totals.rays())));
            }
        }

        // ── Damage taken / blocking ───────────────────────────────────────────
        if (totalDmgTaken > 0) {
            double wallShare = wallDmgRaw / totalDmgTaken;
            obs.add(new Observation("Damage Intake Split",
                    String.format("Tower: %s | Wall: %s (%.0f %% of intake) | "
                                    + "Blocking stopped: %s (%.1f %% efficiency).",
                            formatRaw(towerDmgRaw), formatRaw(wallDmgRaw), wallShare * 100.0,
                            formatRaw(blockedRaw), blockEfficiency * 100.0)));

            if (wallShare > WALL_DAMAGE_SHARE_HIGH) {
                // Informational only — wall dominance is expected architecture in wall-regen builds.
                obs.add(new Observation("Wall-Regen Build Architecture",
                        String.format("The wall absorbed %.0f %% of all damage taken — this is the "
                                + "expected defensive profile for a wall-regen build. Wall health "
                                + "maintenance is the critical dependency.", wallShare * 100.0)));
            }
        }

        if (dmgBlocked != null && blockedRaw > 0) {
            obs.add(new Observation("Blocking Layer Breakdown",
                    String.format("ChronoField: %.1f %% | ChainThunder: %.1f %% | "
                                    + "FlameBot: %.1f %% | Absolute: %.1f %% | Percent: %.1f %% | "
                                    + "PrimordialCollapse: %.1f %% | NegativeMass: %.1f %%",
                            pctOf(toRawDouble(dmgBlocked.chronoField()),             blockedRaw),
                            pctOf(toRawDouble(dmgBlocked.chainThunder()),            blockedRaw),
                            pctOf(toRawDouble(dmgBlocked.flameBot()),                blockedRaw),
                            pctOf(toRawDouble(dmgBlocked.defenseAbsolute()),         blockedRaw),
                            pctOf(toRawDouble(dmgBlocked.defensePercent()),          blockedRaw),
                            pctOf(toRawDouble(dmgBlocked.primordialCollapse()),      blockedRaw),
                            pctOf(toRawDouble(dmgBlocked.negativeMassProjector()),   blockedRaw))));
        }

        // ── Damage dealt breakdown ────────────────────────────────────────────
        if (damage != null) {
            double totalDealt = toRawDouble(damage.damageDealt());
            if (totalDealt > 0) {
                obs.add(new Observation("Damage Output Breakdown",
                        String.format("Total: %s | Projectiles: %.1f %% | Orbs: %.1f %% | "
                                        + "CL: %.1f %% | Thorns: %.1f %% | Mines: %.1f %% | "
                                        + "Death Ray: %.1f %% | Smart Missiles: %.1f %% | "
                                        + "DeathWave: %.1f %% | BlackHole: %.1f %% | FlameBot: %.1f %%",
                                formatRaw(totalDealt),
                                pctOf(toRawDouble(damage.projectiles()),    totalDealt),
                                pctOf(toRawDouble(damage.orbs()),           totalDealt),
                                pctOf(toRawDouble(damage.chainLightning()), totalDealt),
                                pctOf(toRawDouble(damage.thorns()),         totalDealt),
                                pctOf(toRawDouble(damage.landMines()),      totalDealt),
                                pctOf(toRawDouble(damage.deathRay()),       totalDealt),
                                pctOf(toRawDouble(damage.smartMissiles()),  totalDealt),
                                pctOf(toRawDouble(damage.deathWave()),      totalDealt),
                                pctOf(toRawDouble(damage.blackHole()),      totalDealt),
                                pctOf(toRawDouble(damage.flameBot()),       totalDealt))));
            }
        }

        // ── Health regeneration ───────────────────────────────────────────────
        if (totalRegenRaw > 0) {
            double lsFraction = lifeStealRaw / totalRegenRaw;
            boolean isWallRegenBuild = (wallRegenRaw / totalRegenRaw) >= WALL_REGEN_BUILD_THRESHOLD;
            obs.add(new Observation("Regen Breakdown",
                    String.format("Life Steal: %s (%.0f %%) | Tower Passive: %s (%.0f %%) | "
                                    + "Wall Regen: %s (%.0f %%)",
                            formatRaw(lifeStealRaw),   lsFraction * 100.0,
                            formatRaw(towerRegenRaw),  (towerRegenRaw / totalRegenRaw) * 100.0,
                            formatRaw(wallRegenRaw),   (wallRegenRaw  / totalRegenRaw) * 100.0)));

            if (lsFraction < LIFESTEAL_REGEN_SHARE_MIN && lifeStealRaw > 0) {
                if (isWallRegenBuild) {
                    // Wall-regen build: low life steal fraction is expected, not a vulnerability.
                    // Emit informational note instead of a warning.
                    obs.add(new Observation("Wall-Regen Build Confirmed",
                            String.format("Wall Regen supplies %.0f %% of total healing — this is a "
                                            + "wall-regen build. Low Life Steal fraction (%.0f %%) is "
                                            + "expected architecture, not a risk indicator.",
                                    (wallRegenRaw / totalRegenRaw) * 100.0, lsFraction * 100.0)));
                } else {
                    obs.add(new Observation("Low Life Steal Reliance",
                            String.format("Life Steal supplied only %.0f %% of total regen. The build "
                                    + "relies heavily on passive recovery — any kill-rate drop will "
                                    + "sharply reduce survivability.", lsFraction * 100.0)));
                }
            }
            if (wallRegenRaw > towerRegenRaw * 2.0) {
                obs.add(new Observation("Wall Regen Dominates Passive Healing",
                        String.format("Wall regen (%s) is more than 2× tower regen (%s). Losing "
                                        + "the wall would remove the majority of passive healing.",
                                formatRaw(wallRegenRaw), formatRaw(towerRegenRaw))));
            }
        }

        // ── KilledWithEffectActive ────────────────────────────────────────────
        if (kwea != null && totalKills > 0) {
            if (deathWaveActive) {
                if (dwKillShare == 0.0) {
                    // Full desync: DW fired but no kills registered at all during its window.
                    obs.add(new Observation("Death Wave — Full Desync",
                            String.format("Death Wave generated %s of bonus health but delivered 0 %% "
                                            + "of kills. The burst window is not aligning with any enemy clusters — "
                                            + "it is firing into empty lanes or after screens have already cleared. "
                                            + "Timed into late-wave Elite packs, it would provide the missing "
                                            + "burst damage against Elites that Orbs cannot touch.",
                                    formatRaw(dwBonusHealthRaw))));
                } else if (dwKillShare < DEATH_WAVE_ACTIVATION_MIN) {
                    obs.add(new Observation("Death Wave Activation — Low",
                            String.format("%.1f %% of kills occurred during an active Death Wave window "
                                            + "(generated %s bonus health). Burst timing could be improved — "
                                            + "more cluster alignment would significantly boost Elite clear.",
                                    dwKillShare * 100.0, formatRaw(dwBonusHealthRaw))));
                } else {
                    obs.add(new Observation("Death Wave Activation",
                            String.format("%.1f %% of kills occurred during an active Death Wave window.",
                                    dwKillShare * 100.0)));
                }
            }
            if (kwea.goldenTower() > 0) {
                obs.add(new Observation("Golden Tower Kills",
                        String.format("%d kills during Golden Tower activation.",
                                kwea.goldenTower())));
            }
            if (kwea.spotlight() > 0) {
                obs.add(new Observation("Spotlight Kills",
                        String.format("%.1f %% of kills occurred under Spotlight.",
                                share(kwea.spotlight(), totalKills) * 100.0)));
            }
            if (kwea.amplifyBot() > 0) {
                obs.add(new Observation("Amplify Bot Kills",
                        String.format("%d kills during Amplify Bot activation.", kwea.amplifyBot())));
            }
            if (kwea.goldenBot() > 0) {
                obs.add(new Observation("Golden Bot Kills",
                        String.format("%d kills during Golden Bot activation.", kwea.goldenBot())));
            }
            if (kwea.deathPenalty() > 0) {
                obs.add(new Observation("Death Penalty Kills",
                        String.format("%d kills during Death Penalty effect.", kwea.deathPenalty())));
            }
        }

        // ── Projectile efficiency ─────────────────────────────────────────────
        if (hitBy != null && destroyedBy != null && totalKills > 0) {
            double projHits  = toRawDouble(hitBy.projectiles());
            long   projKills = destroyedBy.projectiles();
            if (projHits > 0 && projKills > 0) {
                double hitsPerKill = projHits / projKills;
                if (hitsPerKill > PROJ_HITS_PER_KILL_HIGH) {
                    obs.add(new Observation("Projectile Efficiency Low",
                            String.format("Projectiles averaged %.1f hits per kill — enemies are "
                                    + "absorbing many shots. DPS may be under-scaling for the "
                                    + "current tier.", hitsPerKill)));
                }
            }
            double cfHits = toRawDouble(hitBy.chronoField());
            if (cfHits > 0) {
                obs.add(new Observation("ChronoField Hit Coverage",
                        String.format("ChronoField registered %s hits across the run.",
                                formatRaw(cfHits))));
            }
            double attackChipHits = toRawDouble(hitBy.attackChip());
            if (attackChipHits > 0) {
                obs.add(new Observation("Attack Chip Hits",
                        String.format("Attack Chip registered %s hits.", formatRaw(attackChipHits))));
            }
        }

        // ── Counts / survivability events ─────────────────────────────────────
        if (counts != null) {
            if (counts.deathDefy() > 0) {
                obs.add(new Observation("Death Defies Triggered",
                        String.format("Death Defy fired %d time(s). Each activation reflects a "
                                + "near-death moment — the run was closer to failure earlier than "
                                + "the final wave suggests.", counts.deathDefy())));
            }
            if (counts.nuke() > 0) {
                obs.add(new Observation("Nukes Deployed",
                        String.format("%d nuke(s) used — emergency crowd control was needed at "
                                + "least once.", counts.nuke())));
            }
            if (counts.secondWind() > 0) {
                obs.add(new Observation("Second Wind Activations",
                        String.format("Second Wind triggered %d time(s).", counts.secondWind())));
            }
            if (counts.hitsAbsorbedByEnergyShield() > 0) {
                obs.add(new Observation("Energy Shield Absorption",
                        String.format("Energy Shield absorbed %d hit(s).",
                                counts.hitsAbsorbedByEnergyShield())));
            }
            if (counts.demonMode() > 0) {
                obs.add(new Observation("Demon Mode Activations",
                        String.format("Demon Mode triggered %d time(s) — indicates severe health "
                                + "pressure sustained during the run.", counts.demonMode())));
            }
            // Wave Skip / ELS cross-reference is emitted after the utility block below
            if (counts.thunderBotStuns() > 0) {
                obs.add(new Observation("Thunder Bot Stuns",
                        String.format("%d stuns landed by Thunder Bot.", counts.thunderBotStuns())));
            }
            if (counts.landMinesSpawned() > 0) {
                obs.add(new Observation("Land Mines Spawned",
                        String.format("%d mines placed during this run.", counts.landMinesSpawned())));
            }
        }

        // ── Coins / economy ───────────────────────────────────────────────────
        if (coins != null) {
            double coinsEarned = toRawDouble(coins.coinsEarned());
            if (coinsEarned > 0) {
                double gtShare = toRawDouble(coins.goldenTower()) / coinsEarned;
                if (gtShare < GOLDEN_TOWER_COIN_SHARE_MIN && toRawDouble(coins.goldenTower()) > 0) {
                    obs.add(new Observation("Weak Golden Tower Economy",
                            String.format("Golden Tower contributed only %.1f %% of total coins. "
                                            + "A stronger golden economy would accelerate upgrade cycles.",
                                    gtShare * 100.0)));
                }
                double waveSkipCoins = toRawDouble(coins.waveSkip());
                if (waveSkipCoins > 0) {
                    obs.add(new Observation("Wave Skip Coin Bonus",
                            String.format("Wave skipping generated %s coins (%.1f %% of total income).",
                                    formatRaw(waveSkipCoins),
                                    (waveSkipCoins / coinsEarned) * 100.0)));
                }
                double bountyCoins = toRawDouble(coins.bountyCoins());
                if (bountyCoins > 0) {
                    obs.add(new Observation("Bounty Coins",
                            String.format("%s earned from bounties (%.1f %% of income).",
                                    formatRaw(bountyCoins),
                                    (bountyCoins / coinsEarned) * 100.0)));
                }
            }
        }

        // ── Cash / interest economy ───────────────────────────────────────────
        if (cash != null) {
            double totalCash   = toRawDouble(cash.cashEarned());
            double interestRaw = toRawDouble(cash.interestEarned());
            double gtCash      = toRawDouble(cash.goldenTower());
            if (totalCash > 0) {
                if (interestRaw > 0) {
                    obs.add(new Observation("Interest Income",
                            String.format("Interest provided %s cash (%.1f %% of total).",
                                    formatRaw(interestRaw), (interestRaw / totalCash) * 100.0)));
                }
                if (gtCash > 0) {
                    obs.add(new Observation("Golden Tower Cash Share",
                            String.format("Golden Tower generated %.1f %% of total cash income.",
                                    (gtCash / totalCash) * 100.0)));
                }
            }
        }

        // ── Records / personal bests ──────────────────────────────────────────
        if (records != null) {
            if (records.largestSmartMissileStack() > 0) {
                obs.add(new Observation("Peak Smart Missile Stack",
                        String.format("Largest Smart Missile stack this run: %d.",
                                records.largestSmartMissileStack())));
            }
            if (records.largestGoldenCombo() > 0) {
                obs.add(new Observation("Peak Golden Combo",
                        String.format("Largest Golden Combo: %d — "
                                        + "%s in coin rewards.",
                                records.largestGoldenCombo(),
                                formatRaw(records.mostCoinsFromGoldenCombo()))));
            }
            if (records.largestWaveSkip() > 0) {
                obs.add(new Observation("Largest Wave Skip",
                        String.format("Skipped %d wave(s) in one go, earning %s coins.",
                                records.largestWaveSkip(),
                                formatRaw(records.mostCoinsFromWaveSkip()))));
            }
            double innerCharge = toRawDouble(records.largestInnerLandmineCharge());
            if (innerCharge > 0) {
                obs.add(new Observation("Peak Inner Landmine Charge",
                        String.format("Largest single inner landmine charge: %s.",
                                formatRaw(innerCharge))));
            }
        }

        // ── Utility upgrades ─────────────────────────────────────────────────
        if (utility != null) {
            int freeUpgrades = utility.freeAttackUpgrades()
                    + utility.freeDefenseUpgrades()
                    + utility.freeUtilityUpgrades();
            if (freeUpgrades > 0) {
                obs.add(new Observation("Free Upgrades Received",
                        String.format("%d free upgrade(s) — %d attack, %d defense, %d utility.",
                                freeUpgrades,
                                utility.freeAttackUpgrades(),
                                utility.freeDefenseUpgrades(),
                                utility.freeUtilityUpgrades())));
            }
            if (utility.recoveryPackages() > 0) {
                obs.add(new Observation("Recovery Packages",
                        String.format("%d recovery package(s) collected.",
                                utility.recoveryPackages())));
            }
        }

        // ── Wave Skip × Enemy Level Skip cross-reference ──────────────────────
        // These two mechanics directly offset each other: Wave Skip advances the wave counter
        // (pushing enemies toward higher base stats faster in real time), while ELS suppresses
        // those stat increases per-track. Reporting them in isolation is misleading — what
        // matters is the net per-track effect relative to waves actually defended.
        {
            int wavesSkipped = counts != null ? counts.wavesSkipped() : 0;
            int attackELS    = utility != null ? utility.enemyAttackLevelSkipped() : 0;
            int healthELS    = utility != null ? utility.enemyHealthLevelSkipped() : 0;
            int totalELS     = attackELS + healthELS;

            boolean hasWaveSkip = wavesSkipped > 0;
            boolean hasELS      = totalELS > 0;

            if (hasWaveSkip && hasELS) {
                long netAttack = (long) wavesSkipped - attackELS;
                long netHealth = (long) wavesSkipped - healthELS;

                String message;
                if (netAttack <= 0 && netHealth <= 0) {
                    message = String.format(
                            "%d wave(s) skipped, but Enemy Level Skip triggers (%d attack, %d health) "
                            + "exceeded the wave count on both stat tracks. Net enemy scaling was "
                            + "suppressed by %d levels for Attack and %d levels for Health relative to "
                            + "waves played — an overall difficulty decrease, not an acceleration.",
                            wavesSkipped, attackELS, healthELS,
                            Math.abs(netAttack), Math.abs(netHealth));
                } else if (netAttack > 0 && netHealth > 0) {
                    message = String.format(
                            "%d wave(s) skipped with Enemy Level Skip active (%d attack, %d health). "
                            + "Wave Skip outpaced ELS on both stat tracks — net enemy scaling "
                            + "increased by %d attack levels and %d health levels relative to waves played.",
                            wavesSkipped, attackELS, healthELS, netAttack, netHealth);
                } else {
                    message = String.format(
                            "%d wave(s) skipped with Enemy Level Skip active (%d attack, %d health). "
                            + "Net scaling per track: Attack %s%d levels, Health %s%d levels relative to waves played.",
                            wavesSkipped, attackELS, healthELS,
                            netAttack >= 0 ? "+" : "", netAttack,
                            netHealth >= 0 ? "+" : "", netHealth);
                }
                obs.add(new Observation("Net Difficulty Impact (Wave Skip vs ELS)", message));
            } else if (hasWaveSkip) {
                obs.add(new Observation("Waves Skipped",
                        String.format("%d wave(s) skipped — accelerated enemy scaling may have "
                                + "contributed to a faster difficulty ramp.", wavesSkipped)));
            } else if (hasELS) {
                obs.add(new Observation("Enemy Levels Skipped",
                        String.format("%d enemy level(s) bypassed (%d attack, %d health). "
                                + "These decrease difficulty by preventing enemy stats from increasing.",
                                totalELS, attackELS, healthELS)));
            }
        }
    }

    /**
     * Builds a natural-language description of what was congesting the targeting queue in a
     * Ranged slip-through scenario, based on available corroborating signals.
     */
    private static String buildCongestionDescription(
            boolean highRangedDensity, boolean protectorPresence,
            double rangedDensity, TotalEnemies totals) {
        if (highRangedDensity && protectorPresence) {
            return String.format("a sustained high-Ranged field (%.1f %% of spawns) behind "
                            + "Protector shielding (%d Protectors over the run)",
                    rangedDensity * 100.0, totals.protector());
        }
        if (protectorPresence) {
            return String.format("Protector shielding (%d Protectors over the run) — shielded "
                            + "enemies linger in the spawn cap, starving unshielded units of knockback frames",
                    totals.protector());
        }
        if (highRangedDensity) {
            return String.format("a sustained high-Ranged field (%.1f %% of spawns) requiring "
                    + "continuous knockback maintenance across many targets", rangedDensity * 100.0);
        }
        return "a dense mix of high-health targets competing for projectile targeting slots";
    }

    // =========================================================================
    // Utility helpers
    // =========================================================================

    /** Safely retrieves and casts a section from the history map. Returns null if absent. */
    @SuppressWarnings("unchecked")
    private static <T> T section(BattleHistory history, SectionHeader header, Class<T> type) {
        Section s = history.sectionMap().get(header);
        return type.isInstance(s) ? (T) s : null;
    }

    /** Extracts a long field from a nullable record via a method reference. */
    @FunctionalInterface
    private interface LongGetter<T> { long get(T t); }

    private static <T> long getLong(T sec, LongGetter<T> getter) {
        return sec != null ? getter.get(sec) : 0L;
    }

    /** Sums every kill category from {@link EnemiesDestroyedBy}. Returns 0 if section is null. */
    private static long totalKills(EnemiesDestroyedBy d) {
        if (d == null) return 0L;
        return d.projectiles() + d.thorns()        + d.landMines()     + d.orbs()
                + d.chainLightning() + d.smartMissiles() + d.innerLandMines()
                + d.poisonSwamp()   + d.deathRay()    + d.blackHole() + d.flameBot() + d.other();
    }

    /**
     * Converts a {@link TowerNumber} to its absolute {@code double} value, respecting
     * {@link ScaleSuffix} magnitude. Returns 0.0 for null input.
     */
    public static double toRawDouble(TowerNumber tn) {
        if (tn == null) return 0.0;
        BigDecimal base = tn.amount() != null ? tn.amount() : BigDecimal.ZERO;
        if (tn.scaleSuffix() == null) return base.doubleValue();
        return base.multiply(tn.scaleSuffix().getScientificNotation()).doubleValue();
    }

    /** {@code numerator / denominator} as [0–1]; 0.0 when denominator is zero. */
    private static double share(long numerator, long denominator) {
        return denominator > 0 ? (double) numerator / denominator : 0.0;
    }

    /** {@code numerator / denominator * 100} as a percentage; 0.0 when denominator is zero. */
    private static double pctOf(double numerator, double denominator) {
        return denominator > 0 ? (numerator / denominator) * 100.0 : 0.0;
    }

    /**
     * Formats a raw double to a human-readable suffixed string (e.g. {@code "26.98 T"}).
     * Falls back to plain decimal for values below 1 000.
     */
    public static String formatRaw(double raw) {
        if (raw == 0.0) return "0";
        ScaleSuffix[] suffixes = ScaleSuffix.values();
        for (int i = suffixes.length - 1; i >= 0; i--) {
            double scale = suffixes[i].getScientificNotation().doubleValue();
            if (raw >= scale) {
                return String.format("%.2f %s", raw / scale, suffixes[i].getSuffix());
            }
        }
        return String.format("%.2f", raw);
    }

    /** Convenience overload for {@link TowerNumber}. */
    public static String formatRaw(TowerNumber tn) {
        return formatRaw(toRawDouble(tn));
    }

    private static String nullSafe(String s) { return s != null ? s : ""; }

    private static DiagnosisResult result(
            FailureType type, Confidence confidence, String explanation,
            double swarmShare, double heavyShare, double lifeStealRaw,
            double totalDmgTaken, double blockEfficiency,
            double vampireDensity, double rangedDensity,
            List<Observation> observations) {
        return new DiagnosisResult(
                type, confidence, explanation,
                swarmShare, heavyShare, lifeStealRaw,
                totalDmgTaken, blockEfficiency, vampireDensity, rangedDensity,
                Collections.unmodifiableList(observations));
    }
}
