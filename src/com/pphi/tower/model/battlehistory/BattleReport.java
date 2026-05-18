package com.pphi.tower.model.battlehistory;

import com.pphi.tower.model.TowerNumber;

import java.time.Duration;
import java.time.Instant;

public record BattleReport(
        String towerEra,
        Instant battleReportDate,
        Duration gameTime,
        Duration realTime,
        int tier,
        int wave,
        String killedBy,
        TowerNumber coinsEarned,
        TowerNumber coinsPerHour,
        TowerNumber cellsEarned,
        TowerNumber cellsPerHour) implements Section {

    @Override
    public SectionHeader sectionHeader() {
        return SectionHeader.BATTLE_REPORT;
    }
}
