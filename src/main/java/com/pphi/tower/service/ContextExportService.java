package com.pphi.tower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlediagnostics.Observation;
import com.pphi.tower.model.battlehistory.*;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.repository.RunRepository;
import com.pphi.tower.web.dto.ReportSummaryDto;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class ContextExportService {

    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RunRepository runRepository;
    private final ObjectMapper objectMapper;

    public ContextExportService(RunRepository runRepository, ObjectMapper objectMapper) {
        this.runRepository = runRepository;
        this.objectMapper = objectMapper;
    }

    public Path exportDiagnosisToDocuments(DiagnosisResult result, String reportName) throws IOException {
        Path outputDir = Path.of(System.getProperty("user.home"), "Documents", "TowerAnalyzer");
        Files.createDirectories(outputDir);

        var summary = runRepository.findSummaryById(reportName);
        String baseName = summary
                .map(ContextExportService::diagnosisFileName)
                .orElseGet(() -> {
                    String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
                    return (reportName.isBlank() ? "diagnosis" : reportName) + "-" + ts;
                });
        Path outputFile = outputDir.resolve(baseName + ".md");
        Files.writeString(outputFile, buildDiagnosisMarkdown(result, reportName, summary.orElse(null)));
        return outputFile;
    }

    private String buildDiagnosisMarkdown(DiagnosisResult result, String reportName, ReportSummaryDto summary) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        StringBuilder sb = new StringBuilder();
        sb.append("# The Tower — Battle Run Diagnosis\n\n");
        sb.append("_Generated: ").append(timestamp).append("_  \n");
        if (!reportName.isBlank()) {
            sb.append("_Report: ").append(reportName).append("_  \n");
        }
        if (summary != null) {
            if (summary.runType() != null && !summary.runType().isBlank()) {
                String type = Character.toUpperCase(summary.runType().charAt(0)) + summary.runType().substring(1).toLowerCase();
                sb.append("_Run Type: ").append(type).append("_  \n");
            }
            sb.append("_Tier: ").append(summary.tier()).append("_  \n");
            sb.append("_Wave: ").append(summary.wave()).append("_  \n");
            if (summary.towerEra() != null) {
                sb.append("_Version: ").append(summary.towerEra()).append("_\n");
            }
        }
        sb.append("\n---\n\n");

        sb.append("## Primary Failure\n\n");
        sb.append("**").append(result.primaryFailure()).append("**");
        sb.append(" — Confidence: **").append(result.confidence()).append("**\n\n");
        sb.append(result.explanation()).append("\n\n");

        sb.append("---\n\n");
        sb.append("## Diagnostic Metrics\n\n");
        sb.append("| Metric | Value |\n");
        sb.append("|--------|-------|\n");
        sb.append("| Swarm Kill Share   | ").append(result.swarmKillShareFormatted()).append(" |\n");
        sb.append("| Heavy Kill Share   | ").append(result.heavyKillShareFormatted()).append(" |\n");
        sb.append("| Block Efficiency   | ").append(result.blockEfficiencyFormatted()).append(" |\n");
        sb.append("| Vampire Density    | ").append(result.vampireDensityFormatted()).append(" |\n");
        sb.append("| Ranged Density     | ").append(result.rangedDensityFormatted()).append(" |\n");
        sb.append(String.format("| Life Steal (raw)   | %.2f |%n", result.lifeStealRaw()));
        sb.append(String.format("| Total Damage Taken | %.2f |%n", result.totalDamageTakenRaw()));
        sb.append("\n");

        if (result.observations() != null && !result.observations().isEmpty()) {
            sb.append("---\n\n");
            sb.append("## Observations\n\n");
            for (Observation obs : result.observations()) {
                sb.append("### ").append(obs.label()).append("\n\n");
                sb.append(obs.detail()).append("\n\n");
            }
        }

        return sb.toString();
    }

    private static String diagnosisFileName(ReportSummaryDto dto) {
        return "Diagnosis_" + runSegment(dto);
    }

    private static String runSegment(ReportSummaryDto dto) {
        String type = dto.runType() == null || dto.runType().isBlank() ? "Unknown"
                : Character.toUpperCase(dto.runType().charAt(0)) + dto.runType().substring(1).toLowerCase();
        return type + "_T" + dto.tier() + "_W" + dto.wave() + "_" + dto.id();
    }

    public Path exportComparisonToDocuments(List<BattleHistory> comparisonResult, String id1, String id2) throws IOException {
        Path outputDir = Path.of(System.getProperty("user.home"), "Documents", "TowerAnalyzer");
        Files.createDirectories(outputDir);

        var s1 = runRepository.findSummaryById(id1);
        var s2 = runRepository.findSummaryById(id2);
        String seg1 = s1.map(ContextExportService::runSegment).orElse(id1);
        String seg2 = s2.map(ContextExportService::runSegment).orElse(id2);
        Path outputFile = outputDir.resolve("Comparison_" + seg1 + "_vs_" + seg2 + ".md");
        Files.writeString(outputFile, buildComparisonMarkdown(comparisonResult, id1, id2));
        return outputFile;
    }

    private String buildComparisonMarkdown(List<BattleHistory> comparisonResult, String id1, String id2) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        StringBuilder sb = new StringBuilder();
        sb.append("# The Tower — Battle Run Comparison\n\n");
        sb.append("_Generated: ").append(timestamp).append("_  \n");
        sb.append("_Report 1: ").append(id1).append("_  \n");
        sb.append("_Report 2: ").append(id2).append("_\n\n");
        sb.append("---\n\n");
        sb.append("```\n");
        sb.append(formatComparison(comparisonResult, id1, id2));
        sb.append("```\n");
        return sb.toString();
    }

    private String formatComparison(List<BattleHistory> comparisonResult, String id1, String id2) {
        BattleHistory r1 = comparisonResult.get(0);
        BattleHistory r2 = comparisonResult.get(1);
        BattleHistory delta = comparisonResult.get(2);

        Map<SectionHeader, Section> m1 = r1.sectionMap();
        Map<SectionHeader, Section> m2 = r2.sectionMap();
        Map<SectionHeader, Section> md = delta.sectionMap();

        StringBuilder sb = new StringBuilder();
        sb.append("=== The Tower Battle Run Comparison ===\n\n");
        sb.append("Report 1 ID: ").append(id1).append("\n");
        sb.append("Report 2 ID: ").append(id2).append("\n\n");

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
        row(sb, "Highest Coins/Min",         s1.highestCoinsPerMinute(),    s2.highestCoinsPerMinute(),    sd != null ? sd.highestCoinsPerMinute() : null);
        row(sb, "Largest Wave Skip",         s1.largestWaveSkip(),          s2.largestWaveSkip(),          sd != null ? sd.largestWaveSkip() : null);
        row(sb, "Most Coins From Wave Skip", s1.mostCoinsFromWaveSkip(),    s2.mostCoinsFromWaveSkip(),    sd != null ? sd.mostCoinsFromWaveSkip() : null);
        row(sb, "Largest Golden Combo",      s1.largestGoldenCombo(),       s2.largestGoldenCombo(),       sd != null ? sd.largestGoldenCombo() : null);
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
        row(sb, "Projectiles",     s1.projectiles(),    s2.projectiles(),    sd != null ? sd.projectiles() : null);
        row(sb, "Orbs",            s1.orbs(),           s2.orbs(),           sd != null ? sd.orbs() : null);
        row(sb, "Chain Lightning", s1.chainLightning(), s2.chainLightning(), sd != null ? sd.chainLightning() : null);
        row(sb, "Land Mines",      s1.landMines(),      s2.landMines(),      sd != null ? sd.landMines() : null);
        row(sb, "Smart Missiles",  s1.smartMissiles(),  s2.smartMissiles(),  sd != null ? sd.smartMissiles() : null);
        sb.append("\n");
    }

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

    public Path exportStatsToDocuments(String id) throws IOException {
        String payload = runRepository.findPayloadById(id)
                .orElseThrow(() -> new RuntimeException("Report not found: " + id));
        BattleHistory history;
        try {
            history = objectMapper.readValue(payload, BattleHistory.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize report " + id, e);
        }
        var summary = runRepository.findSummaryById(id);

        Path outputDir = Path.of(System.getProperty("user.home"), "Documents", "TowerAnalyzer");
        Files.createDirectories(outputDir);

        String baseName = summary
                .map(s -> "Stats_" + runSegment(s))
                .orElseGet(() -> {
                    String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
                    return "Stats_" + id + "_" + ts;
                });
        Path outputFile = outputDir.resolve(baseName + ".md");
        Files.writeString(outputFile, buildStatsMarkdown(history, id, summary.orElse(null)));
        return outputFile;
    }

    private String buildStatsMarkdown(BattleHistory history, String id, ReportSummaryDto summary) {
        var sm = history.sectionMap();
        var br      = (BattleReport)        sm.get(SectionHeader.BATTLE_REPORT);
        var dmg     = (Damage)              sm.get(SectionHeader.DAMAGE);
        var dmgBlk  = (DamageBlocked)       sm.get(SectionHeader.DAMAGE_BLOCKED);
        var dmgTkn  = (DamageTaken)         sm.get(SectionHeader.DAMAGE_TAKEN);
        var coins   = (Coins)               sm.get(SectionHeader.COINS);
        var cash    = (Cash)                sm.get(SectionHeader.CASH);
        var curr    = (Currencies)          sm.get(SectionHeader.CURRENCIES);
        var rec     = (Records)             sm.get(SectionHeader.RECORDS);
        var util    = (Utility)             sm.get(SectionHeader.UTILITY);
        var counts  = (Counts)              sm.get(SectionHeader.COUNTS);
        var enemies = (TotalEnemies)        sm.get(SectionHeader.TOTAL_ENEMIES);
        var destroyed = (EnemiesDestroyedBy) sm.get(SectionHeader.ENEMIES_DESTROYED_BY);
        var hp      = (BonusHealthGained)   sm.get(SectionHeader.BONUS_HEALTH_GAINED);
        var hregen  = (HealthRegenerated)   sm.get(SectionHeader.HEALTH_REGENERATED);

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        StringBuilder sb = new StringBuilder();

        String towerVersion = br != null && br.towerEra() != null ? br.towerEra().toString() : "Unknown";
        sb.append("# The Tower — Battle Stats\n\n");
        sb.append("_Tower Version: ").append(towerVersion).append("_  \n");
        sb.append("_Generated: ").append(timestamp).append("_  \n");
        if (summary != null) {
            String type = summary.runType() == null || summary.runType().isBlank() ? "Unknown"
                    : Character.toUpperCase(summary.runType().charAt(0)) + summary.runType().substring(1).toLowerCase();
            sb.append("_Run Type: ").append(type).append("_  \n");
            sb.append("_Tier: ").append(summary.tier()).append(" | Wave: ").append(String.format("%,d", summary.wave())).append("_  \n");
            sb.append("_Date: ").append(summary.battleDate()).append("_  \n");
            sb.append("_Killed By: ").append(summary.killedBy() != null ? summary.killedBy() : "Unknown").append("_  \n");
        }
        sb.append("\n---\n\n");

        sb.append("## Overview\n\n");
        sb.append("| Stat | Value |\n|------|-------|\n");
        if (br != null) {
            sb.append("| Coins Earned | ").append(disp(br.coinsEarned())).append(" |\n");
            sb.append("| Coins / Hour | ").append(disp(br.coinsPerHour())).append(" |\n");
            sb.append("| Cells Earned | ").append(disp(br.cellsEarned())).append(" |\n");
            sb.append("| Cells / Hour | ").append(disp(br.cellsPerHour())).append(" |\n");
        }
        if (dmg != null) sb.append("| Damage Dealt | ").append(disp(dmg.damageDealt())).append(" |\n");
        if (enemies != null) sb.append("| Total Enemies | ").append(String.format("%,d", enemies.totalEnemies())).append(" |\n");
        if (cash != null) sb.append("| Cash Earned | ").append(disp(cash.cashEarned())).append(" |\n");
        sb.append("\n");

        if (rec != null) {
            sb.append("## Records\n\n");
            sb.append("| Record | Value |\n|--------|-------|\n");
            sb.append("| Highest Coins/Min | ").append(disp(rec.highestCoinsPerMinute())).append(" |\n");
            sb.append("| Largest Wave Skip | ").append(rec.largestWaveSkip()).append(" |\n");
            sb.append("| Most Coins from Skip | ").append(disp(rec.mostCoinsFromWaveSkip())).append(" |\n");
            sb.append("| Largest Missile Stack | ").append(rec.largestSmartMissileStack()).append(" |\n");
            sb.append("| Largest Golden Combo | ").append(rec.largestGoldenCombo()).append(" |\n");
            sb.append("| Largest Inner Mine Charge | ").append(disp(rec.largestInnerLandmineCharge())).append(" |\n");
            sb.append("\n");
        }

        if (dmg != null) {
            sb.append("## Damage Breakdown\n\n");
            sb.append("| Source | Damage |\n|--------|--------|\n");
            appendIfPositive(sb, "Orbs",             dmg.orbs());
            appendIfPositive(sb, "Chain Lightning",  dmg.chainLightning());
            appendIfPositive(sb, "Thorns",           dmg.thorns());
            appendIfPositive(sb, "Black Hole",       dmg.blackHole());
            appendIfPositive(sb, "Projectiles",      dmg.projectiles());
            appendIfPositive(sb, "Land Mines",       dmg.landMines());
            appendIfPositive(sb, "Smart Missiles",   dmg.smartMissiles());
            appendIfPositive(sb, "Flame Bot",        dmg.flameBot());
            appendIfPositive(sb, "Inner Land Mines", dmg.innerLandMines());
            appendIfPositive(sb, "Death Wave",       dmg.deathWave());
            appendIfPositive(sb, "Rend Armor",       dmg.rendArmor());
            appendIfPositive(sb, "Death Ray",        dmg.deathRay());
            appendIfPositive(sb, "Attack Chip",      dmg.attackChip());
            appendIfPositive(sb, "Electrons",        dmg.electrons());
            appendIfPositive(sb, "Poison Swamp",     dmg.poisonSwamp());
            sb.append("\n");
        }

        if (destroyed != null) {
            sb.append("## Kill Sources\n\n");
            sb.append("| Source | Kills |\n|--------|-------|\n");
            appendLongIfPositive(sb, "Orbs",             destroyed.orbs());
            appendLongIfPositive(sb, "Chain Lightning",  destroyed.chainLightning());
            appendLongIfPositive(sb, "Land Mines",       destroyed.landMines());
            appendLongIfPositive(sb, "Projectiles",      destroyed.projectiles());
            appendLongIfPositive(sb, "Smart Missiles",   destroyed.smartMissiles());
            appendLongIfPositive(sb, "Flame Bot",        destroyed.flameBot());
            appendLongIfPositive(sb, "Black Hole",       destroyed.blackHole());
            appendLongIfPositive(sb, "Thorns",           destroyed.thorns());
            appendLongIfPositive(sb, "Inner Land Mines", destroyed.innerLandMines());
            appendLongIfPositive(sb, "Death Ray",        destroyed.deathRay());
            appendLongIfPositive(sb, "Poison Swamp",     destroyed.poisonSwamp());
            appendLongIfPositive(sb, "Other",            destroyed.other());
            sb.append("\n");
        }

        if (dmgBlk != null) {
            sb.append("## Damage Blocked\n\n");
            sb.append("| Source | Amount |\n|--------|--------|\n");
            appendIfPositive(sb, "Defense %",           dmgBlk.defensePercent());
            appendIfPositive(sb, "Chrono Field",        dmgBlk.chronoField());
            appendIfPositive(sb, "Chain Thunder",       dmgBlk.chainThunder());
            appendIfPositive(sb, "Flame Bot",           dmgBlk.flameBot());
            appendIfPositive(sb, "Primordial Collapse", dmgBlk.primordialCollapse());
            appendIfPositive(sb, "Defense Absolute",    dmgBlk.defenseAbsolute());
            appendIfPositive(sb, "Neg. Mass Projector", dmgBlk.negativeMassProjector());
            sb.append("\n");
        }

        if (coins != null) {
            sb.append("## Coin Sources\n\n");
            sb.append("| Source | Coins |\n|--------|-------|\n");
            appendIfPositive(sb, "Golden Tower",  coins.goldenTower());
            appendIfPositive(sb, "Black Hole",    coins.blackHole());
            appendIfPositive(sb, "Other Bonuses", coins.otherCoinBonuses());
            appendIfPositive(sb, "Golden Bot",    coins.goldenBot());
            appendIfPositive(sb, "Death Wave",    coins.deathWave());
            appendIfPositive(sb, "Spotlight",     coins.spotlight());
            appendIfPositive(sb, "Critical Coin", coins.criticalCoin());
            appendIfPositive(sb, "Wave Skip",     coins.waveSkip());
            appendIfPositive(sb, "Coins Fetched", coins.coinsFetched());
            appendIfPositive(sb, "Bounty Coins",  coins.bountyCoins());
            sb.append("\n");
        }

        sb.append("## Survival\n\n");
        sb.append("| Stat | Value |\n|------|-------|\n");
        if (hregen != null) {
            sb.append("| Lifesteal | ").append(disp(hregen.lifeSteal())).append(" |\n");
            sb.append("| Wall Health Regen | ").append(disp(hregen.wallHealthRegen())).append(" |\n");
            sb.append("| Tower Health Regen | ").append(disp(hregen.towerHealthRegen())).append(" |\n");
        }
        if (hp != null) sb.append("| Bonus HP (Death Wave) | ").append(disp(hp.fromDeathWave())).append(" |\n");
        if (dmgTkn != null) {
            sb.append("| Tower Damage Taken | ").append(disp(dmgTkn.tower())).append(" |\n");
            sb.append("| Wall Damage Taken | ").append(disp(dmgTkn.wall())).append(" |\n");
        }
        sb.append("\n");

        if (enemies != null) {
            sb.append("## Enemy Composition\n\n");
            sb.append("| Type | Count |\n|------|-------|\n");
            appendLongIfPositive(sb, "Basic",      enemies.basic());
            appendLongIfPositive(sb, "Fast",       enemies.fast());
            appendLongIfPositive(sb, "Tank",       enemies.tank());
            appendLongIfPositive(sb, "Ranged",     enemies.ranged());
            appendLongIfPositive(sb, "Boss",       enemies.boss());
            appendLongIfPositive(sb, "Protector",  enemies.protector());
            appendLongIfPositive(sb, "Vampires",   enemies.vampires());
            appendLongIfPositive(sb, "Rays",       enemies.rays());
            appendLongIfPositive(sb, "Scatters",   enemies.scatters());
            appendLongIfPositive(sb, "Saboteur",   enemies.saboteur());
            appendLongIfPositive(sb, "Commander",  enemies.commander());
            appendLongIfPositive(sb, "Overcharge", enemies.overcharge());
            sb.append("\n");
        }

        if (util != null || counts != null) {
            sb.append("## Utility & Counts\n\n");
            sb.append("| Stat | Value |\n|------|-------|\n");
            if (util != null) {
                sb.append("| Recovery Packages | ").append(util.recoveryPackages()).append(" |\n");
                sb.append("| Free Atk Upgrades | ").append(util.freeAttackUpgrades()).append(" |\n");
                sb.append("| Free Def Upgrades | ").append(util.freeDefenseUpgrades()).append(" |\n");
                sb.append("| Free Util Upgrades | ").append(util.freeUtilityUpgrades()).append(" |\n");
                sb.append("| Enemy Atk Lvls Skipped | ").append(util.enemyAttackLevelSkipped()).append(" |\n");
                sb.append("| Enemy HP Lvls Skipped | ").append(util.enemyHealthLevelSkipped()).append(" |\n");
            }
            if (counts != null) {
                sb.append("| Waves Skipped | ").append(counts.wavesSkipped()).append(" |\n");
                sb.append("| Land Mines Spawned | ").append(String.format("%,d", counts.landMinesSpawned())).append(" |\n");
            }
            sb.append("\n");
        }

        if (curr != null) {
            sb.append("## Currencies & Shards\n\n");
            sb.append("| Currency | Amount |\n|----------|--------|\n");
            sb.append("| Gems | ").append(curr.gems()).append(" |\n");
            sb.append("| Ad Gems | ").append(curr.adGems()).append(" |\n");
            sb.append("| Fetch Gems | ").append(curr.fetchGems()).append(" |\n");
            sb.append("| Medals | ").append(curr.medals()).append(" |\n");
            sb.append("| Reroll Shards | ").append(disp(curr.reRollShardsEarned())).append(" |\n");
            sb.append("| Cannon Shards | ").append(curr.cannonShards()).append(" |\n");
            sb.append("| Armor Shards | ").append(curr.armorShards()).append(" |\n");
            sb.append("| Common Modules | ").append(curr.commonModules()).append(" |\n");
            sb.append("| Rare Modules | ").append(curr.rareModules()).append(" |\n");
            sb.append("| Generator Shards | ").append(curr.generatorShards()).append(" |\n");
            sb.append("| Core Shards | ").append(curr.coreShards()).append(" |\n");
        }

        return sb.toString();
    }

    private static String disp(TowerNumber n) {
        return n != null ? n.toString() : "—";
    }

    private static void appendIfPositive(StringBuilder sb, String label, TowerNumber n) {
        if (n == null) return;
        double raw = n.scaleSuffix() != null
                ? n.amount().multiply(n.scaleSuffix().getScientificNotation()).doubleValue()
                : n.amount().doubleValue();
        if (raw > 0) sb.append("| ").append(label).append(" | ").append(n).append(" |\n");
    }

    private static void appendLongIfPositive(StringBuilder sb, String label, long v) {
        if (v > 0) sb.append("| ").append(label).append(" | ").append(String.format("%,d", v)).append(" |\n");
    }
}
