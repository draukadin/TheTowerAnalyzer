package com.pphi.tower.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.BaseIntegrationTest;
import com.pphi.tower.fixtures.BattleHistoryFixtures;
import com.pphi.tower.fixtures.RunRowFixtures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AnalysisControllerIT extends BaseIntegrationTest {

    @Autowired ObjectMapper objectMapper;

    @BeforeAll
    void insertRuns() throws Exception {
        String payload = objectMapper.writeValueAsString(BattleHistoryFixtures.unknownVariance());
        RunRowFixtures.insertFarmingRun(jdbc, "a1", LocalDate.now().minusDays(1), 10, 3000, 2000.0, 3600L, payload);
        RunRowFixtures.insertFarmingRun(jdbc, "a2", LocalDate.now().minusDays(2), 10, 4000, 3000.0, 5400L, payload);
        RunRowFixtures.insertFarmingRun(jdbc, "a3", LocalDate.now().minusDays(3), 10, 2500, 1800.0, 3000L, payload);
    }

    @Test
    void getCells_returns200WithRuns() throws Exception {
        mvc.perform(get("/api/analysis/cells"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runsAnalyzed").value(greaterThanOrEqualTo(3)));
    }

    @Test
    void getCells_daysParam_clamped() throws Exception {
        mvc.perform(get("/api/analysis/cells").param("days", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.windowDays").value(3));
    }

    @Test
    void getLabSpeed_returns200WithFiveSlots() throws Exception {
        mvc.perform(get("/api/analysis/lab-speed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slots.length()").value(5));
    }

    @Test
    void getGtIncome_returns200() throws Exception {
        mvc.perform(get("/api/analysis/gt-income")
                        .param("gtPlusLevel", "0")
                        .param("gtDurationSec", "30")
                        .param("gtCooldownSec", "60")
                        .param("kps", "1000")
                        .param("totalRunDurationSec", "3600")
                        .param("incomePerMob", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectedIncome").exists());
    }

    @Test
    void getShardSnapshotCount_returns200() throws Exception {
        mvc.perform(get("/api/analysis/shards/snapshot-count"))
                .andExpect(status().isOk());
    }

    @Test
    void getShards_returns200() throws Exception {
        mvc.perform(get("/api/analysis/shards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.windowDays").exists());
    }
}
