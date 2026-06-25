package com.pphi.tower.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.BaseIntegrationTest;
import com.pphi.tower.fixtures.BattleHistoryFixtures;
import com.pphi.tower.fixtures.RunRowFixtures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ReportControllerIT extends BaseIntegrationTest {

    @Autowired ObjectMapper objectMapper;

    private String payload;

    @BeforeAll
    void insertRuns() throws Exception {
        payload = objectMapper.writeValueAsString(BattleHistoryFixtures.unknownVariance());
        RunRowFixtures.insertFarmingRun(jdbc, "rp1", LocalDate.now().minusDays(1), 10, 3000, 1000.0, 3600L, payload);
        RunRowFixtures.insertFarmingRun(jdbc, "rp2", LocalDate.now().minusDays(2), 10, 4000, 2000.0, 5400L, payload);
        RunRowFixtures.insertTournamentRun(jdbc, "rp3", LocalDate.now().minusDays(1), 10, 3000, payload);
    }

    // ── list ─────────────────────────────────────────────────────────────────

    @Test @Order(1)
    void listReports_returns200WithAllRuns() throws Exception {
        mvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(3)));
    }

    @Test @Order(2)
    void listReports_filteredByRunType_returnsFarming() throws Exception {
        mvc.perform(get("/api/reports").param("runType", "farming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].runType").value(everyItem(is("farming"))));
    }

    // ── get by id ────────────────────────────────────────────────────────────

    @Test @Order(3)
    void getReport_knownId_returns200() throws Exception {
        mvc.perform(get("/api/reports/rp1"))
                .andExpect(status().isOk());
    }

    @Test @Order(4)
    void getReport_unknownId_returns404() throws Exception {
        mvc.perform(get("/api/reports/does-not-exist"))
                .andExpect(status().isNotFound());
    }

    // ── diagnosis ────────────────────────────────────────────────────────────

    @Test @Order(5)
    void getDiagnosis_knownId_returns200WithPrimaryFailure() throws Exception {
        mvc.perform(get("/api/reports/rp2/diagnosis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.primaryFailure").exists());
    }

    // ── comparison ───────────────────────────────────────────────────────────

    @Test @Order(6)
    void getComparison_returns3Elements() throws Exception {
        mvc.perform(get("/api/reports/rp1/comparison/rp2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    // ── fetch (mocked drive) ─────────────────────────────────────────────────

    @Test @Order(7)
    void fetchReports_emptyFolder_returns0Processed() throws Exception {
        when(reportFetcherService.processReports()).thenReturn(0);
        mvc.perform(post("/api/reports/fetch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processed").value(0));
    }

    // ── duplicates ───────────────────────────────────────────────────────────

    @Test @Order(8)
    void getDuplicates_returns200() throws Exception {
        mvc.perform(get("/api/reports/duplicates"))
                .andExpect(status().isOk());
    }

    // ── delete ───────────────────────────────────────────────────────────────

    @Test @Order(9)
    void deleteReport_unknownId_returns404() throws Exception {
        mvc.perform(delete("/api/reports/no-such-id"))
                .andExpect(status().isNotFound());
    }

    @Test @Order(10)
    void deleteReport_knownId_returns200() throws Exception {
        mvc.perform(delete("/api/reports/rp1"))
                .andExpect(status().isOk());
    }
}
