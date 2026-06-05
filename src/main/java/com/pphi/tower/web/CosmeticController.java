package com.pphi.tower.web;

import com.pphi.tower.repository.CosmeticRepository;
import com.pphi.tower.repository.CosmeticRepository.CosmeticItem;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cosmetics")
@CrossOrigin(origins = "*")
public class CosmeticController {

    private final CosmeticRepository repo;

    public CosmeticController(CosmeticRepository repo) {
        this.repo = repo;
    }

    record OwnedRequest(boolean owned) {}

    record AddEventRequest(String eventName, int reroll, String towerSkinName, String bgSkinName) {}

    record AddItemRequest(String categoryId, String name,
                          Integer milestoneNumber, String milestoneTier, String milestoneUnlock) {}

    @GetMapping
    public List<CosmeticItem> getAll() {
        return repo.getAll();
    }

    @PutMapping("/{id}/owned")
    public void setOwned(@PathVariable long id, @RequestBody OwnedRequest req) {
        repo.setOwned(id, req.owned());
    }

    @PostMapping("/events")
    public List<CosmeticItem> addEvent(@RequestBody AddEventRequest req) {
        repo.addEvent(req.eventName(), req.reroll(), req.towerSkinName(), req.bgSkinName());
        return repo.getAll();
    }

    @PostMapping("/items")
    public List<CosmeticItem> addItem(@RequestBody AddItemRequest req) {
        repo.addItem(req.categoryId(), req.name(),
                req.milestoneNumber(), req.milestoneTier(), req.milestoneUnlock());
        return repo.getAll();
    }
}
