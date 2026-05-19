package com.pphi.tower.analyzers;

import com.pphi.tower.model.battlehistory.*;
import com.pphi.tower.parser.BattleHistoryParser;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pphi.tower.model.battlehistory.SectionHeader.*;

public class RunComparison {

    private final BattleHistoryParser battleHistoryParser;

    public RunComparison(BattleHistoryParser battleHistoryParser) {
        this.battleHistoryParser = battleHistoryParser;
    }

    public List<BattleHistory> compareBattles(BattleHistory firstBattle, BattleHistory secondBattle) {
        Map<SectionHeader, Section> sectionMap = new HashMap<>();
        sectionMap.put(BATTLE_REPORT, compareBattleReport((BattleReport) firstBattle.sectionMap().get(BATTLE_REPORT), (BattleReport) secondBattle.sectionMap().get(BATTLE_REPORT)));
        sectionMap.put(RECORDS, compareRecords((Records) firstBattle.sectionMap().get(RECORDS), (Records) secondBattle.sectionMap().get(RECORDS)));
        sectionMap.put(DAMAGE, compareDamage((Damage) firstBattle.sectionMap().get(DAMAGE), (Damage) secondBattle.sectionMap().get(DAMAGE)));
        sectionMap.put(DAMAGE_TAKEN, compareDamageTaken((DamageTaken) firstBattle.sectionMap().get(DAMAGE_TAKEN), (DamageTaken) secondBattle.sectionMap().get(DAMAGE_TAKEN)));
        sectionMap.put(BONUS_HEALTH_GAINED, compareBonusHealthGained((BonusHealthGained) firstBattle.sectionMap().get(BONUS_HEALTH_GAINED), (BonusHealthGained) secondBattle.sectionMap().get(BONUS_HEALTH_GAINED)));
        sectionMap.put(HEALTH_REGENERATED, compareHealthRegenerated((HealthRegenerated) firstBattle.sectionMap().get(HEALTH_REGENERATED), (HealthRegenerated) secondBattle.sectionMap().get(HEALTH_REGENERATED)));
        sectionMap.put(DAMAGE_BLOCKED, compareDamageBlocked((DamageBlocked) firstBattle.sectionMap().get(DAMAGE_BLOCKED), (DamageBlocked) secondBattle.sectionMap().get(DAMAGE_BLOCKED)));
        sectionMap.put(UTILITY, compareUtility((Utility) firstBattle.sectionMap().get(UTILITY), (Utility) secondBattle.sectionMap().get(UTILITY)));
        sectionMap.put(COUNTS, compareCounts((Counts) firstBattle.sectionMap().get(COUNTS), (Counts) secondBattle.sectionMap().get(COUNTS)));
        sectionMap.put(ENEMIES_HIT_BY, compareEnemiesHitBy((EnemiesHitBy) firstBattle.sectionMap().get(ENEMIES_HIT_BY), (EnemiesHitBy) secondBattle.sectionMap().get(ENEMIES_HIT_BY)));
        sectionMap.put(KILLED_WITH_EFFECT_ACTIVE, compareKilledWithEffectActive((KilledWithEffectActive) firstBattle.sectionMap().get(KILLED_WITH_EFFECT_ACTIVE), (KilledWithEffectActive) secondBattle.sectionMap().get(KILLED_WITH_EFFECT_ACTIVE)));
        sectionMap.put(TOTAL_ENEMIES, compareTotalEnemies((TotalEnemies) firstBattle.sectionMap().get(TOTAL_ENEMIES), (TotalEnemies) secondBattle.sectionMap().get(TOTAL_ENEMIES)));
        sectionMap.put(COINS, compareCoins((Coins) firstBattle.sectionMap().get(COINS), (Coins) secondBattle.sectionMap().get(COINS)));
        sectionMap.put(CASH, compareCash((Cash) firstBattle.sectionMap().get(CASH), (Cash) secondBattle.sectionMap().get(CASH)));
        sectionMap.put(CURRENCIES, compareCurrencies((Currencies) firstBattle.sectionMap().get(CURRENCIES), (Currencies) secondBattle.sectionMap().get(CURRENCIES)));
        sectionMap.put(ENEMIES_DESTROYED_BY, compareEnemiesDestroyedBy((EnemiesDestroyedBy) firstBattle.sectionMap().get(ENEMIES_DESTROYED_BY), (EnemiesDestroyedBy) secondBattle.sectionMap().get(ENEMIES_DESTROYED_BY)));
        BattleHistory delta = new BattleHistory(sectionMap);
        return List.of(firstBattle, secondBattle, delta);
    }

    public List<BattleHistory> compareBattles(Path firstBattlePath, Path secondBattlePath) {
        BattleHistory firstBattle = battleHistoryParser.parse(firstBattlePath);
        BattleHistory secondBattle = battleHistoryParser.parse(secondBattlePath);
        return compareBattles(firstBattle, secondBattle);
    }

    private Section compareBattleReport(BattleReport reportOne, BattleReport reportTwo) {
        return new BattleReport(
                null,
                null,
                reportOne.gameTime().minus(reportTwo.gameTime()),
                reportOne.realTime().minus(reportTwo.realTime()),
                reportOne.tier() - reportTwo.tier(),
                reportOne.wave() - reportTwo.wave(),
                String.format("%s vs %s", reportOne.killedBy(), reportTwo.killedBy()),
                reportOne.coinsEarned().minus(reportTwo.coinsEarned()),
                reportOne.coinsPerHour().minus(reportTwo.coinsPerHour()),
                reportOne.cellsEarned().minus(reportTwo.cellsEarned()),
                reportOne.cellsPerHour().minus(reportTwo.cellsPerHour())
        );
    }

    private Section compareRecords(Records reportOne, Records reportTwo) {
        return new Records(
                reportOne.highestCoinsPerMinute().minus(reportTwo.highestCoinsPerMinute()),
                reportOne.largestWaveSkip() - reportTwo.largestWaveSkip(),
                reportOne.mostCoinsFromWaveSkip().minus(reportTwo.mostCoinsFromWaveSkip()),
                reportOne.mostCellsFromWaveSkip() - reportTwo.mostCellsFromWaveSkip(),
                reportOne.largestSmartMissileStack() - reportTwo.largestSmartMissileStack(),
                reportOne.largestGoldenCombo() - reportTwo.largestGoldenCombo(),
                reportOne.mostCoinsFromGoldenCombo().minus(reportTwo.mostCoinsFromGoldenCombo()),
                reportOne.largestInnerLandmineCharge().minus(reportTwo.largestInnerLandmineCharge())
        );
    }

    private Section compareDamage(Damage damageOne, Damage damageTwo) {
        return new Damage(
                damageOne.damageDealt().minus(damageTwo.damageDealt()),
                damageOne.projectiles().minus(damageTwo.projectiles()),
                damageOne.rendArmor().minus(damageTwo.rendArmor()),
                damageOne.deathRay().minus(damageTwo.deathRay()),
                damageOne.thorns().minus(damageTwo.thorns()),
                damageOne.orbs().minus(damageTwo.orbs()),
                damageOne.landMines().minus(damageTwo.landMines()),
                damageOne.chainLightning().minus(damageTwo.chainLightning()),
                damageOne.smartMissiles().minus(damageTwo.smartMissiles()),
                damageOne.innerLandMines().minus(damageTwo.innerLandMines()),
                damageOne.poisonSwamp().minus(damageTwo.poisonSwamp()),
                damageOne.deathWave().minus(damageTwo.deathWave()),
                damageOne.blackHole().minus(damageTwo.blackHole()),
                damageOne.flameBot().minus(damageTwo.flameBot()),
                damageOne.attackChip().minus(damageTwo.attackChip()),
                damageOne.electrons().minus(damageTwo.electrons())
        );
    }

    private Section compareDamageTaken(DamageTaken damageTakenOne, DamageTaken damageTakenTwo) {
        return new DamageTaken(
                damageTakenOne.tower().minus(damageTakenTwo.tower()),
                damageTakenOne.wall().minus(damageTakenTwo.wall())
        );
    }

    private Section compareBonusHealthGained(BonusHealthGained bonusHealthGainedOne, BonusHealthGained bonusHealthGainedTwo) {
        return new BonusHealthGained(
                bonusHealthGainedOne.fromDeathWave().minus(bonusHealthGainedTwo.fromDeathWave())
        );
    }

    private Section compareHealthRegenerated(HealthRegenerated healthRegeneratedOne, HealthRegenerated healthRegeneratedTwo) {
        return new HealthRegenerated(
                healthRegeneratedOne.lifeSteal().minus(healthRegeneratedTwo.lifeSteal()),
                healthRegeneratedOne.towerHealthRegen().minus(healthRegeneratedTwo.towerHealthRegen()),
                healthRegeneratedOne.wallHealthRegen().minus(healthRegeneratedTwo.wallHealthRegen())
        );
    }

    private Section compareDamageBlocked(DamageBlocked damageBlockedOne, DamageBlocked damageBlockedTwo) {
        return new DamageBlocked(
                damageBlockedOne.defensePercent().minus(damageBlockedTwo.defensePercent()),
                damageBlockedOne.defenseAbsolute().minus(damageBlockedTwo.defenseAbsolute()),
                damageBlockedOne.chronoField().minus(damageBlockedTwo.chronoField()),
                damageBlockedOne.chainThunder().minus(damageBlockedTwo.chainThunder()),
                damageBlockedOne.flameBot().minus(damageBlockedTwo.flameBot()),
                damageBlockedOne.primordialCollapse().minus(damageBlockedTwo.primordialCollapse()),
                damageBlockedOne.negativeMassProjector().minus(damageBlockedTwo.negativeMassProjector())
        );
    }

    private Section compareUtility(Utility utilityOne, Utility utilityTwo) {
        return new Utility(
                utilityOne.recoveryPackages() - utilityTwo.recoveryPackages(),
                utilityOne.freeAttackUpgrades() - utilityTwo.freeAttackUpgrades(),
                utilityOne.freeDefenseUpgrades() - utilityTwo.freeDefenseUpgrades(),
                utilityOne.freeUtilityUpgrades() - utilityTwo.freeUtilityUpgrades(),
                utilityOne.enemyAttackLevelSkipped() - utilityTwo.enemyAttackLevelSkipped(),
                utilityOne.enemyHealthLevelSkipped() - utilityTwo.enemyHealthLevelSkipped()
        );
    }

    private Section compareCounts(Counts countsOne, Counts countsTwo) {
        return new Counts(
                countsOne.projectileCount().minus(countsTwo.projectileCount()),
                countsOne.landMinesSpawned() - countsTwo.landMinesSpawned(),
                countsOne.thunderBotStuns() - countsTwo.thunderBotStuns(),
                countsOne.wavesSkipped() - countsTwo.wavesSkipped(),
                countsOne.deathDefy() - countsTwo.deathDefy(),
                countsOne.hitsAbsorbedByEnergyShield() - countsTwo.hitsAbsorbedByEnergyShield(),
                countsOne.nuke() - countsTwo.nuke(),
                countsOne.secondWind() - countsTwo.secondWind(),
                countsOne.demonMode() - countsTwo.demonMode()
        );
    }

    private Section compareEnemiesHitBy(EnemiesHitBy enemiesHitByOne, EnemiesHitBy enemiesHitByTwo) {
        return new EnemiesHitBy(
                enemiesHitByOne.projectiles().minus(enemiesHitByTwo.projectiles()),
                enemiesHitByOne.thorns().minus(enemiesHitByTwo.thorns()),
                enemiesHitByOne.orbs().minus(enemiesHitByTwo.orbs()),
                enemiesHitByOne.deathRay().minus(enemiesHitByTwo.deathRay()),
                enemiesHitByOne.chainLightning().minus(enemiesHitByTwo.chainLightning()),
                enemiesHitByOne.smartMissiles().minus(enemiesHitByTwo.smartMissiles()),
                enemiesHitByOne.innerLandMines().minus(enemiesHitByTwo.innerLandMines()),
                enemiesHitByOne.poisonSwamp().minus(enemiesHitByTwo.poisonSwamp()),
                enemiesHitByOne.deathWave().minus(enemiesHitByTwo.deathWave()),
                enemiesHitByOne.blackHole().minus(enemiesHitByTwo.blackHole()),
                enemiesHitByOne.chronoField().minus(enemiesHitByTwo.chronoField()),
                enemiesHitByOne.landMines().minus(enemiesHitByTwo.landMines()),
                enemiesHitByOne.thunderBot().minus(enemiesHitByTwo.thunderBot()),
                enemiesHitByOne.flameBot().minus(enemiesHitByTwo.flameBot()),
                enemiesHitByOne.attackChip().minus(enemiesHitByTwo.attackChip()),
                enemiesHitByOne.orbitalAugment().minus(enemiesHitByTwo.orbitalAugment())
        );
    }

    private Section compareKilledWithEffectActive(KilledWithEffectActive killedWithEffectActiveOne, KilledWithEffectActive killedWithEffectActiveTwo) {
        return new KilledWithEffectActive(
                killedWithEffectActiveOne.goldenTower() - killedWithEffectActiveTwo.goldenTower(),
                killedWithEffectActiveOne.deathWave().minus(killedWithEffectActiveTwo.deathWave()),
                killedWithEffectActiveOne.spotlight() - killedWithEffectActiveTwo.spotlight(),
                killedWithEffectActiveOne.amplifyBot() - killedWithEffectActiveTwo.amplifyBot(),
                killedWithEffectActiveOne.goldenBot() - killedWithEffectActiveTwo.goldenBot(),
                killedWithEffectActiveOne.deathPenalty() - killedWithEffectActiveTwo.deathPenalty()
        );
    }

    private Section compareTotalEnemies(TotalEnemies totalEnemiesOne, TotalEnemies totalEnemiesTwo) {
        return new TotalEnemies(
                totalEnemiesOne.totalEnemies() - totalEnemiesTwo.totalEnemies(),
                totalEnemiesOne.basic() - totalEnemiesTwo.basic(),
                totalEnemiesOne.fast() - totalEnemiesTwo.fast(),
                totalEnemiesOne.tank() - totalEnemiesTwo.tank(),
                totalEnemiesOne.ranged() - totalEnemiesTwo.ranged(),
                totalEnemiesOne.boss() - totalEnemiesTwo.boss(),
                totalEnemiesOne.protector() - totalEnemiesTwo.protector(),
                totalEnemiesOne.vampires() - totalEnemiesTwo.vampires(),
                totalEnemiesOne.rays() - totalEnemiesTwo.rays(),
                totalEnemiesOne.scatters() - totalEnemiesTwo.scatters(),
                totalEnemiesOne.saboteur() - totalEnemiesTwo.saboteur(),
                totalEnemiesOne.commander() - totalEnemiesTwo.commander(),
                totalEnemiesOne.overcharge() - totalEnemiesTwo.overcharge(),
                totalEnemiesOne.summonedEnemies().minus(totalEnemiesTwo.summonedEnemies())
        );
    }

    private Section compareCoins(Coins coinsOne, Coins coinsTwo) {
        return new Coins(
                coinsOne.coinsEarned().minus(coinsTwo.coinsEarned()),
                coinsOne.coinsPerKill().minus(coinsTwo.coinsPerKill()),
                coinsOne.otherCoinBonuses().minus(coinsTwo.otherCoinBonuses()),
                coinsOne.criticalCoin().minus(coinsTwo.criticalCoin()),
                coinsOne.goldenTower().minus(coinsTwo.goldenTower()),
                coinsOne.goldenCombo().minus(coinsTwo.goldenCombo()),
                coinsOne.deathWave().minus(coinsTwo.deathWave()),
                coinsOne.spotlight().minus(coinsTwo.spotlight()),
                coinsOne.blackHole().minus(coinsTwo.blackHole()),
                coinsOne.orbs().minus(coinsTwo.orbs()),
                coinsOne.goldenBot().minus(coinsTwo.goldenBot()),
                coinsOne.waveSkip().minus(coinsTwo.waveSkip()),
                coinsOne.coinsPerWave().minus(coinsTwo.coinsPerWave()),
                coinsOne.coinsFetched().minus(coinsTwo.coinsFetched()),
                coinsOne.bountyCoins().minus(coinsTwo.bountyCoins())
        );
    }

    private Section compareCash(Cash cashOne, Cash cashTwo) {
        return new Cash(
                cashOne.cashEarned().minus(cashTwo.cashEarned()),
                cashOne.goldenTower().minus(cashTwo.goldenTower()),
                cashOne.interestEarned().minus(cashTwo.interestEarned())
        );
    }

    private Section compareCurrencies(Currencies currenciesOne, Currencies currenciesTwo) {
        return new Currencies(
                currenciesOne.cellsEarned().minus(currenciesTwo.cellsEarned()),
                currenciesOne.gems() - currenciesTwo.gems(),
                currenciesOne.adGems() - currenciesTwo.adGems(),
                currenciesOne.gemBlockTapped() - currenciesTwo.gemBlockTapped(),
                currenciesOne.fetchGems() - currenciesTwo.fetchGems(),
                currenciesOne.medals() - currenciesTwo.medals(),
                currenciesOne.reRollShardsEarned().minus(currenciesTwo.reRollShardsEarned()),
                currenciesOne.reRollShardsFetched().minus(currenciesTwo.reRollShardsFetched()),
                currenciesOne.cannonShards() - currenciesTwo.cannonShards(),
                currenciesOne.armorShards() - currenciesTwo.armorShards(),
                currenciesOne.generatorShards() - currenciesTwo.generatorShards(),
                currenciesOne.coreShards() - currenciesTwo.coreShards(),
                currenciesOne.commonModules() - currenciesTwo.commonModules(),
                currenciesOne.rareModules() - currenciesTwo.rareModules()
        );
    }

    private Section compareEnemiesDestroyedBy(EnemiesDestroyedBy enemiesDestroyedByOne, EnemiesDestroyedBy enemiesDestroyedByTwo) {
        return new EnemiesDestroyedBy(
                enemiesDestroyedByOne.projectiles() - enemiesDestroyedByTwo.projectiles(),
                enemiesDestroyedByOne.thorns() - enemiesDestroyedByTwo.thorns(),
                enemiesDestroyedByOne.landMines() - enemiesDestroyedByTwo.landMines(),
                enemiesDestroyedByOne.orbs() - enemiesDestroyedByTwo.orbs(),
                enemiesDestroyedByOne.chainLightning() - enemiesDestroyedByTwo.chainLightning(),
                enemiesDestroyedByOne.smartMissiles() - enemiesDestroyedByTwo.smartMissiles(),
                enemiesDestroyedByOne.innerLandMines() - enemiesDestroyedByTwo.innerLandMines(),
                enemiesDestroyedByOne.poisonSwamp() - enemiesDestroyedByTwo.poisonSwamp(),
                enemiesDestroyedByOne.deathRay() - enemiesDestroyedByTwo.deathRay(),
                enemiesDestroyedByOne.blackHole() - enemiesDestroyedByTwo.blackHole(),
                enemiesDestroyedByOne.flameBot() - enemiesDestroyedByTwo.flameBot(),
                enemiesDestroyedByOne.other() - enemiesDestroyedByTwo.other()
        );
    }
}
