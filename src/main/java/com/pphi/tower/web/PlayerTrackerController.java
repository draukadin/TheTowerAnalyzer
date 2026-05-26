package com.pphi.tower.web;

import com.pphi.tower.model.sheets.Currencies;
import com.pphi.tower.service.TowerTrackerFetcherService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
}
