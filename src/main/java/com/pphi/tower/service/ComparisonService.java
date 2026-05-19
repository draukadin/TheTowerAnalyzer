package com.pphi.tower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.analyzers.RunComparison;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.repository.RunRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComparisonService {

    private final RunRepository repository;
    private final ObjectMapper objectMapper;
    private final RunComparison runComparison;

    public ComparisonService(RunRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        // RunComparison only needs the parser for its path-based overload; we use the
        // object-based overload directly, so pass null for the parser.
        this.runComparison = new RunComparison(null);
    }

    public List<BattleHistory> compare(String id1, String id2) {
        BattleHistory h1 = load(id1);
        BattleHistory h2 = load(id2);
        return runComparison.compareBattles(h1, h2);
    }

    private BattleHistory load(String id) {
        String payload = repository.findPayloadById(id)
                .orElseThrow(() -> new ReportNotFoundException(id));
        try {
            return objectMapper.readValue(payload, BattleHistory.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize report " + id, e);
        }
    }
}
