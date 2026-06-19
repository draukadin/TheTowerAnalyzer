package com.pphi.tower.web;

import com.pphi.tower.repository.GuardianRepository;
import com.pphi.tower.repository.GuardianRepository.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guardian")
@CrossOrigin(origins = "*")
public class GuardianController {

    private final GuardianRepository repo;

    public GuardianController(GuardianRepository repo) {
        this.repo = repo;
    }

    // ── State & chips ─────────────────────────────────────────────────────────

    @GetMapping
    public Map<String, Object> get() {
        return Map.of(
                "unlocked", repo.isGuardianUnlocked(),
                "slots",    repo.getSlots(),
                "chips",    repo.getAll());
    }

    @GetMapping("/level-values")
    public List<StatLevelValues> getAllLevelValues() {
        return repo.getAllLevelValues();
    }

    // ── Player state ──────────────────────────────────────────────────────────

    record UnlockRequest(boolean unlocked) {}

    @PutMapping("/unlocked")
    public void setGuardianUnlocked(@RequestBody UnlockRequest req) {
        repo.setGuardianUnlocked(req.unlocked());
    }

    record SlotUnlockRequest(boolean unlocked) {}

    @PutMapping("/slots/{slotNumber}/unlocked")
    public void setSlotUnlocked(@PathVariable int slotNumber, @RequestBody SlotUnlockRequest req) {
        repo.setSlotUnlocked(slotNumber, req.unlocked());
    }

    record ChipAcquiredRequest(boolean acquired) {}

    @PutMapping("/chips/{chipId}/acquired")
    public void setChipAcquired(@PathVariable long chipId, @RequestBody ChipAcquiredRequest req) {
        repo.setChipAcquired(chipId, req.acquired());
    }

    record CreateChipRequest(String name, String source, Integer unlockCostTokens,
                             Integer unlockSeason, List<StatInput> stats) {}

    @PostMapping("/chips")
    public long createChip(@RequestBody CreateChipRequest req) {
        String code = req.name().toUpperCase().replaceAll("[^A-Z0-9]+", "_");
        return repo.createChip(code, req.name(), req.source(),
                req.unlockSeason(), req.unlockCostTokens(), req.stats());
    }

    record StatLevelRequest(int level) {}

    @PutMapping("/stats/{statId}/level")
    public void setStatLevel(@PathVariable long statId, @RequestBody StatLevelRequest req) {
        repo.setStatLevel(statId, req.level());
    }

    record StatTargetLevelRequest(Integer targetLevel) {}

    @PutMapping("/stats/{statId}/target-level")
    public void setStatTargetLevel(@PathVariable long statId, @RequestBody StatTargetLevelRequest req) {
        repo.setStatTargetLevel(statId, req.targetLevel());
    }

    // ── Presets ───────────────────────────────────────────────────────────────

    @GetMapping("/presets")
    public List<Preset> getPresets() {
        return repo.getPresets();
    }

    record UpsertPresetRequest(int slot, String name) {}

    @PutMapping("/presets")
    public int upsertPreset(@RequestBody UpsertPresetRequest req) {
        return repo.upsertPreset(req.slot(), req.name());
    }

    @DeleteMapping("/presets/{presetId}")
    public void deletePreset(@PathVariable int presetId) {
        repo.deletePreset(presetId);
    }

    @GetMapping("/presets/{presetId}/chips")
    public List<PresetChip> getPresetChips(@PathVariable int presetId) {
        return repo.getPresetChips(presetId);
    }

    record SetPresetChipsRequest(List<PresetChip> chips) {}

    @PutMapping("/presets/{presetId}/chips")
    public void setPresetChips(@PathVariable int presetId, @RequestBody SetPresetChipsRequest req) {
        repo.setPresetChips(presetId, req.chips());
    }

    @GetMapping("/presets/{presetId}/stat-levels")
    public List<PresetStatLevel> getPresetStatLevels(@PathVariable int presetId) {
        return repo.getPresetStatLevels(presetId);
    }

    record SetPresetStatLevelsRequest(List<PresetStatLevel> levels) {}

    @PutMapping("/presets/{presetId}/stat-levels")
    public void setPresetStatLevels(@PathVariable int presetId,
                                     @RequestBody SetPresetStatLevelsRequest req) {
        repo.setPresetStatLevels(presetId, req.levels());
    }
}
