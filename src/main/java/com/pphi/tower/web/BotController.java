package com.pphi.tower.web;

import com.pphi.tower.repository.BotRepository;
import com.pphi.tower.repository.BotRepository.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bots")
@CrossOrigin(origins = "*")
public class BotController {

    private final BotRepository repo;

    public BotController(BotRepository repo) {
        this.repo = repo;
    }

    // ── Bots & stats ──────────────────────────────────────────────────────────

    @GetMapping
    public List<BotDef> getAll() {
        return repo.getAll();
    }

    @GetMapping("/unlock-costs")
    public int[] getUnlockCosts() {
        return BotRepository.UNLOCK_COSTS;
    }

    @GetMapping("/stats/{statId}/levels")
    public List<LevelValue> getLevelValues(@PathVariable long statId) {
        return repo.getLevelValues(statId);
    }

    @GetMapping("/level-values")
    public List<StatLevelValues> getAllLevelValues() {
        return repo.getAllLevelValues();
    }

    // ── Player state ──────────────────────────────────────────────────────────

    record UnlockRequest(boolean unlocked, Integer unlockOrder) {}

    @PutMapping("/{botId}/unlocked")
    public void setUnlocked(@PathVariable long botId, @RequestBody UnlockRequest req) {
        repo.setUnlocked(botId, req.unlocked(), req.unlockOrder());
    }

    record BotPlusRequest(boolean botPlusUnlocked) {}

    @PutMapping("/{botId}/bot-plus-unlocked")
    public void setBotPlusUnlocked(@PathVariable long botId, @RequestBody BotPlusRequest req) {
        repo.setBotPlusUnlocked(botId, req.botPlusUnlocked());
    }

    record StatLevelRequest(int level) {}

    @PutMapping("/stats/{statId}/level")
    public void setStatLevel(@PathVariable long statId, @RequestBody StatLevelRequest req) {
        repo.setStatLevel(statId, req.level());
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

    @GetMapping("/presets/{presetId}/unlocks")
    public List<PresetBotUnlock> getPresetUnlocks(@PathVariable int presetId) {
        return repo.getPresetUnlocks(presetId);
    }

    record SetPresetUnlocksRequest(List<PresetBotUnlock> unlocks) {}

    @PutMapping("/presets/{presetId}/unlocks")
    public void setPresetUnlocks(@PathVariable int presetId,
                                  @RequestBody SetPresetUnlocksRequest req) {
        repo.setPresetUnlocks(presetId, req.unlocks());
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
