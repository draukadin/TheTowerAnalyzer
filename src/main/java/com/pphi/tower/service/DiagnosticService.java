package com.pphi.tower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.analyzers.BattleDiagnostic;
import com.pphi.tower.exceptions.ReportNotFoundException;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.repository.RunRepository;
import org.springframework.stereotype.Service;

@Service
public class DiagnosticService {

    private final RunRepository repository;
    private final ObjectMapper objectMapper;
    private final BattleDiagnostic diagnostic;

    public DiagnosticService(RunRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.diagnostic = new BattleDiagnostic();
    }

    public DiagnosisResult diagnose(String id) {
        String payload = repository.findPayloadById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));
        try {
            BattleHistory history = objectMapper.readValue(payload, BattleHistory.class);
            return diagnostic.analyzeReport(history);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize report " + id, e);
        }
    }
}
