package com.pphi.tower.service.context;

import com.pphi.tower.model.battlehistory.*;

import java.util.List;
import java.util.Map;

public class ComparisonReportContext implements ChatContext {

    private final List<BattleHistory> comparisonResult;
    private final String reportId1;
    private final String reportId2;

    public ComparisonReportContext(List<BattleHistory> comparisonResult, String reportId1, String reportId2) {
        this.comparisonResult = comparisonResult;
        this.reportId1 = reportId1;
        this.reportId2 = reportId2;
    }

    @Override
    public String getLabel() {
        return "Comparison Report Results";
    }

    @Override
    public String getContent() {
        BattleHistory r1 = comparisonResult.get(0);
        BattleHistory r2 = comparisonResult.get(1);
        BattleHistory delta = comparisonResult.get(2);

        Map<SectionHeader, Section> m1 = r1.sectionMap();
        Map<SectionHeader, Section> m2 = r2.sectionMap();
        Map<SectionHeader, Section> md = delta.sectionMap();

        StringBuilder sb = new StringBuilder();
        sb.append("=== The Tower Battle Run Comparison ===\n\n");
        sb.append("Report 1 ID: ").append(reportId1).append("\n");
        sb.append("Report 2 ID: ").append(reportId2).append("\n\n");

        appendBattleReport(sb, m1, m2, md);
        appendRecords(sb, m1, m2, md);
        appendDamage(sb, m1, m2, md);
        appendDamageTaken(sb, m1, m2, md);
        appendDamageBlocked(sb, m1, m2, md);
        appendCoins(sb, m1, m2, md);
        appendCurrencies(sb, m1, m2, md);
        appendEnemiesDestroyedBy(sb, m1, m2, md);

        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Section formatters
    // -------------------------------------------------------------------------

    private void appendBattleReport(StringBuilder sb,
            Map<SectionHeader, Section> m1, Map<SectionHeader, Section> m2, Map<SectionHeader, Section> md) {
        BattleReport s1 = cast(m1, SectionHeader.BATTLE_REPORT, BattleReport.class);
        BattleReport s2 = cast(m2, SectionHeader.BATTLE_REPORT, BattleReport.class);
        BattleReport sd = cast(md, SectionHeader.BATTLE_REPORT, BattleReport.class);
        if (s1 == null || s2 == null) return;

        sb.append("--- Battle Report ---\n");
        row(sb, "Tower Era",      s1.towerEra(),      s2.towerEra(),      null);
        row(sb, "Tier",           s1.tier(),           s2.tier(),           sd != null ? sd.tier() : null);
        row(sb, "Wave",           s1.wave(),           s2.wave(),           sd != null ? sd.wave() : null);
        row(sb, "Game Time",      s1.gameTime(),       s2.gameTime(),       sd != null ? sd.gameTime() : null);
        row(sb, "Real Time",      s1.realTime(),       s2.realTime(),       sd != null ? sd.realTime() : null);
        row(sb, "Killed By",      s1.killedBy(),       s2.killedBy(),       null);
        row(sb, "Coins Earned",   s1.coinsEarned(),    s2.coinsEarned(),    sd != null ? sd.coinsEarned() : null);
        row(sb, "Coins Per Hour", s1.coinsPerHour(),   s2.coinsPerHour(),   sd != null ? sd.coinsPerHour() : null);
        row(sb, "Cells Earned",   s1.cellsEarned(),    s2.cellsEarned(),    sd != null ? sd.cellsEarned() : null);
        row(sb, "Cells Per Hour", s1.cellsPerHour(),   s2.cellsPerHour(),   sd != null ? sd.cellsPerHour() : null);
        sb.append("\n");
    }

    private void appendRecords(StringBuilder sb,
            Map<SectionHeader, Section> m1, Map<SectionHeader, Section> m2, Map<SectionHeader, Section> md) {
        Records s1 = cast(m1, SectionHeader.RECORDS, Records.class);
        Records s2 = cast(m2, SectionHeader.RECORDS, Records.class);
        Records sd = cast(md, SectionHeader.RECORDS, Records.class);
        if (s1 == null || s2 == null) return;

        sb.append("--- Records ---\n");
        row(sb, "Highest Coins/Min",        s1.highestCoinsPerMinute(),    s2.highestCoinsPerMinute(),    sd != null ? sd.highestCoinsPerMinute() : null);
        row(sb, "Largest Wave Skip",        s1.largestWaveSkip(),          s2.largestWaveSkip(),          sd != null ? sd.largestWaveSkip() : null);
        row(sb, "Most Coins From Wave Skip",s1.mostCoinsFromWaveSkip(),    s2.mostCoinsFromWaveSkip(),    sd != null ? sd.mostCoinsFromWaveSkip() : null);
        row(sb, "Largest Golden Combo",     s1.largestGoldenCombo(),       s2.largestGoldenCombo(),       sd != null ? sd.largestGoldenCombo() : null);
        sb.append("\n");
    }

    private void appendDamage(StringBuilder sb,
            Map<SectionHeader, Section> m1, Map<SectionHeader, Section> m2, Map<SectionHeader, Section> md) {
        Damage s1 = cast(m1, SectionHeader.DAMAGE, Damage.class);
        Damage s2 = cast(m2, SectionHeader.DAMAGE, Damage.class);
        Damage sd = cast(md, SectionHeader.DAMAGE, Damage.class);
        if (s1 == null || s2 == null) return;

        sb.append("--- Damage ---\n");
        row(sb, "Damage Dealt",    s1.damageDealt(),    s2.damageDealt(),    sd != null ? sd.damageDealt() : null);
        row(sb, "Projectiles",     s1.projectiles(),    s2.projectiles(),    sd != null ? sd.projectiles() : null);
        row(sb, "Orbs",            s1.orbs(),           s2.orbs(),           sd != null ? sd.orbs() : null);
        row(sb, "Chain Lightning", s1.chainLightning(), s2.chainLightning(), sd != null ? sd.chainLightning() : null);
        row(sb, "Thorns",          s1.thorns(),         s2.thorns(),         sd != null ? sd.thorns() : null);
        row(sb, "Land Mines",      s1.landMines(),      s2.landMines(),      sd != null ? sd.landMines() : null);
        row(sb, "Smart Missiles",  s1.smartMissiles(),  s2.smartMissiles(),  sd != null ? sd.smartMissiles() : null);
        row(sb, "Death Wave",      s1.deathWave(),      s2.deathWave(),      sd != null ? sd.deathWave() : null);
        row(sb, "Black Hole",      s1.blackHole(),      s2.blackHole(),      sd != null ? sd.blackHole() : null);
        sb.append("\n");
    }

    private void appendDamageTaken(StringBuilder sb,
            Map<SectionHeader, Section> m1, Map<SectionHeader, Section> m2, Map<SectionHeader, Section> md) {
        DamageTaken s1 = cast(m1, SectionHeader.DAMAGE_TAKEN, DamageTaken.class);
        DamageTaken s2 = cast(m2, SectionHeader.DAMAGE_TAKEN, DamageTaken.class);
        DamageTaken sd = cast(md, SectionHeader.DAMAGE_TAKEN, DamageTaken.class);
        if (s1 == null || s2 == null) return;

        sb.append("--- Damage Taken ---\n");
        row(sb, "Tower", s1.tower(), s2.tower(), sd != null ? sd.tower() : null);
        row(sb, "Wall",  s1.wall(),  s2.wall(),  sd != null ? sd.wall()  : null);
        sb.append("\n");
    }

    private void appendDamageBlocked(StringBuilder sb,
            Map<SectionHeader, Section> m1, Map<SectionHeader, Section> m2, Map<SectionHeader, Section> md) {
        DamageBlocked s1 = cast(m1, SectionHeader.DAMAGE_BLOCKED, DamageBlocked.class);
        DamageBlocked s2 = cast(m2, SectionHeader.DAMAGE_BLOCKED, DamageBlocked.class);
        DamageBlocked sd = cast(md, SectionHeader.DAMAGE_BLOCKED, DamageBlocked.class);
        if (s1 == null || s2 == null) return;

        sb.append("--- Damage Blocked ---\n");
        row(sb, "Defense %",     s1.defensePercent(),     s2.defensePercent(),     sd != null ? sd.defensePercent() : null);
        row(sb, "Chrono Field",  s1.chronoField(),        s2.chronoField(),        sd != null ? sd.chronoField() : null);
        row(sb, "Chain Thunder", s1.chainThunder(),       s2.chainThunder(),       sd != null ? sd.chainThunder() : null);
        sb.append("\n");
    }

    private void appendCoins(StringBuilder sb,
            Map<SectionHeader, Section> m1, Map<SectionHeader, Section> m2, Map<SectionHeader, Section> md) {
        Coins s1 = cast(m1, SectionHeader.COINS, Coins.class);
        Coins s2 = cast(m2, SectionHeader.COINS, Coins.class);
        Coins sd = cast(md, SectionHeader.COINS, Coins.class);
        if (s1 == null || s2 == null) return;

        sb.append("--- Coins ---\n");
        row(sb, "Coins Earned",  s1.coinsEarned(),  s2.coinsEarned(),  sd != null ? sd.coinsEarned() : null);
        row(sb, "Coins/Kill",    s1.coinsPerKill(), s2.coinsPerKill(), sd != null ? sd.coinsPerKill() : null);
        row(sb, "Golden Tower",  s1.goldenTower(),  s2.goldenTower(),  sd != null ? sd.goldenTower() : null);
        row(sb, "Wave Skip",     s1.waveSkip(),     s2.waveSkip(),     sd != null ? sd.waveSkip() : null);
        row(sb, "Death Wave",    s1.deathWave(),    s2.deathWave(),    sd != null ? sd.deathWave() : null);
        sb.append("\n");
    }

    private void appendCurrencies(StringBuilder sb,
            Map<SectionHeader, Section> m1, Map<SectionHeader, Section> m2, Map<SectionHeader, Section> md) {
        Currencies s1 = cast(m1, SectionHeader.CURRENCIES, Currencies.class);
        Currencies s2 = cast(m2, SectionHeader.CURRENCIES, Currencies.class);
        Currencies sd = cast(md, SectionHeader.CURRENCIES, Currencies.class);
        if (s1 == null || s2 == null) return;

        sb.append("--- Currencies ---\n");
        row(sb, "Cells Earned",  s1.cellsEarned(),       s2.cellsEarned(),       sd != null ? sd.cellsEarned() : null);
        row(sb, "Gems",          s1.gems(),               s2.gems(),               sd != null ? sd.gems() : null);
        row(sb, "Medals",        s1.medals(),             s2.medals(),             sd != null ? sd.medals() : null);
        row(sb, "Reroll Shards", s1.reRollShardsEarned(), s2.reRollShardsEarned(), sd != null ? sd.reRollShardsEarned() : null);
        sb.append("\n");
    }

    private void appendEnemiesDestroyedBy(StringBuilder sb,
            Map<SectionHeader, Section> m1, Map<SectionHeader, Section> m2, Map<SectionHeader, Section> md) {
        EnemiesDestroyedBy s1 = cast(m1, SectionHeader.ENEMIES_DESTROYED_BY, EnemiesDestroyedBy.class);
        EnemiesDestroyedBy s2 = cast(m2, SectionHeader.ENEMIES_DESTROYED_BY, EnemiesDestroyedBy.class);
        EnemiesDestroyedBy sd = cast(md, SectionHeader.ENEMIES_DESTROYED_BY, EnemiesDestroyedBy.class);
        if (s1 == null || s2 == null) return;

        sb.append("--- Enemies Destroyed By ---\n");
        row(sb, "Projectiles",    s1.projectiles(),    s2.projectiles(),    sd != null ? sd.projectiles() : null);
        row(sb, "Orbs",           s1.orbs(),           s2.orbs(),           sd != null ? sd.orbs() : null);
        row(sb, "Chain Lightning",s1.chainLightning(), s2.chainLightning(), sd != null ? sd.chainLightning() : null);
        row(sb, "Land Mines",     s1.landMines(),      s2.landMines(),      sd != null ? sd.landMines() : null);
        row(sb, "Smart Missiles", s1.smartMissiles(),  s2.smartMissiles(),  sd != null ? sd.smartMissiles() : null);
        sb.append("\n");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void row(StringBuilder sb, String label, Object v1, Object v2, Object delta) {
        sb.append(String.format("  %-28s R1: %-16s R2: %-16s Delta: %s%n",
                label + ":", fmt(v1), fmt(v2), fmt(delta)));
    }

    private String fmt(Object v) {
        return v == null ? "-" : v.toString();
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Map<SectionHeader, Section> map, SectionHeader header, Class<T> type) {
        Section s = map == null ? null : map.get(header);
        if (s == null) return null;
        return type.isInstance(s) ? (T) s : null;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
