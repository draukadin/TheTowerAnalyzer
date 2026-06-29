package com.pphi.tower.reporter;

import com.pphi.tower.model.battlehistory.*;

import java.util.List;

public class BattleComparisonReporter implements Reporter {

    // ANSI color codes
    private static final String GREEN = "\u001B[32m";
    private static final String RED   = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private static final int COL_LABEL = 40;
    private static final int COL_VAL   = 20;

    // Delta coloring strategies
    private enum ColorStrategy {
        HIGHER_IS_BETTER,  // positive delta = green, negative = red
        LOWER_IS_BETTER,   // positive delta = red,   negative = green
        NEUTRAL            // no color
    }

    public void printReport(List<BattleHistory> battles) {
        BattleHistory report1 = battles.get(0);
        BattleHistory report2 = battles.get(1);
        BattleHistory delta   = battles.get(2);

        printHeader();
        printBattleReport(report1, report2, delta);
        printRecords(report1, report2, delta);
        printDamage(report1, report2, delta);
        printDamageTaken(report1, report2, delta);
        printBonusHealthGained(report1, report2, delta);
        printHealthRegenerated(report1, report2, delta);
        printDamageBlocked(report1, report2, delta);
        printUtility(report1, report2, delta);
        printCounts(report1, report2, delta);
        printEnemiesHitBy(report1, report2, delta);
        printKilledWithEffectActive(report1, report2, delta);
        printTotalEnemies(report1, report2, delta);
        printCoins(report1, report2, delta);
        printCash(report1, report2, delta);
        printCurrencies(report1, report2, delta);
        printEnemiesDestroyedBy(report1, report2, delta);
    }

    // -------------------------------------------------------------------------
    // Sections
    // -------------------------------------------------------------------------

    private void printBattleReport(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        BattleReport s1 = (BattleReport) r1.sectionMap().get(SectionHeader.BATTLE_REPORT);
        BattleReport s2 = (BattleReport) r2.sectionMap().get(SectionHeader.BATTLE_REPORT);
        BattleReport sd = (BattleReport) d.sectionMap().get(SectionHeader.BATTLE_REPORT);

        printSectionHeader("Battle Report");
        printRow("Tower Era",      fmt(s1.towerEra()),      fmt(s2.towerEra()),      fmt(null),         ColorStrategy.NEUTRAL);
        printRow("Battle Date",    fmt(s1.battleReportDate()),    fmt(s2.battleReportDate()),    fmt(sd.battleReportDate()),    ColorStrategy.NEUTRAL);
        printRow("Game Time",      fmt(s1.gameTime()),      fmt(s2.gameTime()),      fmt(sd.gameTime()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Real Time",      fmt(s1.realTime()),      fmt(s2.realTime()),      fmt(sd.realTime()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Tier",           fmt(s1.tier()),          fmt(s2.tier()),          fmt(sd.tier()),          ColorStrategy.HIGHER_IS_BETTER);
        printRow("Wave",           fmt(s1.wave()),          fmt(s2.wave()),          fmt(sd.wave()),          ColorStrategy.HIGHER_IS_BETTER);
        printRow("Killed By",      fmt(s1.killedBy()),      fmt(s2.killedBy()),      fmt(sd.killedBy()),      ColorStrategy.NEUTRAL);
        printRow("Coins Earned",   fmt(s1.coinsEarned()),   fmt(s2.coinsEarned()),   fmt(sd.coinsEarned()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Coins Per Hour", fmt(s1.coinsPerHour()),  fmt(s2.coinsPerHour()),  fmt(sd.coinsPerHour()),  ColorStrategy.HIGHER_IS_BETTER);
        printRow("Cells Earned",   fmt(s1.cellsEarned()),   fmt(s2.cellsEarned()),   fmt(sd.cellsEarned()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Cells Per Hour", fmt(s1.cellsPerHour()),  fmt(s2.cellsPerHour()),  fmt(sd.cellsPerHour()),  ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printRecords(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        Records s1 = (Records) r1.sectionMap().get(SectionHeader.RECORDS);
        Records s2 = (Records) r2.sectionMap().get(SectionHeader.RECORDS);
        Records sd = (Records) d.sectionMap().get(SectionHeader.RECORDS);

        printSectionHeader("Records");
        printRow("Highest Coins / Minute",      fmt(s1.highestCoinsPerMinute()),    fmt(s2.highestCoinsPerMinute()),    fmt(sd.highestCoinsPerMinute()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Largest Wave Skip",           fmt(s1.largestWaveSkip()),          fmt(s2.largestWaveSkip()),          fmt(sd.largestWaveSkip()),          ColorStrategy.HIGHER_IS_BETTER);
        printRow("Most Coins From Wave Skip",   fmt(s1.mostCoinsFromWaveSkip()),    fmt(s2.mostCoinsFromWaveSkip()),    fmt(sd.mostCoinsFromWaveSkip()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Most Cells From Wave Skip",   fmt(s1.mostCellsFromWaveSkip()),    fmt(s2.mostCellsFromWaveSkip()),    fmt(sd.mostCellsFromWaveSkip()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Largest Smart Missile Stack", fmt(s1.largestSmartMissileStack()), fmt(s2.largestSmartMissileStack()), fmt(sd.largestSmartMissileStack()), ColorStrategy.HIGHER_IS_BETTER);
        printRow("Largest Golden Combo",        fmt(s1.largestGoldenCombo()),       fmt(s2.largestGoldenCombo()),       fmt(sd.largestGoldenCombo()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Most Coins From Golden Combo",fmt(s1.mostCoinsFromGoldenCombo()), fmt(s2.mostCoinsFromGoldenCombo()), fmt(sd.mostCoinsFromGoldenCombo()), ColorStrategy.HIGHER_IS_BETTER);
        printRow("Largest Inner Landmine Charge",fmt(s1.largestInnerLandmineCharge()),fmt(s2.largestInnerLandmineCharge()),fmt(sd.largestInnerLandmineCharge()),ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printDamage(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        Damage s1 = (Damage) r1.sectionMap().get(SectionHeader.DAMAGE);
        Damage s2 = (Damage) r2.sectionMap().get(SectionHeader.DAMAGE);
        Damage sd = (Damage) d.sectionMap().get(SectionHeader.DAMAGE);

        printSectionHeader("Damage");
        printRow("Damage Dealt",     fmt(s1.damageDealt()),     fmt(s2.damageDealt()),     fmt(sd.damageDealt()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Projectiles",      fmt(s1.projectiles()),     fmt(s2.projectiles()),     fmt(sd.projectiles()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Rend Armor",       fmt(s1.rendArmor()),       fmt(s2.rendArmor()),       fmt(sd.rendArmor()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Death Ray",        fmt(s1.deathRay()),        fmt(s2.deathRay()),        fmt(sd.deathRay()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Thorns",           fmt(s1.thorns()),          fmt(s2.thorns()),          fmt(sd.thorns()),          ColorStrategy.HIGHER_IS_BETTER);
        printRow("Orbs",             fmt(s1.orbs()),            fmt(s2.orbs()),            fmt(sd.orbs()),            ColorStrategy.HIGHER_IS_BETTER);
        printRow("Land Mines",       fmt(s1.landMines()),       fmt(s2.landMines()),       fmt(sd.landMines()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Chain Lightning",  fmt(s1.chainLightning()),  fmt(s2.chainLightning()),  fmt(sd.chainLightning()),  ColorStrategy.HIGHER_IS_BETTER);
        printRow("Smart Missiles",   fmt(s1.smartMissiles()),   fmt(s2.smartMissiles()),   fmt(sd.smartMissiles()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Inner Land Mines", fmt(s1.innerLandMines()),  fmt(s2.innerLandMines()),  fmt(sd.innerLandMines()),  ColorStrategy.HIGHER_IS_BETTER);
        printRow("Poison Swamp",     fmt(s1.poisonSwamp()),     fmt(s2.poisonSwamp()),     fmt(sd.poisonSwamp()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Death Wave",       fmt(s1.deathWave()),       fmt(s2.deathWave()),       fmt(sd.deathWave()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Black Hole",       fmt(s1.blackHole()),       fmt(s2.blackHole()),       fmt(sd.blackHole()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Flame Bot",        fmt(s1.flameBot()),        fmt(s2.flameBot()),        fmt(sd.flameBot()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Attack Chip",      fmt(s1.attackChip()),      fmt(s2.attackChip()),      fmt(sd.attackChip()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Electrons",        fmt(s1.electrons()),       fmt(s2.electrons()),       fmt(sd.electrons()),       ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printDamageTaken(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        DamageTaken s1 = (DamageTaken) r1.sectionMap().get(SectionHeader.DAMAGE_TAKEN);
        DamageTaken s2 = (DamageTaken) r2.sectionMap().get(SectionHeader.DAMAGE_TAKEN);
        DamageTaken sd = (DamageTaken) d.sectionMap().get(SectionHeader.DAMAGE_TAKEN);

        printSectionHeader("Damage Taken");
        printRow("Tower", fmt(s1.tower()), fmt(s2.tower()), fmt(sd.tower()), ColorStrategy.LOWER_IS_BETTER);
        printRow("Wall",  fmt(s1.wall()),  fmt(s2.wall()),  fmt(sd.wall()),  ColorStrategy.LOWER_IS_BETTER);
    }

    private void printBonusHealthGained(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        BonusHealthGained s1 = (BonusHealthGained) r1.sectionMap().get(SectionHeader.BONUS_HEALTH_GAINED);
        BonusHealthGained s2 = (BonusHealthGained) r2.sectionMap().get(SectionHeader.BONUS_HEALTH_GAINED);
        BonusHealthGained sd = (BonusHealthGained) d.sectionMap().get(SectionHeader.BONUS_HEALTH_GAINED);

        printSectionHeader("Bonus Health Gained");
        printRow("From Death Wave", fmt(s1.fromDeathWave()), fmt(s2.fromDeathWave()), fmt(sd.fromDeathWave()), ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printHealthRegenerated(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        HealthRegenerated s1 = (HealthRegenerated) r1.sectionMap().get(SectionHeader.HEALTH_REGENERATED);
        HealthRegenerated s2 = (HealthRegenerated) r2.sectionMap().get(SectionHeader.HEALTH_REGENERATED);
        HealthRegenerated sd = (HealthRegenerated) d.sectionMap().get(SectionHeader.HEALTH_REGENERATED);

        printSectionHeader("Health Regenerated");
        printRow("Lifesteal",        fmt(s1.lifeSteal()),        fmt(s2.lifeSteal()),        fmt(sd.lifeSteal()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Tower Health Regen", fmt(s1.towerHealthRegen()), fmt(s2.towerHealthRegen()), fmt(sd.towerHealthRegen()), ColorStrategy.HIGHER_IS_BETTER);
        printRow("Wall Health Regen",  fmt(s1.wallHealthRegen()),  fmt(s2.wallHealthRegen()),  fmt(sd.wallHealthRegen()),  ColorStrategy.HIGHER_IS_BETTER);
        printRow("Recovery Packages",  fmt(s1.recoveryPackages()), fmt(s2.recoveryPackages()), fmt(sd.recoveryPackages()), ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printDamageBlocked(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        DamageBlocked s1 = (DamageBlocked) r1.sectionMap().get(SectionHeader.DAMAGE_BLOCKED);
        DamageBlocked s2 = (DamageBlocked) r2.sectionMap().get(SectionHeader.DAMAGE_BLOCKED);
        DamageBlocked sd = (DamageBlocked) d.sectionMap().get(SectionHeader.DAMAGE_BLOCKED);

        printSectionHeader("Damage Blocked");
        printRow("Defense %",               fmt(s1.defensePercent()),        fmt(s2.defensePercent()),        fmt(sd.defensePercent()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Defense Absolute",        fmt(s1.defenseAbsolute()),       fmt(s2.defenseAbsolute()),       fmt(sd.defenseAbsolute()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Chrono Field",            fmt(s1.chronoField()),           fmt(s2.chronoField()),           fmt(sd.chronoField()),           ColorStrategy.HIGHER_IS_BETTER);
        printRow("Chain Thunder",           fmt(s1.chainThunder()),          fmt(s2.chainThunder()),          fmt(sd.chainThunder()),          ColorStrategy.HIGHER_IS_BETTER);
        printRow("Flame Bot",               fmt(s1.flameBot()),              fmt(s2.flameBot()),              fmt(sd.flameBot()),              ColorStrategy.HIGHER_IS_BETTER);
        printRow("Primordial Collapse",     fmt(s1.primordialCollapse()),    fmt(s2.primordialCollapse()),    fmt(sd.primordialCollapse()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Negative Mass Projector", fmt(s1.negativeMassProjector()), fmt(s2.negativeMassProjector()), fmt(sd.negativeMassProjector()), ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printUtility(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        Utility s1 = (Utility) r1.sectionMap().get(SectionHeader.UTILITY);
        Utility s2 = (Utility) r2.sectionMap().get(SectionHeader.UTILITY);
        Utility sd = (Utility) d.sectionMap().get(SectionHeader.UTILITY);

        printSectionHeader("Utility");
        printRow("Recovery Packages",         fmt(s1.recoveryPackages()),        fmt(s2.recoveryPackages()),        fmt(sd.recoveryPackages()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Free Attack Upgrade",       fmt(s1.freeAttackUpgrades()),      fmt(s2.freeAttackUpgrades()),      fmt(sd.freeAttackUpgrades()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Free Defense Upgrade",      fmt(s1.freeDefenseUpgrades()),     fmt(s2.freeDefenseUpgrades()),     fmt(sd.freeDefenseUpgrades()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Free Utility Upgrade",      fmt(s1.freeUtilityUpgrades()),     fmt(s2.freeUtilityUpgrades()),     fmt(sd.freeUtilityUpgrades()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Enemy Attack Levels Skipped", fmt(s1.enemyAttackLevelSkipped()), fmt(s2.enemyAttackLevelSkipped()), fmt(sd.enemyAttackLevelSkipped()), ColorStrategy.HIGHER_IS_BETTER);
        printRow("Enemy Health Levels Skipped", fmt(s1.enemyHealthLevelSkipped()), fmt(s2.enemyHealthLevelSkipped()), fmt(sd.enemyHealthLevelSkipped()), ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printCounts(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        Counts s1 = (Counts) r1.sectionMap().get(SectionHeader.COUNTS);
        Counts s2 = (Counts) r2.sectionMap().get(SectionHeader.COUNTS);
        Counts sd = (Counts) d.sectionMap().get(SectionHeader.COUNTS);

        printSectionHeader("Counts");
        printRow("Projectiles Count",            fmt(s1.projectileCount()),           fmt(s2.projectileCount()),           fmt(sd.projectileCount()),           ColorStrategy.HIGHER_IS_BETTER);
        printRow("Land Mines Spawned",           fmt(s1.landMinesSpawned()),          fmt(s2.landMinesSpawned()),          fmt(sd.landMinesSpawned()),          ColorStrategy.HIGHER_IS_BETTER);
        printRow("Thunder Bot Stuns",            fmt(s1.thunderBotStuns()),           fmt(s2.thunderBotStuns()),           fmt(sd.thunderBotStuns()),           ColorStrategy.HIGHER_IS_BETTER);
        printRow("Waves Skipped",                fmt(s1.wavesSkipped()),              fmt(s2.wavesSkipped()),              fmt(sd.wavesSkipped()),              ColorStrategy.HIGHER_IS_BETTER);
        printRow("Death Defy",                   fmt(s1.deathDefy()),                 fmt(s2.deathDefy()),                 fmt(sd.deathDefy()),                 ColorStrategy.HIGHER_IS_BETTER);
        printRow("Hits Absorbed By Energy Shield", fmt(s1.hitsAbsorbedByEnergyShield()), fmt(s2.hitsAbsorbedByEnergyShield()), fmt(sd.hitsAbsorbedByEnergyShield()), ColorStrategy.HIGHER_IS_BETTER);
        printRow("Nuke",                         fmt(s1.nuke()),                      fmt(s2.nuke()),                      fmt(sd.nuke()),                      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Second Wind",                  fmt(s1.secondWind()),                fmt(s2.secondWind()),                fmt(sd.secondWind()),                ColorStrategy.HIGHER_IS_BETTER);
        printRow("Demon Mode",                   fmt(s1.demonMode()),                 fmt(s2.demonMode()),                 fmt(sd.demonMode()),                 ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printEnemiesHitBy(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        EnemiesHitBy s1 = (EnemiesHitBy) r1.sectionMap().get(SectionHeader.ENEMIES_HIT_BY);
        EnemiesHitBy s2 = (EnemiesHitBy) r2.sectionMap().get(SectionHeader.ENEMIES_HIT_BY);
        EnemiesHitBy sd = (EnemiesHitBy) d.sectionMap().get(SectionHeader.ENEMIES_HIT_BY);

        printSectionHeader("Enemies Hit By");
        printRow("Projectiles",    fmt(s1.projectiles()),    fmt(s2.projectiles()),    fmt(sd.projectiles()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Thorns",         fmt(s1.thorns()),         fmt(s2.thorns()),         fmt(sd.thorns()),         ColorStrategy.HIGHER_IS_BETTER);
        printRow("Orbs",           fmt(s1.orbs()),           fmt(s2.orbs()),           fmt(sd.orbs()),           ColorStrategy.HIGHER_IS_BETTER);
        printRow("Death Ray",      fmt(s1.deathRay()),       fmt(s2.deathRay()),       fmt(sd.deathRay()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Chain Lightning",fmt(s1.chainLightning()), fmt(s2.chainLightning()), fmt(sd.chainLightning()), ColorStrategy.HIGHER_IS_BETTER);
        printRow("Smart Missiles", fmt(s1.smartMissiles()),  fmt(s2.smartMissiles()),  fmt(sd.smartMissiles()),  ColorStrategy.HIGHER_IS_BETTER);
        printRow("Inner Land Mines",fmt(s1.innerLandMines()),fmt(s2.innerLandMines()), fmt(sd.innerLandMines()), ColorStrategy.HIGHER_IS_BETTER);
        printRow("Poison Swamp",   fmt(s1.poisonSwamp()),    fmt(s2.poisonSwamp()),    fmt(sd.poisonSwamp()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Death Wave",     fmt(s1.deathWave()),      fmt(s2.deathWave()),      fmt(sd.deathWave()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Black Hole",     fmt(s1.blackHole()),      fmt(s2.blackHole()),      fmt(sd.blackHole()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Chrono Field",   fmt(s1.chronoField()),    fmt(s2.chronoField()),    fmt(sd.chronoField()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Land Mines",     fmt(s1.landMines()),      fmt(s2.landMines()),      fmt(sd.landMines()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Thunder Bot",    fmt(s1.thunderBot()),     fmt(s2.thunderBot()),     fmt(sd.thunderBot()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Flame Bot",      fmt(s1.flameBot()),       fmt(s2.flameBot()),       fmt(sd.flameBot()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Attack Chip",    fmt(s1.attackChip()),     fmt(s2.attackChip()),     fmt(sd.attackChip()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Orbital Augment",fmt(s1.orbitalAugment()), fmt(s2.orbitalAugment()), fmt(sd.orbitalAugment()), ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printKilledWithEffectActive(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        KilledWithEffectActive s1 = (KilledWithEffectActive) r1.sectionMap().get(SectionHeader.KILLED_WITH_EFFECT_ACTIVE);
        KilledWithEffectActive s2 = (KilledWithEffectActive) r2.sectionMap().get(SectionHeader.KILLED_WITH_EFFECT_ACTIVE);
        KilledWithEffectActive sd = (KilledWithEffectActive) d.sectionMap().get(SectionHeader.KILLED_WITH_EFFECT_ACTIVE);

        printSectionHeader("Killed With Effect Active");
        printRow("Golden Tower", fmt(s1.goldenTower()), fmt(s2.goldenTower()), fmt(sd.goldenTower()), ColorStrategy.HIGHER_IS_BETTER);
        printRow("Death Wave",   fmt(s1.deathWave()),   fmt(s2.deathWave()),   fmt(sd.deathWave()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Spotlight",    fmt(s1.spotlight()),   fmt(s2.spotlight()),   fmt(sd.spotlight()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Amplify Bot",  fmt(s1.amplifyBot()),  fmt(s2.amplifyBot()),  fmt(sd.amplifyBot()),  ColorStrategy.HIGHER_IS_BETTER);
        printRow("Golden Bot",   fmt(s1.goldenBot()),   fmt(s2.goldenBot()),   fmt(sd.goldenBot()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Death Penalty",fmt(s1.deathPenalty()),fmt(s2.deathPenalty()),fmt(sd.deathPenalty()),ColorStrategy.HIGHER_IS_BETTER);
        printRow("Black Hole",   fmt(s1.blackHole()),   fmt(s2.blackHole()),   fmt(sd.blackHole()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Orbs",         fmt(s1.orbs()),        fmt(s2.orbs()),        fmt(sd.orbs()),        ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printTotalEnemies(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        TotalEnemies s1 = (TotalEnemies) r1.sectionMap().get(SectionHeader.TOTAL_ENEMIES);
        TotalEnemies s2 = (TotalEnemies) r2.sectionMap().get(SectionHeader.TOTAL_ENEMIES);
        TotalEnemies sd = (TotalEnemies) d.sectionMap().get(SectionHeader.TOTAL_ENEMIES);

        printSectionHeader("Total Enemies");
        printRow("Total Enemies",    fmt(s1.totalEnemies()),    fmt(s2.totalEnemies()),    fmt(sd.totalEnemies()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Basic",            fmt(s1.basic()),           fmt(s2.basic()),           fmt(sd.basic()),           ColorStrategy.HIGHER_IS_BETTER);
        printRow("Fast",             fmt(s1.fast()),            fmt(s2.fast()),            fmt(sd.fast()),            ColorStrategy.HIGHER_IS_BETTER);
        printRow("Tank",             fmt(s1.tank()),            fmt(s2.tank()),            fmt(sd.tank()),            ColorStrategy.HIGHER_IS_BETTER);
        printRow("Ranged",           fmt(s1.ranged()),          fmt(s2.ranged()),          fmt(sd.ranged()),          ColorStrategy.HIGHER_IS_BETTER);
        printRow("Boss",             fmt(s1.boss()),            fmt(s2.boss()),            fmt(sd.boss()),            ColorStrategy.HIGHER_IS_BETTER);
        printRow("Protector",        fmt(s1.protector()),       fmt(s2.protector()),       fmt(sd.protector()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Vampires",         fmt(s1.vampires()),        fmt(s2.vampires()),        fmt(sd.vampires()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Rays",             fmt(s1.rays()),            fmt(s2.rays()),            fmt(sd.rays()),            ColorStrategy.HIGHER_IS_BETTER);
        printRow("Scatters",         fmt(s1.scatters()),        fmt(s2.scatters()),        fmt(sd.scatters()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Saboteur",         fmt(s1.saboteur()),        fmt(s2.saboteur()),        fmt(sd.saboteur()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Commander",        fmt(s1.commander()),       fmt(s2.commander()),       fmt(sd.commander()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Overcharge",       fmt(s1.overcharge()),      fmt(s2.overcharge()),      fmt(sd.overcharge()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Summoned Enemies", fmt(s1.summonedEnemies()), fmt(s2.summonedEnemies()), fmt(sd.summonedEnemies()), ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printCoins(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        Coins s1 = (Coins) r1.sectionMap().get(SectionHeader.COINS);
        Coins s2 = (Coins) r2.sectionMap().get(SectionHeader.COINS);
        Coins sd = (Coins) d.sectionMap().get(SectionHeader.COINS);

        printSectionHeader("Coins");
        printRow("Coins Earned",          fmt(s1.coinsEarned()),     fmt(s2.coinsEarned()),     fmt(sd.coinsEarned()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Coins / Kill",          fmt(s1.coinsPerKill()),    fmt(s2.coinsPerKill()),    fmt(sd.coinsPerKill()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Other Coin Bonuses",    fmt(s1.otherCoinBonuses()),fmt(s2.otherCoinBonuses()),fmt(sd.otherCoinBonuses()),ColorStrategy.HIGHER_IS_BETTER);
        printRow("Critical Coin",         fmt(s1.criticalCoin()),    fmt(s2.criticalCoin()),    fmt(sd.criticalCoin()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Golden Tower",          fmt(s1.goldenTower()),     fmt(s2.goldenTower()),     fmt(sd.goldenTower()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Golden Combo",          fmt(s1.goldenCombo()),     fmt(s2.goldenCombo()),     fmt(sd.goldenCombo()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Death Wave",            fmt(s1.deathWave()),       fmt(s2.deathWave()),       fmt(sd.deathWave()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Spotlight",             fmt(s1.spotlight()),       fmt(s2.spotlight()),       fmt(sd.spotlight()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Black Hole",            fmt(s1.blackHole()),       fmt(s2.blackHole()),       fmt(sd.blackHole()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Orbs",                  fmt(s1.orbs()),            fmt(s2.orbs()),            fmt(sd.orbs()),            ColorStrategy.HIGHER_IS_BETTER);
        printRow("Golden Bot",            fmt(s1.goldenBot()),       fmt(s2.goldenBot()),       fmt(sd.goldenBot()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Wave Skip",             fmt(s1.waveSkip()),        fmt(s2.waveSkip()),        fmt(sd.waveSkip()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Coins / Wave",          fmt(s1.coinsPerWave()),    fmt(s2.coinsPerWave()),    fmt(sd.coinsPerWave()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Coins Fetched",         fmt(s1.coinsFetched()),    fmt(s2.coinsFetched()),    fmt(sd.coinsFetched()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Bounty Coins",          fmt(s1.bountyCoins()),     fmt(s2.bountyCoins()),     fmt(sd.bountyCoins()),     ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printCash(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        Cash s1 = (Cash) r1.sectionMap().get(SectionHeader.CASH);
        Cash s2 = (Cash) r2.sectionMap().get(SectionHeader.CASH);
        Cash sd = (Cash) d.sectionMap().get(SectionHeader.CASH);

        printSectionHeader("Cash");
        printRow("Cash Earned",    fmt(s1.cashEarned()),    fmt(s2.cashEarned()),    fmt(sd.cashEarned()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Golden Tower",   fmt(s1.goldenTower()),   fmt(s2.goldenTower()),   fmt(sd.goldenTower()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Interest Earned",fmt(s1.interestEarned()),fmt(s2.interestEarned()),fmt(sd.interestEarned()),ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printCurrencies(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        Currencies s1 = (Currencies) r1.sectionMap().get(SectionHeader.CURRENCIES);
        Currencies s2 = (Currencies) r2.sectionMap().get(SectionHeader.CURRENCIES);
        Currencies sd = (Currencies) d.sectionMap().get(SectionHeader.CURRENCIES);

        printSectionHeader("Currencies");
        printRow("Cells Earned",        fmt(s1.cellsEarned()),       fmt(s2.cellsEarned()),       fmt(sd.cellsEarned()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Gems",                fmt(s1.gems()),              fmt(s2.gems()),              fmt(sd.gems()),              ColorStrategy.HIGHER_IS_BETTER);
        printRow("Ad Gems",             fmt(s1.adGems()),            fmt(s2.adGems()),            fmt(sd.adGems()),            ColorStrategy.HIGHER_IS_BETTER);
        printRow("Gem Blocks Tapped",   fmt(s1.gemBlockTapped()),    fmt(s2.gemBlockTapped()),    fmt(sd.gemBlockTapped()),    ColorStrategy.HIGHER_IS_BETTER);
        printRow("Fetch Gems",          fmt(s1.fetchGems()),         fmt(s2.fetchGems()),         fmt(sd.fetchGems()),         ColorStrategy.HIGHER_IS_BETTER);
        printRow("Medals",              fmt(s1.medals()),            fmt(s2.medals()),            fmt(sd.medals()),            ColorStrategy.HIGHER_IS_BETTER);
        printRow("Reroll Shards Earned",fmt(s1.reRollShardsEarned()),fmt(s2.reRollShardsEarned()),fmt(sd.reRollShardsEarned()),ColorStrategy.HIGHER_IS_BETTER);
        printRow("Reroll Shards Fetched",fmt(s1.reRollShardsFetched()),fmt(s2.reRollShardsFetched()),fmt(sd.reRollShardsFetched()),ColorStrategy.HIGHER_IS_BETTER);
        printRow("Cannon Shards",       fmt(s1.cannonShards()),      fmt(s2.cannonShards()),      fmt(sd.cannonShards()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Armor Shards",        fmt(s1.armorShards()),       fmt(s2.armorShards()),       fmt(sd.armorShards()),       ColorStrategy.HIGHER_IS_BETTER);
        printRow("Generator Shards",    fmt(s1.generatorShards()),   fmt(s2.generatorShards()),   fmt(sd.generatorShards()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Core Shards",         fmt(s1.coreShards()),        fmt(s2.coreShards()),        fmt(sd.coreShards()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Common Modules",      fmt(s1.commonModules()),     fmt(s2.commonModules()),     fmt(sd.commonModules()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Rare Modules",        fmt(s1.rareModules()),       fmt(s2.rareModules()),       fmt(sd.rareModules()),       ColorStrategy.HIGHER_IS_BETTER);
    }

    private void printEnemiesDestroyedBy(BattleHistory r1, BattleHistory r2, BattleHistory d) {
        EnemiesDestroyedBy s1 = (EnemiesDestroyedBy) r1.sectionMap().get(SectionHeader.ENEMIES_DESTROYED_BY);
        EnemiesDestroyedBy s2 = (EnemiesDestroyedBy) r2.sectionMap().get(SectionHeader.ENEMIES_DESTROYED_BY);
        EnemiesDestroyedBy sd = (EnemiesDestroyedBy) d.sectionMap().get(SectionHeader.ENEMIES_DESTROYED_BY);

        printSectionHeader("Enemies Destroyed By");
        printRow("Projectiles",    fmt(s1.projectiles()),   fmt(s2.projectiles()),   fmt(sd.projectiles()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Thorns",         fmt(s1.thorns()),        fmt(s2.thorns()),        fmt(sd.thorns()),        ColorStrategy.HIGHER_IS_BETTER);
        printRow("Land Mines",     fmt(s1.landMines()),     fmt(s2.landMines()),     fmt(sd.landMines()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Orbs",           fmt(s1.orbs()),          fmt(s2.orbs()),          fmt(sd.orbs()),          ColorStrategy.HIGHER_IS_BETTER);
        printRow("Chain Lightning",fmt(s1.chainLightning()),fmt(s2.chainLightning()),fmt(sd.chainLightning()),ColorStrategy.HIGHER_IS_BETTER);
        printRow("Smart Missiles", fmt(s1.smartMissiles()), fmt(s2.smartMissiles()), fmt(sd.smartMissiles()), ColorStrategy.HIGHER_IS_BETTER);
        printRow("Inner Land Mines",fmt(s1.innerLandMines()),fmt(s2.innerLandMines()),fmt(sd.innerLandMines()),ColorStrategy.HIGHER_IS_BETTER);
        printRow("Poison Swamp",   fmt(s1.poisonSwamp()),   fmt(s2.poisonSwamp()),   fmt(sd.poisonSwamp()),   ColorStrategy.HIGHER_IS_BETTER);
        printRow("Death Ray",      fmt(s1.deathRay()),      fmt(s2.deathRay()),      fmt(sd.deathRay()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Black Hole",     fmt(s1.blackHole()),     fmt(s2.blackHole()),     fmt(sd.blackHole()),     ColorStrategy.HIGHER_IS_BETTER);
        printRow("Flame Bot",      fmt(s1.flameBot()),      fmt(s2.flameBot()),      fmt(sd.flameBot()),      ColorStrategy.HIGHER_IS_BETTER);
        printRow("Other",          fmt(s1.other()),         fmt(s2.other()),         fmt(sd.other()),         ColorStrategy.HIGHER_IS_BETTER);
    }

    // -------------------------------------------------------------------------
    // Rendering helpers
    // -------------------------------------------------------------------------

    private void printHeader() {
        System.out.println();
        System.out.printf("%-" + COL_LABEL + "s %-" + COL_VAL + "s %-" + COL_VAL + "s %-" + COL_VAL + "s%n",
                "", "Report 1", "Report 2", "Delta");
        System.out.println("=".repeat(COL_LABEL + COL_VAL * 3 + 3));
    }

    private void printSectionHeader(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println("-".repeat(COL_LABEL + COL_VAL * 3 + 3));
    }

    private void printRow(String label, String v1, String v2, String delta, ColorStrategy strategy) {
        String coloredDelta = colorize(delta, strategy);
        System.out.printf("%-" + COL_LABEL + "s %-" + COL_VAL + "s %-" + COL_VAL + "s %s%n",
                label, v1, v2, coloredDelta);
    }

    private String colorize(String delta, ColorStrategy strategy) {
        if (strategy == ColorStrategy.NEUTRAL) return delta;

        // Try to determine sign from the raw string value
        // TowerNumber.toString() and numeric types should produce a string we can inspect
        boolean isNegative = delta.startsWith("-");
        boolean isZero = delta.equals("0") || delta.equals("0.00") || delta.equals("0s");

        if (isZero) return delta;

        boolean isGood = switch (strategy) {
            case HIGHER_IS_BETTER -> !isNegative;
            case LOWER_IS_BETTER  ->  isNegative;
            default -> false;
        };

        return (isGood ? GREEN : RED) + delta + RESET;
    }

    // -------------------------------------------------------------------------
    // Formatting — delegate to whatever toString() your types provide.
    // Add overloads here if you need custom formatting for specific types.
    // -------------------------------------------------------------------------

    private String fmt(Object value) {
        return value == null ? "-" : value.toString();
    }

    private String fmt(int value) {
        return String.valueOf(value);
    }

    private String fmt(long value) {
        return String.valueOf(value);
    }
}
