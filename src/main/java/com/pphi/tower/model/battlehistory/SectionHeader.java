package com.pphi.tower.model.battlehistory;

import java.util.Arrays;
import java.util.List;

public enum SectionHeader {
    BATTLE_REPORT("Battle Report", BattleReport.class),
    RECORDS("Records", Records.class),
    DAMAGE("Damage", Damage.class),
    DAMAGE_TAKEN("Damage Taken", DamageTaken.class),
    BONUS_HEALTH_GAINED("Bonus Health Gained", BonusHealthGained.class),
    HEALTH_REGENERATED("Health Regenerated", HealthRegenerated.class),
    DAMAGE_BLOCKED("Damage Blocked", DamageBlocked.class),
    UTILITY("Utility", Utility.class),
    COUNTS("Counts", Counts.class),
    ENEMIES_HIT_BY("Enemies Hit By", EnemiesHitBy.class),
    KILLED_WITH_EFFECT_ACTIVE("Killed With Effect Active", KilledWithEffectActive.class),
    TOTAL_ENEMIES("Total Enemies", TotalEnemies.class),
    COINS("Coins", Coins.class),
    CASH("Cash", Cash.class),
    CURRENCIES("Currencies", Currencies.class),
    ENEMIES_DESTROYED_BY("Enemies Destroyed By", EnemiesDestroyedBy.class);

    private static final List<SectionHeader> SECTION_HEADERS = Arrays.stream(SectionHeader.values()).toList();

    private final String name;
    private final Class<?> type;

    SectionHeader(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    public static List<SectionHeader> valuesList() {
        return SECTION_HEADERS;
    }

    public static SectionHeader fromName(String name) {
        return SECTION_HEADERS
                .stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
