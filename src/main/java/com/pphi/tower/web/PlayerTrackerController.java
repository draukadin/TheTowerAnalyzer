package com.pphi.tower.web;

import com.pphi.tower.model.sheets.Currencies;
import com.pphi.tower.repository.LabRepository.LabMultipliers;
import com.pphi.tower.service.TowerTrackerFetcherService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/player-tracker")
@CrossOrigin(origins = "*")
public class PlayerTrackerController {

    private final TowerTrackerFetcherService service;

    public PlayerTrackerController(TowerTrackerFetcherService service) {
        this.service = service;
    }

    @GetMapping("/currencies")
    public Currencies getCurrencies() throws IOException {
        return service.fetchCurrencies();
    }

    @GetMapping("/state")
    public Map<String, Object> getTowerState(
            @RequestParam(defaultValue = "false") boolean includeRelicDetails) {
        LabMultipliers m = service.fetchLabMultipliers();
        var builder = new HashMap<String, Object>();
        builder.put("ultimateWeapons", service.fetchUltimateWeapons());
        builder.put("modules", service.fetchModules());
        builder.put("relics", service.fetchRelics());
        builder.put("includeRelicDetails", includeRelicDetails);
        builder.put("labMultipliers", Map.of(
            "speedMultiplier",      m.speedMult(),
            "coinCostMultiplier",   m.costMult(),
            "labsSpeedLevel",       m.labsSpeedLevel(),
            "coinDiscountLevel",    m.coinDiscountLevel(),
            "relicLabSpeedBonus",   m.relicLabSpeedBonus()
        ));
        return builder;
    }

    @GetMapping("/labs")
    public Map<String, String> getLabPlan() throws IOException {
        return Map.of("labPlanning", service.fetchLabPlanning());
    }

    @GetMapping("/lab-state")
    public Map<String, Object> getLabState() {
        return Map.of(
            "labs", service.fetchLabState(),
            "multipliers", service.fetchLabMultipliers()
        );
    }
}
