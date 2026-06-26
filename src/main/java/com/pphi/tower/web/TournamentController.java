package com.pphi.tower.web;

import com.pphi.tower.repository.TournamentRepository;
import com.pphi.tower.repository.TournamentRepository.BattleConditionData;
import com.pphi.tower.repository.TournamentRepository.TournamentData;
import com.pphi.tower.service.TournamentImportService;
import com.pphi.tower.service.TournamentImportService.ImportResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "*")
public class TournamentController {

    private final TournamentRepository repo;
    private final TournamentImportService importService;

    public TournamentController(TournamentRepository repo, TournamentImportService importService) {
        this.repo = repo;
        this.importService = importService;
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

    @PostMapping(value = "/import/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResult importCsv(@RequestParam("date") String date,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        String csvContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        return importService.importAndUpload(date, csvContent);
    }

    @PostMapping("/sync")
    public List<ImportResult> syncFromS3() throws IOException {
        return importService.syncFromS3();
    }

    @GetMapping("/fetch-from-s3")
    public ResponseEntity<ImportResult> fetchFromS3(@RequestParam("date") String date) throws IOException {
        return importService.fetchDateFromS3(date)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
