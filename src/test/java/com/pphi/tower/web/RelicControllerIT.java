package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RelicControllerIT extends BaseIntegrationTest {

    @Test
    void getAll_returns200AndNonEmptyList() throws Exception {
        mvc.perform(get("/api/relics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void setOwned_returns200() throws Exception {
        mvc.perform(put("/api/relics/1/owned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"owned\": true}"))
                .andExpect(status().isOk());
    }
}