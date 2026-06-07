package com.pphi.tower.web;

import com.pphi.tower.repository.CardRepository;
import com.pphi.tower.repository.CardRepository.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
