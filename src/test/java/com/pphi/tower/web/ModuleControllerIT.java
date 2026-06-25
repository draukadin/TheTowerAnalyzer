package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ModuleControllerIT extends BaseIntegrationTest {

    @Test
    void getAll_returns200AndNonEmptyList() throws Exception {
        mvc.perform(get("/api/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void getLevelingCost_withParams_returns200() throws Exception {
        mvc.perform(get("/api/modules/leveling-cost")
                        .param("fromLevel", "1").param("toLevel", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getSubstats_returns200() throws Exception {
        mvc.perform(get("/api/modules/substats"))
                .andExpect(status().isOk());
    }

    @Test
    void getBans_returns200() throws Exception {
        mvc.perform(get("/api/modules/bans"))
                .andExpect(status().isOk());
    }

    @Test
    void updateState_returns200() throws Exception {
        mvc.perform(put("/api/modules/1/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"owned\": true, \"rarity\": \"RARE\", \"stars\": 1, \"level\": 1}"))
                .andExpect(status().isOk());
    }
}
