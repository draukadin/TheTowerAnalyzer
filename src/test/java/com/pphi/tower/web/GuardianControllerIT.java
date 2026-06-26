package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GuardianControllerIT extends BaseIntegrationTest {

    @Test
    void get_returns200() throws Exception {
        mvc.perform(get("/api/guardian"))
                .andExpect(status().isOk());
    }

    @Test
    void setUnlocked_returns200() throws Exception {
        mvc.perform(put("/api/guardian/unlocked")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"unlocked\": true}"))
                .andExpect(status().isOk());
    }

    @Test
    void getPresets_returns200() throws Exception {
        mvc.perform(get("/api/guardian/presets"))
                .andExpect(status().isOk());
    }
}