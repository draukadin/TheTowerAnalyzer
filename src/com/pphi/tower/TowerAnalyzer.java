package com.pphi.tower;

import com.pphi.tower.analyzers.BattleDiagnostic;
import com.pphi.tower.analyzers.RunComparison;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.parser.BattleHistoryParser;
import com.pphi.tower.reporter.ReflectionBattleComparisonReporter;

import java.nio.file.Path;
import java.util.List;

public class TowerAnalyzer {

    public static void main(String[] args) throws Exception {
        BattleHistoryParser battleHistoryParser = new BattleHistoryParser();
        RunComparison runComparison = new RunComparison(battleHistoryParser);
        BattleHistory reportOne = battleHistoryParser.parse(Path.of(args[0]));
        BattleDiagnostic battleDiagnostic = new BattleDiagnostic();

        DiagnosisResult battleResult = battleDiagnostic.analyzeReport(reportOne);
        System.out.println(battleResult);

        if (args.length == 2) {
            BattleHistory reportTwo = battleHistoryParser.parse(Path.of(args[1]));
            List<BattleHistory> results = runComparison.compareBattles(reportOne, reportTwo);
            new ReflectionBattleComparisonReporter().printReport(results);
        }
    }
}
