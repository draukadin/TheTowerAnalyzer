package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BotControllerIT extends BaseIntegrationTest {

    @Test
    void getAll_returns200AndNonEmptyList() throws Exception {
        mvc.perform(get("/api/bots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void getUnlockCosts_returns200() throws Exception {
        mvc.perform(get("/api/bots/unlock-costs"))
                .andExpect(status().isOk());
    }

    @Test
    void setUnlocked_returns200() throws Exception {
        mvc.perform(put("/api/bots/1/unlocked")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"unlocked\": true, \"unlockOrder\": null}"))
                .andExpect(status().isOk());
    }

    @Test
    void getPresets_returns200() throws Exception {
        mvc.perform(get("/api/bots/presets"))
                .andExpect(status().isOk());
    }
}
