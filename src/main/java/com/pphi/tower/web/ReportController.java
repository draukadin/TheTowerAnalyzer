package com.pphi.tower.web;

import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.repository.RunRepository;
import com.pphi.tower.service.ComparisonService;
import com.pphi.tower.service.DiagnosticService;
import com.pphi.tower.service.ReportFetcherService;
import com.pphi.tower.exceptions.ReportNotFoundException;
import com.pphi.tower.web.dto.ReportSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final RunRepository repository;
    private final DiagnosticService diagnosticService;
    private final ComparisonService comparisonService;
    private final ReportFetcherService reportFetcherService;

    public ReportController(RunRepository repository,
                            DiagnosticService diagnosticService,
                            ComparisonService comparisonService,
                            ReportFetcherService reportFetcherService) {
        this.repository = repository;
        this.diagnosticService = diagnosticService;
        this.comparisonService = comparisonService;
        this.reportFetcherService = reportFetcherService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<Map<String, Object>> fetchReports() {
        int count = reportFetcherService.processReports();
        return ResponseEntity.ok(Map.of("processed", count));
    }

    @GetMapping
    public List<ReportSummaryDto> listReports(
            @RequestParam(required = false) String runType) {
        if (runType != null && !runType.isBlank()) {
            return repository.findByRunType(runType);
        }
        return repository.findAllSummaries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getReport(@PathVariable String id) {
        return repository.findPayloadById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ReportNotFoundException(id));
    }

    @GetMapping("/{id}/diagnosis")
    public DiagnosisResult getDiagnosis(@PathVariable String id) {
        return diagnosticService.diagnose(id);
    }

    @GetMapping("/{id}/comparison/{id2}")
    public List<BattleHistory> getComparison(
            @PathVariable String id,
            @PathVariable String id2) {
        return comparisonService.compare(id, id2);
    }
}
