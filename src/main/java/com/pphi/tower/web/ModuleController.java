package com.pphi.tower.web;

import com.pphi.tower.repository.ModuleRepository;
import com.pphi.tower.repository.ModuleRepository.ModulePlayerData;
import org.springframework.web.bind.annotation.*;

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
}
