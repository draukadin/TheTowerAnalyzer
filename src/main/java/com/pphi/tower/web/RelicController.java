package com.pphi.tower.web;

import com.pphi.tower.repository.RelicRepository;
import com.pphi.tower.repository.RelicRepository.RelicData;
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
    public RelicData create(@RequestBody CreateRequest req) {
        long id = repo.create(req.name(), req.rarity(), req.type(),
                req.bonusStat(), req.bonusValue(), req.obtainCondition());
        return repo.getAll().stream().filter(r -> r.id() == id).findFirst()
                .orElseThrow();
    }
}
