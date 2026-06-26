package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PerkControllerIT extends BaseIntegrationTest {

    @Test
    void getPerks_returns200AndNonEmptyList() throws Exception {
        mvc.perform(get("/api/perks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void getSettings_returns200() throws Exception {
        mvc.perform(get("/api/perks/settings"))
                .andExpect(status().isOk());
    }
}