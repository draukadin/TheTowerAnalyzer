package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LabSlotControllerIT extends BaseIntegrationTest {

    @Test
    void getSlots_returns5Slots() throws Exception {
        mvc.perform(get("/api/lab-slots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(5)));
    }

    @Test
    void updateSlotSpeed_returns200() throws Exception {
        mvc.perform(put("/api/lab-slots/1/speed")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"speedMultiplier\": \"x2\"}"))
                .andExpect(status().isOk());
    }
}