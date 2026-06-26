package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CardControllerIT extends BaseIntegrationTest {

    @Test
    void getAll_returns200AndNonEmptyList() throws Exception {
        mvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void updateStarLevel_returns200() throws Exception {
        mvc.perform(put("/api/cards/1/star-level")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"starLevel\": 2}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateCopiesOwned_returns200() throws Exception {
        mvc.perform(put("/api/cards/1/copies-owned")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"copiesOwned\": 5}"))
                .andExpect(status().isOk());
    }

    @Test
    void getSlots_returns200() throws Exception {
        mvc.perform(get("/api/cards/slots"))
                .andExpect(status().isOk());
    }

    @Test
    void getPresets_returns200() throws Exception {
        mvc.perform(get("/api/cards/presets"))
                .andExpect(status().isOk());
    }
}
