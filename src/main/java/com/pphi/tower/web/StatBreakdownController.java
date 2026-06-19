package com.pphi.tower.web;

import com.pphi.tower.repository.StatBreakdownRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatBreakdownController {

    private final StatBreakdownRepository repo;

    public StatBreakdownController(StatBreakdownRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/breakdown")
    public Map<String, StatBreakdownRepository.StatBreakdown> getBreakdown() {
        return repo.getBreakdown();
    }

    @GetMapping("/summary")
    public Map<String, StatBreakdownRepository.StatSummary> getSummary() {
        return repo.getSummary();
    }
}
