package com.pphi.tower.analyzers;

import com.pphi.tower.fixtures.BattleHistoryFixtures;
import com.pphi.tower.fixtures.TowerNumberFactory;
import com.pphi.tower.model.battlehistory.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RunComparisonTest {

    private final RunComparison comparison = new RunComparison(null);

    @Test
    void compareBattles_returnsThreeElements() {
        var h1 = BattleHistoryFixtures.unknownVariance();
        var h2 = BattleHistoryFixtures.unknownVariance();
        List<BattleHistory> result = comparison.compareBattles(h1, h2);
        assertThat(result).hasSize(3);
    }

    @Test
    void compareBattles_firstElementIsFirstHistory() {
        var h1 = BattleHistoryFixtures.vampireDrainLock();
        var h2 = BattleHistoryFixtures.unknownVariance();
        List<BattleHistory> result = comparison.compareBattles(h1, h2);
        assertThat(result.get(0)).isSameAs(h1);
    }

    @Test
    void compareBattles_secondElementIsSecondHistory() {
        var h1 = BattleHistoryFixtures.vampireDrainLock();
        var h2 = BattleHistoryFixtures.unknownVariance();
        List<BattleHistory> result = comparison.compareBattles(h1, h2);
        assertThat(result.get(1)).isSameAs(h2);
    }

    @Test
    void compareBattles_deltaWave_isFirstMinusSecond() {
        // Both fixtures use wave=3000 (from BattleHistoryFixtures.report())
        // Use the same fixture → delta wave = 3000 - 3000 = 0
        var h1 = BattleHistoryFixtures.unknownVariance();
        var h2 = BattleHistoryFixtures.unknownVariance();
        var delta = result(h1, h2);
        var deltaReport = (BattleReport) delta.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(deltaReport.wave()).isZero();
    }

    @Test
    void compareBattles_deltaTier_isFirstMinusSecond() {
        var h1 = BattleHistoryFixtures.unknownVariance();
        var h2 = BattleHistoryFixtures.unknownVariance();
        var delta = result(h1, h2);
        var deltaReport = (BattleReport) delta.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(deltaReport.tier()).isZero();
    }

    @Test
    void compareBattles_deltaKilledBy_isCombined() {
        var h1 = BattleHistoryFixtures.killedByBoss();    // killedBy="Boss"
        var h2 = BattleHistoryFixtures.unknownVariance(); // killedBy="Basic"
        var delta = result(h1, h2);
        var deltaReport = (BattleReport) delta.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(deltaReport.killedBy()).isEqualTo("Boss vs Basic");
    }

    @Test
    void compareBattles_deltaTowerNumber_usesMinusMath() {
        var h1 = BattleHistoryFixtures.unknownVariance();
        var h2 = BattleHistoryFixtures.unknownVariance();
        var delta = result(h1, h2);
        var deltaReport = (BattleReport) delta.sectionMap().get(SectionHeader.BATTLE_REPORT);
        // Same values → difference = zero
        assertThat(deltaReport.coinsEarned().amount()).isZero();
    }

    @Test
    void compareBattles_deltaHasAllSixteenSections() {
        var h1 = BattleHistoryFixtures.unknownVariance();
        var h2 = BattleHistoryFixtures.unknownVariance();
        var delta = result(h1, h2);
        assertThat(delta.sectionMap()).containsKeys(SectionHeader.values());
    }

    @Test
    void compareBattles_long_deltaFields_enemiesDestroyedBy() {
        var h1 = BattleHistoryFixtures.unknownVariance();
        var h2 = BattleHistoryFixtures.unknownVariance();
        var delta = result(h1, h2);
        var deltaDestroyed = (EnemiesDestroyedBy) delta.sectionMap().get(SectionHeader.ENEMIES_DESTROYED_BY);
        // Same input → all deltas are 0
        assertThat(deltaDestroyed.orbs()).isZero();
        assertThat(deltaDestroyed.chainLightning()).isZero();
        assertThat(deltaDestroyed.thorns()).isZero();
    }

    private BattleHistory result(BattleHistory h1, BattleHistory h2) {
        return comparison.compareBattles(h1, h2).get(2);
    }
}