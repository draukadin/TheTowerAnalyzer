package com.pphi.tower.web;

import com.pphi.tower.repository.CardRepository;
import com.pphi.tower.repository.CardRepository.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class CardController {

    private final CardRepository repo;

    public CardController(CardRepository repo) {
        this.repo = repo;
    }

    // ── Cards ─────────────────────────────────────────────────────────────────

    @GetMapping
    public List<CardData> getAll() {
        return repo.getAllCards();
    }

    // ── Player state ──────────────────────────────────────────────────────────

    record StarLevelRequest(int starLevel) {}

    @PutMapping("/{id}/star-level")
    public void updateStarLevel(@PathVariable long id, @RequestBody StarLevelRequest req) {
        if (req.starLevel() < 1 || req.starLevel() > 7) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "starLevel must be between 1 and 7");
        }
        repo.updateStarLevel(id, req.starLevel());
    }

    record CopiesOwnedRequest(int copiesOwned) {}

    @PutMapping("/{id}/copies-owned")
    public void updateCopiesOwned(@PathVariable long id, @RequestBody CopiesOwnedRequest req) {
        if (req.copiesOwned() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "copiesOwned must be >= 0");
        }
        repo.updateCopiesOwned(id, req.copiesOwned());
    }

    record MasteryLevelRequest(int masteryLevel) {}

    @PutMapping("/{id}/mastery-level")
    public void updateMasteryLevel(@PathVariable long id, @RequestBody MasteryLevelRequest req) {
        if (req.masteryLevel() < 0 || req.masteryLevel() > 9) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "masteryLevel must be between 0 and 9");
        }
        int labLevel = repo.getMasteryLabLevel(id);
        if (req.masteryLevel() > labLevel) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "masteryLevel cannot exceed the mastery lab level (" + labLevel + ")");
        }
        repo.updateMasteryLevel(id, req.masteryLevel());
    }

    // ── Card details ──────────────────────────────────────────────────────────

    /** Copies required to advance TO each star level (index = target star, 1-based; index 1 = 0). */
    private static final int[] COPIES_TO_REACH_STAR = {0, 0, 1, 2, 3, 4, 6, 8};
    private static final int GEM_COST_PER_COPY = 20;

    record MasteryDetails(
            String  description,
            String  valueUnit,
            int     stoneCostPerLevel,
            boolean isUnlocked,
            int     currentLevel,
            int     maxLevel,
            double  currentValue,
            List<Double> valuesByLevel,
            int     stonesRemainingToMax
    ) {}

    record PresetEquipped(int presetId, int presetSlot, String presetName, int cardSlot) {}

    record CardDetailsResponse(
            String  name,
            String  rarity,
            String  description,
            String  valueUnit,
            Integer milestoneUnlockTier,
            Integer milestoneUnlockWave,
            int     starLevel,
            double  currentValue,
            List<Double> statsByLevel,
            int     copiesOwned,
            int     copiesForNextStar,
            int     copiesRemainingForMax,
            int     gemCostToMax,
            MasteryDetails mastery,
            List<PresetEquipped> presetsEquipped
    ) {}

    @GetMapping("/by-name/{name}/details")
    public CardDetailsResponse getCardDetails(@PathVariable String name) {
        CardData c = repo.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Card not found: " + name));

        List<Double> statsByLevel = Arrays.asList(
                c.level1(), c.level2(), c.level3(), c.level4(),
                c.level5(), c.level6(), c.level7());
        double currentValue = statsByLevel.get(c.starLevel() - 1);

        // Copies toward the next star (capped at what's needed for the next star)
        int copiesForNextStar = c.starLevel() < 7
                ? Math.min(c.copiesOwned(), COPIES_TO_REACH_STAR[c.starLevel() + 1])
                : 0;

        // Total copies still needed for stars beyond current, minus what's already owned
        int totalCopiesNeededFromNow = 0;
        for (int star = c.starLevel() + 1; star <= 7; star++) {
            totalCopiesNeededFromNow += COPIES_TO_REACH_STAR[star];
        }
        int copiesRemainingForMax = Math.max(0, totalCopiesNeededFromNow - c.copiesOwned());
        int gemCostToMax = copiesRemainingForMax * GEM_COST_PER_COPY;

        // Mastery
        // DB fields mastery_level_0..8 correspond to player research levels 1..9.
        // mastery_level_9 is reserved for a future level 10 and is not surfaced here.
        // Player mastery_level=0 means not yet researched → value is 1.0 (multiplicative no-op).
        List<Double> masteryValuesByLevel = Arrays.asList(
                1.0,
                c.masteryLevel0(), c.masteryLevel1(), c.masteryLevel2(),
                c.masteryLevel3(), c.masteryLevel4(), c.masteryLevel5(),
                c.masteryLevel6(), c.masteryLevel7(), c.masteryLevel8());
        boolean masteryUnlocked = c.masteryLabLevel() > 0;
        double masteryCurrentValue = masteryValuesByLevel.get(c.masteryLabLevel());
        int stonesRemainingToMax = (9 - c.masteryLabLevel()) * c.masteryStoneCost();
        MasteryDetails mastery = new MasteryDetails(
                c.masteryDescription(), c.masteryValueUnit(), c.masteryStoneCost(),
                masteryUnlocked, c.masteryLabLevel(), 9,
                masteryCurrentValue, masteryValuesByLevel, stonesRemainingToMax);

        // Presets
        List<PresetEquipped> presetsEquipped = repo.findPresetsByCardId(c.id()).stream()
                .map(p -> new PresetEquipped(p.presetId(), p.presetSlot(), p.presetName(), p.slotNumber()))
                .toList();

        return new CardDetailsResponse(
                c.name(), c.rarity(), c.description(), c.valueUnit(),
                c.milestoneUnlockTier(), c.milestoneUnlockWave(),
                c.starLevel(), currentValue, statsByLevel,
                c.copiesOwned(), copiesForNextStar, copiesRemainingForMax, gemCostToMax,
                mastery, presetsEquipped);
    }

    // ── Slots ─────────────────────────────────────────────────────────────────

    @GetMapping("/slots")
    public List<CardSlot> getAllSlots() {
        return repo.getAllSlots();
    }

    record SlotOwnedRequest(boolean owned) {}

    @PutMapping("/slots/{slotNumber}/owned")
    public void updateSlotOwned(@PathVariable int slotNumber, @RequestBody SlotOwnedRequest req) {
        if (slotNumber < 1 || slotNumber > 28) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "slotNumber must be between 1 and 28");
        }
        if (slotNumber == 1 && !req.owned()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slot 1 is always owned and cannot be marked unowned");
        }
        repo.updateSlotOwned(slotNumber, req.owned());
    }

    // ── Presets ───────────────────────────────────────────────────────────────

    @GetMapping("/presets")
    public List<CardPreset> getAllPresets() {
        return repo.getAllPresets();
    }

    record CreatePresetRequest(int slot, String name) {}

    @PostMapping("/presets")
    @ResponseStatus(HttpStatus.CREATED)
    public CardPreset createPreset(@RequestBody CreatePresetRequest req) {
        if (req.slot() < 2 || req.slot() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "slot must be between 2 and 5 (slot 1 is the default)");
        }
        boolean slotTaken = repo.getAllPresets().stream().anyMatch(p -> p.slot() == req.slot());
        if (slotTaken) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A preset already exists in slot " + req.slot());
        }
        int id = repo.createPreset(req.slot(), req.name() != null ? req.name() : "Preset " + req.slot());
        return repo.getAllPresets().stream().filter(p -> p.id() == id).findFirst().orElseThrow();
    }

    record RenamePresetRequest(String name) {}

    @PutMapping("/presets/{presetId}/name")
    public void renamePreset(@PathVariable int presetId, @RequestBody RenamePresetRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not be blank");
        }
        repo.renamePreset(presetId, req.name());
    }

    @DeleteMapping("/presets/{presetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePreset(@PathVariable int presetId) {
        CardPreset preset = repo.getAllPresets().stream()
                .filter(p -> p.id() == presetId)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Preset not found"));
        if (preset.slot() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The default preset (slot 1) cannot be deleted");
        }
        repo.deletePreset(presetId);
    }

    // ── Preset assignments ────────────────────────────────────────────────────

    @GetMapping("/presets/{presetId}/assignments")
    public List<PresetAssignment> getAssignments(@PathVariable int presetId) {
        return repo.getAssignments(presetId);
    }

    record AssignCardRequest(long cardId) {}

    @PutMapping("/presets/{presetId}/assignments/{slotNumber}")
    public void assignCard(@PathVariable int presetId,
                           @PathVariable int slotNumber,
                           @RequestBody AssignCardRequest req) {
        if (slotNumber < 1 || slotNumber > 28) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "slotNumber must be between 1 and 28");
        }
        repo.assignCard(presetId, slotNumber, req.cardId());
    }

    @DeleteMapping("/presets/{presetId}/assignments/{slotNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignSlot(@PathVariable int presetId, @PathVariable int slotNumber) {
        repo.unassignSlot(presetId, slotNumber);
    }

    @DeleteMapping("/presets/{presetId}/assignments/by-card/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignCard(@PathVariable int presetId, @PathVariable long cardId) {
        repo.unassignCard(presetId, cardId);
    }

    record SetAssignmentsRequest(List<PresetAssignment> assignments) {}

    @PutMapping("/presets/{presetId}/assignments")
    public void setAssignments(@PathVariable int presetId,
                                @RequestBody SetAssignmentsRequest req) {
        repo.setAssignments(presetId, req.assignments());
    }
}
