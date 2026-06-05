package com.pphi.tower.web;

import com.pphi.tower.repository.GemStoreRelicRotationRepository;
import com.pphi.tower.repository.GemStoreRelicRotationRepository.RotationEntry;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gem-store")
@CrossOrigin(origins = "*")
public class GemStoreController {

    private final GemStoreRelicRotationRepository repo;

    public GemStoreController(GemStoreRelicRotationRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/rotation")
    public List<RotationEntry> getAll() {
        return repo.getAll();
    }

    record AddRotationRequest(String startDate, String standard1, String standard2,
                              String premium1, String premium2, String variant) {}

    @PostMapping("/rotation")
    public void addRotation(@RequestBody AddRotationRequest req) {
        String v = req.variant() != null ? req.variant() : "";
        if (v.isEmpty()) {
            repo.addRotation(req.startDate(), req.standard1(), req.standard2(),
                    req.premium1(), req.premium2());
        } else {
            repo.addEntry(req.startDate(), "STANDARD_1", req.standard1(), v);
            repo.addEntry(req.startDate(), "STANDARD_2", req.standard2(), v);
            repo.addEntry(req.startDate(), "PREMIUM_1",  req.premium1(),  v);
            repo.addEntry(req.startDate(), "PREMIUM_2",  req.premium2(),  v);
        }
    }
}
