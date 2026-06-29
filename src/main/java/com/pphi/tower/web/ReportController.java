package com.pphi.tower.web;

import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.config.AwsProperties;
import com.pphi.tower.repository.GoogleDriveRepository;
import com.pphi.tower.repository.RunRepository;
import com.pphi.tower.repository.TournamentRepository;
import com.pphi.tower.service.ComparisonService;
import com.pphi.tower.service.DiagnosticService;
import com.pphi.tower.service.ReportFetcherService;
import com.pphi.tower.repository.S3ReportRepository;
import com.pphi.tower.service.S3ReportFetcherService;
import com.pphi.tower.exceptions.ReportNotFoundException;
import com.pphi.tower.web.dto.ReportSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final GoogleDriveRepository googleDriveRepository;
    private final AwsProperties awsProperties;
    private final TournamentRepository tournamentRepository;

    @Autowired(required = false)
    private S3ReportFetcherService s3ReportFetcherService;

    @Autowired(required = false)
    private S3ReportRepository s3ReportRepository;

    public ReportController(RunRepository repository,
                            DiagnosticService diagnosticService,
                            ComparisonService comparisonService,
                            ReportFetcherService reportFetcherService,
                            GoogleDriveRepository googleDriveRepository,
                            AwsProperties awsProperties,
                            TournamentRepository tournamentRepository) {
        this.repository = repository;
        this.diagnosticService = diagnosticService;
        this.comparisonService = comparisonService;
        this.reportFetcherService = reportFetcherService;
        this.googleDriveRepository = googleDriveRepository;
        this.awsProperties = awsProperties;
        this.tournamentRepository = tournamentRepository;
    }

    @PostMapping("/fetch")
    public ResponseEntity<Map<String, Object>> fetchReports() {
        int count = (s3ReportFetcherService != null && awsProperties.isConfigured())
                ? s3ReportFetcherService.processReports()
                : reportFetcherService.processReports();
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

    @GetMapping("/duplicates")
    public List<RunRepository.DuplicateGroup> getDuplicates() {
        return repository.findDuplicateGroups();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteReport(
            @PathVariable String id,
            @RequestParam(defaultValue = "false") boolean deleteSourceFile) throws Exception {
        if (!repository.existsById(id)) {
            throw new ReportNotFoundException(id);
        }
        if (deleteSourceFile) {
            if (s3ReportFetcherService != null && awsProperties.isConfigured()) {
                s3ReportRepository.deleteObject(
                        awsProperties.getS3Bucket(),
                        awsProperties.getPlayerId() + "/" + id);
            } else {
                googleDriveRepository.deleteFile(id);
            }
        }
        repository.deleteById(id);
        return ResponseEntity.ok(Map.of("deleted", id, "sourceFileDeleted", deleteSourceFile));
    }

    @GetMapping("/{id}/tournament-conditions")
    public List<TournamentRepository.BattleConditionData> getTournamentConditions(@PathVariable String id) {
        return tournamentRepository.findConditionsByRunId(id);
    }

    @PutMapping("/{id}/tournament/{tournamentId}")
    public ResponseEntity<Map<String, Object>> linkTournament(
            @PathVariable String id,
            @PathVariable long tournamentId) {
        if (!repository.existsById(id)) throw new ReportNotFoundException(id);
        repository.setTournamentId(id, tournamentId);
        return ResponseEntity.ok(Map.of("runId", id, "tournamentId", tournamentId));
    }

    @DeleteMapping("/{id}/tournament")
    public ResponseEntity<Map<String, Object>> unlinkTournament(@PathVariable String id) {
        if (!repository.existsById(id)) throw new ReportNotFoundException(id);
        repository.clearTournamentId(id);
        return ResponseEntity.ok(Map.of("runId", id, "unlinked", true));
    }

    @GetMapping("/compare")
    public List<BattleHistory> compareByNumber(
            @RequestParam int n1,
            @RequestParam int n2) {
        String id1 = repository.findIdByRunNumber(n1)
                .orElseThrow(() -> new ReportNotFoundException("run #" + n1));
        String id2 = repository.findIdByRunNumber(n2)
                .orElseThrow(() -> new ReportNotFoundException("run #" + n2));
        return comparisonService.compare(id1, id2);
    }

}
