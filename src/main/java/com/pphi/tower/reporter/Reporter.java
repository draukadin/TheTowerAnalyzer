package com.pphi.tower.reporter;

import com.pphi.tower.model.battlehistory.BattleHistory;

import java.util.List;

public interface Reporter {
    void printReport(List<BattleHistory> battles);
}
