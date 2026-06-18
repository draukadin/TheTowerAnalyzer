package com.pphi.tower.web;

import com.pphi.tower.repository.PerkRepository;
import com.pphi.tower.repository.PerkRepository.PerkEntry;
import com.pphi.tower.repository.PerkRepository.PerkSettings;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perks")
@CrossOrigin(origins = "*")
public class PerkController {

    private final PerkRepository repo;

    public PerkController(PerkRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<PerkEntry> getAll() {
        return repo.getAll();
    }

    @GetMapping("/settings")
    public PerkSettings getSettings() {
        return repo.getSettings();
    }

    @PutMapping("/bans")
    public void setBans(@RequestBody List<Long> perkIds) {
        repo.setBans(perkIds);
    }

    @PutMapping("/ranking")
    public void setRanking(@RequestBody List<Long> perkIds) {
        repo.setRanking(perkIds);
    }

    @PutMapping("/first-choice")
    public void setFirstChoice(@RequestBody(required = false) Long perkId) {
        repo.setFirstChoice(perkId);
    }
}
