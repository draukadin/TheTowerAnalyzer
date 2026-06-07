package com.pphi.tower.web;

import com.pphi.tower.model.sheets.Currencies;
import com.pphi.tower.repository.CurrencySnapshotRepository;
import com.pphi.tower.repository.LabRepository.LabMultipliers;
import com.pphi.tower.service.TowerTrackerFetcherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/player-tracker")
@CrossOrigin(origins = "*")
public class PlayerTrackerController {

    private static final Logger log = LoggerFactory.getLogger(PlayerTrackerController.class);

    private final TowerTrackerFetcherService service;
    private final CurrencySnapshotRepository snapshotRepository;

    public PlayerTrackerController(TowerTrackerFetcherService service,
                                   CurrencySnapshotRepository snapshotRepository) {
        this.service = service;
        this.snapshotRepository = snapshotRepository;
    }

    @GetMapping("/currencies")
    public ResponseEntity<Currencies> getCurrencies() {
        return snapshotRepository.findLatest()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/currencies")
    public ResponseEntity<Void> saveCurrencies(@RequestBody Currencies currencies) {
        snapshotRepository.saveCurrencySnapshot(LocalDateTime.now(), currencies);
        log.info("Manual currency snapshot saved");
        return ResponseEntity.ok().build();
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

    @GetMapping("/lab-state")
    public Map<String, Object> getLabState() {
        return Map.of(
            "labs", service.fetchLabState(),
            "multipliers", service.fetchLabMultipliers()
        );
    }
}
