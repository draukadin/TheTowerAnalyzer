package com.pphi.tower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlediagnostics.Observation;
import com.pphi.tower.model.battlehistory.*;
import com.pphi.tower.model.sheets.cards.CardPresetType;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.repository.CurrencySnapshotRepository;
import com.pphi.tower.repository.RelicRepository;
import com.pphi.tower.repository.RunRepository;
import com.pphi.tower.service.context.ComparisonReportContext;
import com.pphi.tower.model.sheets.modules.Preset;
import com.pphi.tower.service.context.*;
import com.pphi.tower.web.dto.ReportSummaryDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContextExportService {

    private static final Logger log = LoggerFactory.getLogger(ContextExportService.class);
    private static final DateTimeFormatter TIMESTAMP_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TowerTrackerFetcherService fetcherService;
    private final RunRepository runRepository;
    private final CurrencySnapshotRepository snapshotRepository;
    private final ObjectMapper objectMapper;
    private final RelicRepository relicRepository;

    public ContextExportService(TowerTrackerFetcherService fetcherService,
                                RunRepository runRepository,
                                CurrencySnapshotRepository snapshotRepository,
                                ObjectMapper objectMapper,
                                RelicRepository relicRepository) {
        this.fetcherService = fetcherService;
        this.runRepository = runRepository;
        this.snapshotRepository = snapshotRepository;
        this.objectMapper = objectMapper;
        this.relicRepository = relicRepository;
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
        sb.append(new ComparisonReportContext(comparisonResult, id1, id2).getContent());
        sb.append("```\n");
        return sb.toString();
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
        var br  = (BattleReport)        sm.get(SectionHeader.BATTLE_REPORT);
        var dmg = (Damage)              sm.get(SectionHeader.DAMAGE);
        var dmgBlk = (DamageBlocked)    sm.get(SectionHeader.DAMAGE_BLOCKED);
        var dmgTkn = (DamageTaken)      sm.get(SectionHeader.DAMAGE_TAKEN);
        var coins = (Coins)             sm.get(SectionHeader.COINS);
        var cash  = (Cash)              sm.get(SectionHeader.CASH);
        var curr  = (Currencies)        sm.get(SectionHeader.CURRENCIES);
        var rec   = (Records)           sm.get(SectionHeader.RECORDS);
        var util  = (Utility)           sm.get(SectionHeader.UTILITY);
        var counts = (Counts)           sm.get(SectionHeader.COUNTS);
        var enemies = (TotalEnemies)    sm.get(SectionHeader.TOTAL_ENEMIES);
        var destroyed = (EnemiesDestroyedBy) sm.get(SectionHeader.ENEMIES_DESTROYED_BY);
        var hp    = (BonusHealthGained) sm.get(SectionHeader.BONUS_HEALTH_GAINED);
        var hregen = (HealthRegenerated) sm.get(SectionHeader.HEALTH_REGENERATED);

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

        // Overview
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

        // Records
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

        // Damage Breakdown
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

        // Kill Sources
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

        // Defense
        if (dmgBlk != null) {
            sb.append("## Damage Blocked\n\n");
            sb.append("| Source | Amount |\n|--------|--------|\n");
            appendIfPositive(sb, "Defense %",             dmgBlk.defensePercent());
            appendIfPositive(sb, "Chrono Field",          dmgBlk.chronoField());
            appendIfPositive(sb, "Chain Thunder",         dmgBlk.chainThunder());
            appendIfPositive(sb, "Flame Bot",             dmgBlk.flameBot());
            appendIfPositive(sb, "Primordial Collapse",   dmgBlk.primordialCollapse());
            appendIfPositive(sb, "Defense Absolute",      dmgBlk.defenseAbsolute());
            appendIfPositive(sb, "Neg. Mass Projector",   dmgBlk.negativeMassProjector());
            sb.append("\n");
        }

        // Coin Sources
        if (coins != null) {
            sb.append("## Coin Sources\n\n");
            sb.append("| Source | Coins |\n|--------|-------|\n");
            appendIfPositive(sb, "Golden Tower",    coins.goldenTower());
            appendIfPositive(sb, "Black Hole",      coins.blackHole());
            appendIfPositive(sb, "Other Bonuses",   coins.otherCoinBonuses());
            appendIfPositive(sb, "Golden Bot",      coins.goldenBot());
            appendIfPositive(sb, "Death Wave",      coins.deathWave());
            appendIfPositive(sb, "Spotlight",       coins.spotlight());
            appendIfPositive(sb, "Critical Coin",   coins.criticalCoin());
            appendIfPositive(sb, "Wave Skip",       coins.waveSkip());
            appendIfPositive(sb, "Coins Fetched",   coins.coinsFetched());
            appendIfPositive(sb, "Bounty Coins",    coins.bountyCoins());
            sb.append("\n");
        }

        // Survival
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

        // Enemy Composition
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

        // Utility & Counts
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

        // Currencies
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

    public Path exportToDocuments() throws IOException {
        Path outputDir = Path.of(System.getProperty("user.home"), "Documents", "TowerAnalyzer");
        Files.createDirectories(outputDir);

        String timestamp = LocalDateTime.now().format(TIMESTAMP_FMT);
        for (ChatContext ctx : fetchAllContexts()) {
            String fileName = ctx.getLabel().toLowerCase()
                    .replaceAll("[\\\\/:*?\"<>|]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-{2,}", "-") + ".md";
            StringBuilder sb = new StringBuilder();
            sb.append("# ").append(ctx.getLabel()).append("\n\n");
            sb.append("_Generated: ").append(timestamp).append("_\n\n");
            sb.append("---\n\n");
            sb.append(ctx.getContent()).append("\n");
            Files.writeString(outputDir.resolve(fileName), sb.toString());
        }

        return outputDir;
    }

    private List<ChatContext> fetchAllContexts() throws IOException {
        List<ChatContext> contexts = new ArrayList<>();

        log.info("Fetching player currencies...");
        com.pphi.tower.model.sheets.Currencies currencies = snapshotRepository.findLatest()
                .orElseGet(() -> {
                    try { return fetcherService.fetchCurrencies(); } catch (IOException e) { throw new RuntimeException(e); }
                });
        contexts.add(new PlayerCurrenciesContext(currencies));

        log.info("Fetching labs...");
        contexts.add(new LabsContext(fetcherService.fetchLabs()));

        log.info("Fetching Lab Slot planning...");
        contexts.add(new LabPlanningContext(fetcherService.fetchLabPlanning()));

        log.info("Fetching lab tier list...");
        contexts.add(new LabTierListContext(fetcherService.fetchLabTierList()));

        log.info("Fetching ultimate weapons...");
        contexts.add(new UltimateWeaponsContext(fetcherService.fetchUltimateWeapons()));

        log.info("Fetching module inventory...");
        contexts.add(new ModulesContext(fetcherService.fetchModuleInventory(), fetcherService.fetchModuleSubStats()));

        log.info("Fetching farming module preset...");
        contexts.add(new ModulePresetContext(Preset.FARMING, fetcherService.fetchModulePreset(Preset.FARMING)));

        log.info("Fetching tournament module preset...");
        contexts.add(new ModulePresetContext(Preset.TOURNAMENT, fetcherService.fetchModulePreset(Preset.TOURNAMENT)));

        log.info("Fetching cards...");
        contexts.add(new CardsContext(fetcherService.fetchCards()));

        log.info("Fetching farming card preset...");
        contexts.add(new CardPresetContext(CardPresetType.FARMING, fetcherService.fetchCardPreset(CardPresetType.FARMING)));

        log.info("Fetching tournament card preset...");
        contexts.add(new CardPresetContext(CardPresetType.TOURNAMENT, fetcherService.fetchCardPreset(CardPresetType.TOURNAMENT)));

        log.info("Fetching workshop...");
        contexts.add(new WorkshopContext(fetcherService.fetchWorkshop()));

        log.info("Fetching guardians...");
        contexts.add(new GuardiansContext(fetcherService.fetchGuardians()));

        log.info("Fetching bots...");
        contexts.add(new BotsContext(fetcherService.fetchBots()));

        log.info("Fetching relics...");
        contexts.add(new RelicsContext(relicRepository.toMarkdownContext()));

        log.info("Fetching tier/wave info...");
        contexts.add(new TierWaveContext(fetcherService.fetchTierWave()));

        log.info("Fetching version history...");
        contexts.add(new VersionHistoryContext(fetcherService.fetchVersionHistory()));

        // ── Snapshot persistence ──────────────────────────────────────────────
        // Runs after all contexts are populated so currency and module data are
        // captured together under the same snapshot_time.
        // Wrapped in try/catch: persistence failure must never break the export.
        try {
            LocalDateTime snapshotTime = LocalDateTime.now();
            contexts.forEach(c -> {
                if (c instanceof PlayerCurrenciesContext cc)
                    snapshotRepository.saveCurrencySnapshot(snapshotTime, cc.getCurrencies());
                if (c instanceof ModulesContext mc)
                    snapshotRepository.saveModuleLevelSnapshots(snapshotTime, mc.getModules());
            });
            log.info("Currency snapshot saved at {}", snapshotTime);
        } catch (Exception e) {
            log.warn("Snapshot persistence failed (non-fatal): {}", e.getMessage());
        }

        return contexts;
    }
}
