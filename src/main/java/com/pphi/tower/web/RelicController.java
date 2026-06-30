package com.pphi.tower.web;

import com.pphi.tower.repository.RelicRepository;
import com.pphi.tower.repository.RelicRepository.RelicData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/relics")
@CrossOrigin(origins = "*")
public class RelicController {

    private final RelicRepository repo;

    public RelicController(RelicRepository repo) {
        this.repo = repo;
    }

    record OwnedRequest(boolean owned) {}
    record CreateRequest(String name, String rarity, String type, String bonusStat,
                         double bonusValue, String obtainCondition) {}

    @GetMapping
    public List<RelicData> getAll() {
        return repo.getAll();
    }

    @PutMapping("/{id}/owned")
    public void setOwned(@PathVariable long id, @RequestBody OwnedRequest req) {
        repo.setOwned(id, req.owned());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequest req) {
        if (repo.nameExists(req.name(), -1)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(duplicateNameMsg(req.name()));
        }
        long id = repo.create(req.name(), req.rarity(), req.type(),
                req.bonusStat(), req.bonusValue(), req.obtainCondition());
        return ResponseEntity.ok(repo.getAll().stream().filter(r -> r.id() == id).findFirst()
                .orElseThrow());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody CreateRequest req) {
        if (repo.nameExists(req.name(), id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(duplicateNameMsg(req.name()));
        }
        repo.update(id, req.name(), req.rarity(), req.type(),
                req.bonusStat(), req.bonusValue(), req.obtainCondition());
        return ResponseEntity.ok(repo.getAll().stream().filter(r -> r.id() == id).findFirst()
                .orElseThrow());
    }

    private static String duplicateNameMsg(String name) {
        return "A relic named \"" + name + "\" already exists. Please choose a different name.";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        if (repo.isInGemStoreRotation(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("This relic is part of the Gem Store rotation and cannot be deleted.");
        }
        repo.delete(id);
        return ResponseEntity.noContent().build();
    }
}
