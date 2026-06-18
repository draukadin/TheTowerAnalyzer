package com.pphi.tower.web;

import com.pphi.tower.model.ModuleLevelTable;
import com.pphi.tower.repository.ModuleRepository;
import com.pphi.tower.repository.ModuleRepository.BanState;
import com.pphi.tower.repository.ModuleRepository.ModulePlayerData;
import com.pphi.tower.repository.ModuleRepository.SubstatDef;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@CrossOrigin(origins = "*")
public class ModuleController {

    private final ModuleRepository repo;

    public ModuleController(ModuleRepository repo) {
        this.repo = repo;
    }

    record StateRequest(boolean owned, String rarity, int stars, int level) {}
    record SubstatRequest(String key, String rarity, boolean locked) {}
    record CopyRequest(String rarity) {}
    record ShatteredRequest(int count) {}

    record LevelingCostResponse(int fromLevel, int toLevel, long totalShards, long totalCoins) {}

    @GetMapping("/leveling-cost")
    public LevelingCostResponse getLevelingCost(
            @RequestParam int fromLevel,
            @RequestParam int toLevel) {
        if (fromLevel < 1 || toLevel > ModuleLevelTable.MAX_LEVEL || fromLevel > toLevel) {
            throw new IllegalArgumentException(
                "fromLevel must be >= 1, toLevel must be <= " + ModuleLevelTable.MAX_LEVEL + ", and fromLevel <= toLevel");
        }
        return new LevelingCostResponse(
            fromLevel,
            toLevel,
            ModuleLevelTable.shardsRemainingTo(fromLevel, toLevel),
            ModuleLevelTable.coinsRemainingTo(fromLevel, toLevel)
        );
    }

    @GetMapping
    public List<ModulePlayerData> getAll() {
        return repo.getAll();
    }

    @PutMapping("/{id}/state")
    public void updateState(@PathVariable int id, @RequestBody StateRequest req) {
        repo.updateState(id, req.owned(), req.rarity(), req.stars(), req.level());
    }

    @PutMapping("/{id}/substat/{slot}")
    public void setSubstat(@PathVariable int id, @PathVariable int slot, @RequestBody SubstatRequest req) {
        repo.setSubstat(id, slot, req.key(), req.rarity(), req.locked());
    }

    @DeleteMapping("/{id}/substat/{slot}")
    public void clearSubstat(@PathVariable int id, @PathVariable int slot) {
        repo.clearSubstat(id, slot);
    }

    @PutMapping("/{id}/copy/{copyIndex}")
    public void setCopy(@PathVariable int id, @PathVariable int copyIndex, @RequestBody CopyRequest req) {
        repo.setCopy(id, copyIndex, req.rarity());
    }

    @DeleteMapping("/{id}/copy/{copyIndex}")
    public void clearCopy(@PathVariable int id, @PathVariable int copyIndex) {
        repo.clearCopy(id, copyIndex);
    }

    @PutMapping("/{id}/shattered")
    public void setShattered(@PathVariable int id, @RequestBody ShatteredRequest req) {
        repo.setShatteredEpics(id, req.count());
    }

    @PutMapping("/{id}/preset/{preset}/{slot}")
    public void addPreset(@PathVariable int id, @PathVariable String preset, @PathVariable String slot) {
        repo.addPreset(id, preset, slot);
    }

    @DeleteMapping("/{id}/preset/{preset}/{slot}")
    public void removePreset(@PathVariable int id, @PathVariable String preset, @PathVariable String slot) {
        repo.removePreset(id, preset, slot);
    }

    // ── Substat catalog ───────────────────────────────────────────────────────

    @GetMapping("/substats")
    public Map<String, List<SubstatDef>> getSubstats() {
        return repo.getAllSubstatDefs();
    }

    // ── Effect bans ───────────────────────────────────────────────────────────

    @GetMapping("/bans")
    public List<BanState> getBans() {
        return repo.getAllBanStates();
    }

    @PutMapping("/bans/{moduleType}/{substatKey}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addBan(@PathVariable String moduleType, @PathVariable String substatKey) {
        BanState state = repo.getAllBanStates().stream()
                .filter(b -> b.moduleType().equalsIgnoreCase(moduleType))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Unknown module type: " + moduleType));
        if (state.banned().contains(substatKey)) return;
        if (state.banned().size() >= state.maxBans()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    moduleType + " Effect Bans lab is level " + state.maxBans()
                            + "; all " + state.maxBans() + " ban slot(s) are used");
        }
        repo.addBan(moduleType, substatKey);
    }

    @DeleteMapping("/bans/{moduleType}/{substatKey}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeBan(@PathVariable String moduleType, @PathVariable String substatKey) {
        repo.removeBan(moduleType, substatKey);
    }
}
