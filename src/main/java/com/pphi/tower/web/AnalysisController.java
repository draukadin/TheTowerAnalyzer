package com.pphi.tower.web;

import com.pphi.tower.config.AppConfig;
import com.pphi.tower.service.CellIncomeService;
import com.pphi.tower.web.dto.CellIncomeDto;
import com.pphi.tower.web.dto.LabSpeedDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final CellIncomeService cellIncomeService;
    private final AppConfig config;

    public AnalysisController(CellIncomeService cellIncomeService, AppConfig config) {
        this.cellIncomeService = cellIncomeService;
        this.config = config;
    }

    @GetMapping("/cells")
    public CellIncomeDto getCellIncome(
            @RequestParam(required = false) Integer days) {
        int d = days != null ? days : config.getCells().getWindowDaysDefault();
        return cellIncomeService.getCellIncome(d);
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
