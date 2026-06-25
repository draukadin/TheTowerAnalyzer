package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TournamentControllerIT extends BaseIntegrationTest {

    @Test
    void getAll_returns200() throws Exception {
        mvc.perform(get("/api/tournaments"))
                .andExpect(status().isOk());
    }

    @Test
    void getConditions_returns200() throws Exception {
        mvc.perform(get("/api/tournaments/conditions"))
                .andExpect(status().isOk());
    }

    @Test
    void createAndRetrieve() throws Exception {
        mvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\": \"2026-06-25\", \"league\": \"GOLD\", \"conditionIds\": []}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/tournaments"))
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }
}
