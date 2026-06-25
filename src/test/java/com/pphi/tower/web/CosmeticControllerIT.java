package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CosmeticControllerIT extends BaseIntegrationTest {

    @Test
    void getAll_returns200() throws Exception {
        mvc.perform(get("/api/cosmetics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(0)));
    }

    @Test
    void addEvent_returns200() throws Exception {
        mvc.perform(post("/api/cosmetics/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"eventName\": \"Test Event 2026\", \"reroll\": 3, \"towerSkinName\": \"Test Tower Skin\", \"bgSkinName\": \"Test BG Skin\"}"))
                .andExpect(status().isOk());
    }
}
