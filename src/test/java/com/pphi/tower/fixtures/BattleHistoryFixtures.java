package com.pphi.tower.fixtures;

import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerEra;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.battlehistory.*;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

import static com.pphi.tower.fixtures.TowerNumberFactory.*;

/**
 * Builds BattleHistory objects for each BattleDiagnostic failure-type scenario.
 * Each factory method satisfies exactly the thresholds needed to trigger (or not trigger)
 * a specific check, with all other values in a healthy baseline range.
 */
public final class BattleHistoryFixtures {

    private BattleHistoryFixtures() {}

    // ── Shared baselines ──────────────────────────────────────────────────────

    /** 10 000 total kills split in a healthy CC-dominant pattern. */
    private static final long KILLS_ORBS          = 5_000L;
    private static final long KILLS_CL            = 3_000L;
    private static final long KILLS_THORNS        =   500L;
    private static final long KILLS_MINES         =   300L;
    private static final long KILLS_INNER_MINES   =   100L;
    private static final long KILLS_PROJECTILES   =   900L;
    private static final long KILLS_POISON_SWAMP  =   100L;
    private static final long KILLS_BLACK_HOLE    =   100L;
    private static final long KILLS_FLAME_BOT     =     0L;
    private static final long KILLS_OTHER         =     0L;
    // Smart missiles and Death Ray default to 0 so checks 9 and 11 don't fire.
    private static final long KILLS_SMART_MISSILES = 0L;
    private static final long KILLS_DEATH_RAY      = 0L;

    /** ~100 T life steal (raw) — well above the 30 T suppression ceiling. */
    private static final TowerNumber HEALTHY_LIFESTEAL  = of(100.0, ScaleSuffix.TRILLION);
    private static final TowerNumber HEALTHY_TOWER_REGEN = of(50.0,  ScaleSuffix.TRILLION);
    private static final TowerNumber HEALTHY_WALL_REGEN  = of(200.0, ScaleSuffix.TRILLION);

    /** Blocked >> taken → block efficiency ~80 %. */
    private static final TowerNumber BLOCKED_DEFENSE_PCT  = of(400.0, ScaleSuffix.TRILLION);
    private static final TowerNumber DAMAGE_TAKEN_TOWER   = of(30.0,  ScaleSuffix.TRILLION);
    private static final TowerNumber DAMAGE_TAKEN_WALL    = of(70.0,  ScaleSuffix.TRILLION);

    // ── CHECK 1a: Vampire Active at Run End (healing fully suppressed) ────────

    /**
     * Life Steal and Tower Regen are both zero (Vampire aura active at run end),
     * but Wall Regen is non-zero and vampires > 0. killedBy is "Basic" to distinguish
     * this path from the killedBy-Vampire path (CHECK 1b).
     * Expected: VAMPIRE_DRAIN_LOCK / MEDIUM
     */
    public static BattleHistory vampireAuraActive() {
        return build(
                report("Basic"),
                destroyedBy(KILLS_ORBS, KILLS_CL, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, KILLS_PROJECTILES, KILLS_SMART_MISSILES,
                        KILLS_DEATH_RAY, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                new HealthRegenerated(zero(), zero(), HEALTHY_WALL_REGEN),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 500L, 0L, 1_000L),   // vampires=1000 → spawned
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 1b: Vampire Drain Lock (killedBy Vampire, life steal suppressed) ─

    /**
     * killedBy Vampire + life steal below 30 T ceiling + vampire density >= 3 %.
     * Expected: VAMPIRE_DRAIN_LOCK / HIGH
     */
    public static BattleHistory vampireDrainLock() {
        return build(
                report("Vampire"),
                destroyedBy(KILLS_ORBS, KILLS_CL, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, KILLS_PROJECTILES, KILLS_SMART_MISSILES,
                        KILLS_DEATH_RAY, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                // Life steal = 10 T < 30 T ceiling; tower regen non-zero so check 1a doesn't fire first
                new HealthRegenerated(
                        of(10.0, ScaleSuffix.TRILLION),
                        HEALTHY_TOWER_REGEN,
                        HEALTHY_WALL_REGEN),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 500L, 0L, 1_500L),   // vampires=1500 → density=3 % → HIGH
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 2: Boss / Protector Pressure ───────────────────────────────────

    /**
     * killedBy Boss + boss density >= 2 %.
     * Expected: BOSS_PRESSURE_COLLAPSE / HIGH
     */
    public static BattleHistory killedByBoss() {
        return build(
                report("Boss"),
                destroyedBy(KILLS_ORBS, KILLS_CL, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, KILLS_PROJECTILES, KILLS_SMART_MISSILES,
                        KILLS_DEATH_RAY, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                healthRegenDefault(),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 500L, 1_500L, 0L),   // boss=1500 → density=3 % → HIGH
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 3: Ranged Slip-Through ─────────────────────────────────────────

    /**
     * killedBy Ranged + healthy swarm share + ranged density >= 15 %.
     * Expected: RANGED_SLIP_THROUGH / HIGH
     */
    public static BattleHistory rangedSlipThrough() {
        return build(
                report("Ranged"),
                destroyedBy(KILLS_ORBS, KILLS_CL, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, KILLS_PROJECTILES, KILLS_SMART_MISSILES,
                        KILLS_DEATH_RAY, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                healthRegenDefault(),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 7_500L, 500L, 0L),   // ranged=7500 → 15 % → HIGH
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 4: Orb Layer Collapse ──────────────────────────────────────────

    /**
     * orbShare < 25 % AND swarmShare < 55 % → HIGH confidence.
     * killedBy "Basic" so no vampire/elite/ranged checks fire.
     * Expected: ORB_LAYER_COLLAPSE / HIGH
     */
    public static BattleHistory orbLayerCollapse() {
        // orbs = 1000 (10 %), CL = 3000 (30 %), swarmShare = 40 % < 55 %
        // totalKills = 10 000
        return build(
                report("Basic"),
                destroyedBy(1_000L, 3_000L, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, 5_100L, KILLS_SMART_MISSILES,
                        KILLS_DEATH_RAY, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                healthRegenDefault(),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 5_000L, 500L, 0L),
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 5: Crowd Control Breach ────────────────────────────────────────

    /**
     * swarmShare < 55 % but orbShare >= 25 % (CL weak, orbs moderate).
     * Checks 1–4 must NOT fire: killedBy "Basic", vampires=0, orbs >= 25 %.
     * Expected: CROWD_CONTROL_BREACH / MEDIUM
     */
    public static BattleHistory ccBreach() {
        // orbs = 3000 (30 %), CL = 1000 (10 %), swarmShare = 40 % < 55 %
        // orbShare = 30 % >= 25 % → check 4 does not fire
        return build(
                report("Basic"),
                destroyedBy(3_000L, 1_000L, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, 5_100L, KILLS_SMART_MISSILES,
                        KILLS_DEATH_RAY, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                healthRegenDefault(),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 5_000L, 500L, 0L),
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 6: Elite Overwhelm ─────────────────────────────────────────────

    /**
     * heavyShare (thorns + mines) > 40 % AND killedBy "Tank".
     * swarmShare must be healthy so checks 4/5 don't fire first.
     * Expected: ELITE_OVERWHELM / MEDIUM
     */
    public static BattleHistory eliteOverwhelm() {
        // orbs=4000(40%), CL=2000(20%), swarmShare=60% healthy
        // thorns=2500, mines=1500, heavyShare=4000/10000=40% — need > 40%, so thorns=2600, mines=1500 → 41%
        return build(
                report("Tank"),
                destroyedBy(4_000L, 2_000L, 2_600L, 1_500L,
                        KILLS_INNER_MINES, 0L, KILLS_SMART_MISSILES,
                        KILLS_DEATH_RAY, 0L, 0L,
                        KILLS_FLAME_BOT, 0L),
                hitBy(),
                healthRegenDefault(),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 5_000L, 500L, 0L),
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 7: Defence Saturation ──────────────────────────────────────────

    /**
     * blockEfficiency < 30 % (blocked=50T, total taken=200T → gross=250T, efficiency=20%).
     * Swarm, orb, and elite checks must not fire before this.
     * Expected: DEFENCE_SATURATION / MEDIUM
     */
    public static BattleHistory defenceSaturation() {
        return build(
                report("Basic"),
                destroyedBy(KILLS_ORBS, KILLS_CL, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, KILLS_PROJECTILES, KILLS_SMART_MISSILES,
                        KILLS_DEATH_RAY, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                healthRegenDefault(),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 5_000L, 500L, 0L),
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                // large taken, tiny blocked → block efficiency ~20 %
                new DamageTaken(of(100.0, ScaleSuffix.TRILLION), of(100.0, ScaleSuffix.TRILLION)),
                new DamageBlocked(
                        of(25.0, ScaleSuffix.TRILLION),    // defensePercent
                        zero(), zero(), zero(), zero(), zero(), zero()),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 9: Intercept Gap (Smart Missiles) ───────────────────────────────

    /**
     * Smart Missiles active (missileKills > 0) but missileShare < 5 %.
     * All earlier checks must not fire.
     * Expected: INTERCEPT_GAP / LOW
     */
    public static BattleHistory interceptGap() {
        // base kills=10000; add 200 missile kills → missileShare=200/10200=~2% < 5%
        return build(
                report("Basic"),
                destroyedBy(KILLS_ORBS, KILLS_CL, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, KILLS_PROJECTILES, 200L,
                        KILLS_DEATH_RAY, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                healthRegenDefault(),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 5_000L, 500L, 0L),
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 10: Death Wave Desync ───────────────────────────────────────────

    /**
     * Death Wave active (bonus health > 0) but dwKillShare < 10 %.
     * No missiles or ray kills so checks 9/11 don't fire.
     * Expected: DEATH_WAVE_DESYNC / MEDIUM
     */
    public static BattleHistory deathWaveDesync() {
        // totalKills=10000; DW kills via kwea.deathWave()=raw=500 < 10% of 10000 (= 1000)
        // dwKillShare = 500 / 10000 = 5% < 10%
        return build(
                report("Basic"),
                destroyedBy(KILLS_ORBS, KILLS_CL, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, KILLS_PROJECTILES, 0L,
                        0L, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                healthRegenDefault(),
                // DW killed 500 enemies (raw long, stored in kwea.deathWave as TowerNumber)
                kwea(of(500.0, null)),    // suffix=null → raw = 500 < 1000 = 10% of totalKills
                totals(50_000L, 0L, 0L, 5_000L, 500L, 0L),
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                // bonus health > 0 → deathWaveActive = true
                new BonusHealthGained(of(10.0, ScaleSuffix.TRILLION)),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── CHECK 11: Death Ray Underperformance ──────────────────────────────────

    /**
     * Death Ray active (rayKills > 0) but rayShare < 3 %.
     * All earlier checks must not fire. No DW, no missiles.
     * Expected: DEATH_RAY_UNDERPERFORMANCE / LOW
     */
    public static BattleHistory deathRayUnderperformance() {
        // totalKills=10000+200=10200; ray=200 → rayShare=200/10200=~2% < 3%
        return build(
                report("Basic"),
                destroyedBy(KILLS_ORBS, KILLS_CL, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, KILLS_PROJECTILES, 0L,
                        200L, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                healthRegenDefault(),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 5_000L, 500L, 0L),
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── FALLTHROUGH: Unknown Variance ─────────────────────────────────────────

    /**
     * All indicators healthy — no check fires.
     * Expected: UNKNOWN_DUE_TO_VARIANCE / LOW
     */
    public static BattleHistory unknownVariance() {
        return build(
                report("Basic"),
                destroyedBy(KILLS_ORBS, KILLS_CL, KILLS_THORNS, KILLS_MINES,
                        KILLS_INNER_MINES, KILLS_PROJECTILES, 0L,
                        0L, KILLS_POISON_SWAMP, KILLS_BLACK_HOLE,
                        KILLS_FLAME_BOT, KILLS_OTHER),
                hitBy(),
                healthRegenDefault(),
                kwea(zero()),
                totals(50_000L, 0L, 0L, 5_000L, 500L, 0L),
                damage(of(500.0, ScaleSuffix.TRILLION),
                        of(200.0, ScaleSuffix.TRILLION),
                        of(100.0, ScaleSuffix.TRILLION)),
                new DamageTaken(DAMAGE_TAKEN_TOWER, DAMAGE_TAKEN_WALL),
                blockedDefault(),
                new BonusHealthGained(zero()),
                countsDefault(),
                coinsDefault(),
                cashDefault(),
                recordsDefault(),
                utilityDefault()
        );
    }

    // ── Section constructors ──────────────────────────────────────────────────

    private static BattleReport report(String killedBy) {
        return new BattleReport(
                new TowerEra(1, 0, 0),
                Instant.parse("2025-01-01T00:00:00Z"),
                Duration.ofHours(2),
                Duration.ofHours(2),
                8,
                3000,
                killedBy,
                of(1.0, ScaleSuffix.QUADRILLION),
                of(500.0, ScaleSuffix.TRILLION),
                of(10.0, null),
                of(5.0, null));
    }

    private static EnemiesDestroyedBy destroyedBy(
            long orbs, long cl, long thorns, long mines, long innerMines,
            long projectiles, long smartMissiles, long deathRay,
            long poisonSwamp, long blackHole, long flameBot, long other) {
        return new EnemiesDestroyedBy(
                projectiles, thorns, mines, orbs, cl, smartMissiles,
                innerMines, poisonSwamp, deathRay, blackHole, flameBot, other);
    }

    private static EnemiesHitBy hitBy() {
        TowerNumber z = zero();
        return new EnemiesHitBy(
                of(5.0, ScaleSuffix.BILLION), z, of(2.0, ScaleSuffix.BILLION),
                of(1.0, ScaleSuffix.BILLION), of(1.5, ScaleSuffix.BILLION), z,
                z, z, z, z, z, z, z, z, z, z);
    }

    private static HealthRegenerated healthRegenDefault() {
        return new HealthRegenerated(HEALTHY_LIFESTEAL, HEALTHY_TOWER_REGEN, HEALTHY_WALL_REGEN);
    }

    private static KilledWithEffectActive kwea(TowerNumber deathWaveKills) {
        return new KilledWithEffectActive(500L, deathWaveKills, 200L, 100L, 50L, 30L);
    }

    /**
     * @param totalEnemies  total cumulative spawns
     * @param summonedRaw   summoned enemies count
     * @param commanders    commander count
     * @param ranged        ranged count
     * @param boss          boss count
     * @param vampires      vampire count
     */
    private static TotalEnemies totals(
            long totalEnemies, long summonedRaw, long commanders,
            long ranged, long boss, long vampires) {
        long basic = totalEnemies - ranged - boss - vampires - commanders - summonedRaw;
        return new TotalEnemies(
                totalEnemies, basic, 1_000L, 2_000L, ranged, boss,
                500L, vampires, 200L, 300L, 100L, commanders, 50L,
                of(summonedRaw, null));
    }

    private static Damage damage(TowerNumber total, TowerNumber proj, TowerNumber cl) {
        TowerNumber z = zero();
        return new Damage(total, proj, z, z, z, z, z, cl, z, z, z, z, z, z, z, z);
    }

    private static DamageBlocked blockedDefault() {
        return new DamageBlocked(
                BLOCKED_DEFENSE_PCT, zero(), zero(), zero(), zero(), zero(), zero());
    }

    private static Counts countsDefault() {
        return new Counts(of(1.0, ScaleSuffix.BILLION), 500, 1_000L, 0, 0, 0, 0, 0, 0);
    }

    private static Coins coinsDefault() {
        TowerNumber z = zero();
        TowerNumber c = of(100.0, ScaleSuffix.QUADRILLION);
        return new Coins(c, c, z, z, of(20.0, ScaleSuffix.QUADRILLION), z, z, z, z, z, z, z, z, z, z);
    }

    private static Cash cashDefault() {
        return new Cash(of(500.0, ScaleSuffix.TRILLION), of(100.0, ScaleSuffix.TRILLION), zero());
    }

    private static Records recordsDefault() {
        TowerNumber z = zero();
        return new Records(of(10.0, ScaleSuffix.TRILLION), 0, z, 0, 0, 0, z, z);
    }

    private static Utility utilityDefault() {
        return new Utility(0, 0, 0, 0, 0, 0);
    }

    private static Currencies currenciesDefault() {
        TowerNumber z = zero();
        return new Currencies(of(10.0, null), 5L, 0L, 0L, 0L, 2L, z, z, 0L, 0L, 0L, 0L, 0L, 0L);
    }

    // ── Map assembly ──────────────────────────────────────────────────────────

    private static BattleHistory build(
            BattleReport report,
            EnemiesDestroyedBy destroyedBy,
            EnemiesHitBy hitBy,
            HealthRegenerated healthRegen,
            KilledWithEffectActive kwea,
            TotalEnemies totals,
            Damage damage,
            DamageTaken damageTaken,
            DamageBlocked damageBlocked,
            BonusHealthGained bonusHealth,
            Counts counts,
            Coins coins,
            Cash cash,
            Records records,
            Utility utility) {

        Map<SectionHeader, Section> map = new EnumMap<>(SectionHeader.class);
        map.put(SectionHeader.BATTLE_REPORT,             report);
        map.put(SectionHeader.ENEMIES_DESTROYED_BY,      destroyedBy);
        map.put(SectionHeader.ENEMIES_HIT_BY,            hitBy);
        map.put(SectionHeader.HEALTH_REGENERATED,        healthRegen);
        map.put(SectionHeader.KILLED_WITH_EFFECT_ACTIVE, kwea);
        map.put(SectionHeader.TOTAL_ENEMIES,             totals);
        map.put(SectionHeader.DAMAGE,                    damage);
        map.put(SectionHeader.DAMAGE_TAKEN,              damageTaken);
        map.put(SectionHeader.DAMAGE_BLOCKED,            damageBlocked);
        map.put(SectionHeader.BONUS_HEALTH_GAINED,       bonusHealth);
        map.put(SectionHeader.COUNTS,                    counts);
        map.put(SectionHeader.COINS,                     coins);
        map.put(SectionHeader.CASH,                      cash);
        map.put(SectionHeader.RECORDS,                   records);
        map.put(SectionHeader.UTILITY,                   utility);
        map.put(SectionHeader.CURRENCIES,                currenciesDefault());
        return new BattleHistory(map);
    }
}