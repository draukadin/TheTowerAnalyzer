package com.pphi.tower.web;

import com.pphi.tower.config.AppConfig;
import com.pphi.tower.service.CellIncomeService;
import com.pphi.tower.service.ShardAnalysisService;
import com.pphi.tower.web.dto.CellIncomeDto;
import com.pphi.tower.web.dto.LabSpeedDto;
import com.pphi.tower.web.dto.ShardRateDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    public enum DataSource { BATTLE_REPORTS, SNAPSHOTS }

    private final CellIncomeService cellIncomeService;
    private final ShardAnalysisService shardAnalysisService;
    private final AppConfig config;

    public AnalysisController(CellIncomeService cellIncomeService,
                              ShardAnalysisService shardAnalysisService,
                              AppConfig config) {
        this.cellIncomeService = cellIncomeService;
        this.shardAnalysisService = shardAnalysisService;
        this.config = config;
    }

    @GetMapping("/cells")
    public CellIncomeDto getCellIncome(
            @RequestParam(required = false) Integer days) {
        int d = days != null ? days : config.getCells().getWindowDaysDefault();
        return cellIncomeService.getCellIncome(d);
    }

    @GetMapping("/shards")
    public ShardRateDto getShardRates(
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false, defaultValue = "1")   int cannonLevel,
            @RequestParam(required = false, defaultValue = "1")   int armorLevel,
            @RequestParam(required = false, defaultValue = "1")   int generatorLevel,
            @RequestParam(required = false, defaultValue = "1")   int coreLevel,
            @RequestParam(required = false, defaultValue = "0")   int shardCostDiscountLevel,
            @RequestParam(required = false, defaultValue = "161") int cannonTargetLevel,
            @RequestParam(required = false, defaultValue = "161") int armorTargetLevel,
            @RequestParam(required = false, defaultValue = "161") int generatorTargetLevel,
            @RequestParam(required = false, defaultValue = "161") int coreTargetLevel,
            @RequestParam(required = false, defaultValue = "161") int targetLevel,
            @RequestParam(required = false, defaultValue = "BATTLE_REPORTS") DataSource dataSource) {
        int d = days != null ? days : config.getCells().getWindowDaysDefault();
        return switch (dataSource) {
            case SNAPSHOTS -> shardAnalysisService.getSnapshotBasedShardRates(
                    d, cannonLevel, armorLevel, generatorLevel, coreLevel,
                    shardCostDiscountLevel, targetLevel);
            default -> shardAnalysisService.getShardRates(
                    d, cannonLevel, armorLevel, generatorLevel, coreLevel,
                    shardCostDiscountLevel,
                    cannonTargetLevel, armorTargetLevel, generatorTargetLevel, coreTargetLevel);
        };
    }

    @GetMapping("/shards/snapshot-count")
    public int getSnapshotCount() {
        return shardAnalysisService.getSnapshotCount();
    }

    @GetMapping("/lab-speed")
    public LabSpeedDto getLabSpeed(
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false, defaultValue = "0") double cellsOnHand,
            @RequestParam(required = false, defaultValue = "0") double safetyBuffer) {
        int d = days != null ? days : config.getCells().getWindowDaysDefault();
        return cellIncomeService.getLabSpeedAffordability(d, cellsOnHand, safetyBuffer);
    }
}
