package com.pphi.tower.web;

import com.pphi.tower.repository.LabSlotRepository;
import com.pphi.tower.repository.LabSlotRepository.SlotData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lab-slots")
@CrossOrigin(origins = "*")
public class LabSlotController {

    private final LabSlotRepository repo;

    public LabSlotController(LabSlotRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<SlotData> getAll() {
        return repo.getAllSlots();
    }

    @PutMapping("/{slotNumber}/speed")
    public void updateSpeed(@PathVariable int slotNumber, @RequestBody SpeedRequest req) {
        repo.updateCellSpeed(slotNumber, req.cellSpeedMult());
    }

    @PostMapping("/{slotNumber}/plans")
    public void addPlan(@PathVariable int slotNumber, @RequestBody PlanRequest req) {
        repo.addPlan(slotNumber, req.labId(), req.startLevel(), req.targetLevel());
    }

    @DeleteMapping("/{slotNumber}/plans/{planId}")
    public void deletePlan(@PathVariable int slotNumber, @PathVariable long planId) {
        repo.deletePlan(planId);
    }

    @PutMapping("/{slotNumber}/plans/{planId}")
    public void updatePlan(@PathVariable int slotNumber, @PathVariable long planId,
                           @RequestBody PlanRequest req) {
        repo.updatePlan(planId, req.startLevel(), req.targetLevel());
    }

    @PutMapping("/{slotNumber}/plans/{planId}/move")
    public void movePlan(@PathVariable int slotNumber, @PathVariable long planId,
                         @RequestBody MoveRequest req) {
        repo.movePlan(planId, req.direction());
    }

    record SpeedRequest(double cellSpeedMult) {}
    record PlanRequest(long labId, int startLevel, int targetLevel) {}
    record MoveRequest(String direction) {}
}
