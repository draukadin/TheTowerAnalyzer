package com.pphi.tower.model;

/**
 * Module upgrade cost table derived from game data.
 * Shard and coin costs are pre-lab-discount values.
 */
public final class ModuleLevelTable {

    public static final int MAX_LEVEL = 300;

    private ModuleLevelTable() {}

    /** Shards required to upgrade FROM (level-1) TO level. Level 1 = 0. */
    public static long shardsForLevel(int level) {
        if (level <= 1)   return 0;
        if (level <= 5)   return 7;
        if (level <= 10)  return 12;
        if (level <= 15)  return 20;
        if (level <= 20)  return 25;
        if (level <= 25)  return 40;
        if (level <= 30)  return 50;
        if (level <= 35)  return 75;
        if (level <= 40)  return 90;
        if (level <= 50)  return 120;
        if (level <= 60)  return 180;
        if (level <= 70)  return 250;
        if (level <= 80)  return 350;
        if (level <= 90)  return 500;
        if (level <= 100) return 700;
        if (level <= 110) return 1_000;
        if (level <= 120) return 1_300;
        if (level <= 130) return 1_800;
        if (level <= 140) return 2_500;
        if (level <= 150) return 3_000;
        if (level <= 160) return 4_000;
        if (level == 161) return 5_000;
        if (level <= 201) return 5_000L + (long) (level - 161) * 125;
        return 10_250L + (long) (level - 202) * 250;
    }

    /** Total shards spent to reach the given level (from level 1). */
    public static long cumulativeShardsTo(int level) {
        long total = 0;
        for (int l = 2; l <= level; l++) {
            total += shardsForLevel(l);
        }
        return total;
    }

    /**
     * Shards still needed to go from currentLevel to targetLevel.
     * Returns 0 if already at or past targetLevel.
     */
    public static long shardsRemainingTo(int currentLevel, int targetLevel) {
        if (currentLevel >= targetLevel) return 0;
        return cumulativeShardsTo(targetLevel) - cumulativeShardsTo(currentLevel);
    }

    /** Coins required to upgrade FROM (level-1) TO level. Level 1 = 0. */
    public static long coinsForLevel(int level) {
        if (level <= 1)   return 0;
        if (level <= 5)   return 10_000;
        if (level <= 10)  return 25_000;
        if (level <= 15)  return 45_000;
        if (level <= 20)  return 60_000;
        if (level <= 25)  return 120_000;
        if (level <= 30)  return 180_000;
        if (level <= 35)  return 350_000;
        if (level <= 40)  return 500_000;
        if (level <= 50)  return 1_000_000;
        if (level <= 60)  return 3_000_000;
        if (level <= 70)  return 25_000_000;
        if (level <= 80)  return 100_000_000;
        if (level <= 100) return 350_000_000;
        if (level <= 120) return 8_000_000_000L;
        if (level <= 140) return 32_000_000_000L;
        if (level <= 160) return 500_000_000_000L;
        if (level == 161) return 10_000_000_000_000L;
        return 60_000_000_000_000L + (long)(level - 162) * 50_000_000_000_000L;
    }

    /** Total coins spent to reach the given level (from level 1). */
    public static long cumulativeCoinsTo(int level) {
        long total = 0;
        for (int l = 2; l <= level; l++) {
            total += coinsForLevel(l);
        }
        return total;
    }

    /** Coins still needed to go from currentLevel to targetLevel. */
    public static long coinsRemainingTo(int currentLevel, int targetLevel) {
        if (currentLevel >= targetLevel) return 0;
        return cumulativeCoinsTo(targetLevel) - cumulativeCoinsTo(currentLevel);
    }
}
