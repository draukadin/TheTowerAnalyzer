package com.pphi.tower.web;

import com.pphi.tower.repository.WorkshopRepository;
import com.pphi.tower.repository.WorkshopRepository.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workshop")
@CrossOrigin(origins = "*")
public class WorkshopController {

    private final WorkshopRepository repo;

    public WorkshopController(WorkshopRepository repo) {
        this.repo = repo;
    }

    // ── Items ─────────────────────────────────────────────────────────────────

    @GetMapping
    public List<WorkshopItem> getAll() {
        return repo.getAll();
    }

    @GetMapping("/{id}/costs")
    public List<WorkshopLevelCost> getCosts(@PathVariable long id) {
        return repo.getCosts(id);
    }

    record LevelRequest(int level) {}

    @PutMapping("/{id}/level")
    public void updateLevel(@PathVariable long id, @RequestBody LevelRequest req) {
        repo.updateLevel(id, req.level());
    }

    // ── Workshop (non-plus) unlock groups ─────────────────────────────────────

    @PostMapping("/unlock-groups/{groupId}/purchase")
    public void purchaseUnlockGroup(@PathVariable long groupId) {
        repo.purchaseUnlockGroup(groupId);
    }

    // ── Workshop+ unlock progress ─────────────────────────────────────────────

    @GetMapping("/plus/spend")
    public List<PlusCategorySpend> getPlusCategorySpend() {
        return repo.getPlusCategorySpend();
    }

    @GetMapping("/plus/unlock-progress")
    public List<PlusUnlockProgress> getPlusUnlockProgress() {
        return repo.getPlusUnlockProgress();
    }

    // ── Discounts ─────────────────────────────────────────────────────────────

    @GetMapping("/discounts")
    public WorkshopDiscounts getDiscounts() {
        return repo.getDiscounts();
    }

    // ── Presets ───────────────────────────────────────────────────────────────

    @GetMapping("/presets/unlocks")
    public PresetUnlock getPresetUnlocks() {
        return repo.getPresetUnlocks();
    }

    record PresetUnlockRequest(boolean unlocked) {}

    @PutMapping("/presets/unlocks/{isPlus}")
    public void setPresetUnlocked(@PathVariable boolean isPlus,
                                   @RequestBody PresetUnlockRequest req) {
        repo.setPresetUnlocked(isPlus, req.unlocked());
    }

    @GetMapping("/presets")
    public List<Preset> getPresets(@RequestParam boolean isPlus) {
        return repo.getPresets(isPlus);
    }

    record UpsertPresetRequest(int slot, String name) {}

    @PutMapping("/presets")
    public int upsertPreset(@RequestParam boolean isPlus,
                             @RequestBody UpsertPresetRequest req) {
        return repo.upsertPreset(isPlus, req.slot(), req.name());
    }

    @DeleteMapping("/presets/{presetId}")
    public void deletePreset(@PathVariable int presetId) {
        repo.deletePreset(presetId);
    }

    @GetMapping("/presets/{presetId}/items")
    public List<PresetItem> getPresetItems(@PathVariable int presetId) {
        return repo.getPresetItems(presetId);
    }

    record SetPresetItemsRequest(List<PresetItem> items) {}

    @PutMapping("/presets/{presetId}/items")
    public void setPresetItems(@PathVariable int presetId,
                                @RequestBody SetPresetItemsRequest req) {
        repo.setPresetItems(presetId, req.items());
    }
}
