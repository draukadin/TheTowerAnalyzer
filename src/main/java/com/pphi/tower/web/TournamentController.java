package com.pphi.tower.web;

import com.pphi.tower.repository.TournamentRepository;
import com.pphi.tower.repository.TournamentRepository.BattleConditionData;
import com.pphi.tower.repository.TournamentRepository.TournamentData;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "*")
public class TournamentController {

    private final TournamentRepository repo;

    public TournamentController(TournamentRepository repo) {
        this.repo = repo;
    }

    record SaveTournamentRequest(String date, String league, List<Long> conditionIds) {}
    record CreateConditionRequest(String name, String acronym, String category) {}
    record FindByConditionsRequest(List<Long> conditionIds) {}

    @GetMapping("/conditions")
    public List<BattleConditionData> getAllConditions() {
        return repo.getAllConditions();
    }

    @PostMapping("/conditions")
    public BattleConditionData createCondition(@RequestBody CreateConditionRequest req) {
        long id = repo.createCondition(req.name(), req.acronym(), req.category());
        return repo.getAllConditions().stream().filter(c -> c.id() == id).findFirst()
                .orElseThrow();
    }

    @GetMapping
    public List<TournamentData> getAll() {
        return repo.getAll();
    }

    @PostMapping
    public TournamentData save(@RequestBody SaveTournamentRequest req) {
        long id = repo.save(req.date(), req.league(), req.conditionIds());
        return repo.getAll().stream().filter(t -> t.id() == id).findFirst()
                .orElseThrow();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        repo.delete(id);
    }

    @PostMapping("/search")
    public List<TournamentData> findByConditions(@RequestBody FindByConditionsRequest req) {
        return repo.findByConditions(req.conditionIds());
    }
}
