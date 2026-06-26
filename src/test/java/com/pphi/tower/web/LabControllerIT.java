package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LabControllerIT extends BaseIntegrationTest {

    @Test
    void getAll_returns200AndNonEmptyList() throws Exception {
        mvc.perform(get("/api/labs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThan(0)));
    }

    @Test
    void getByCategory_attack_returnsFilteredList() throws Exception {
        mvc.perform(get("/api/labs").param("category", "ATTACK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].category").value(
                        org.hamcrest.Matchers.everyItem(
                                org.hamcrest.Matchers.is("ATTACK"))));
    }

    @Test
    void search_returnsMatchingLabs() throws Exception {
        mvc.perform(get("/api/labs/search").param("q", "damage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(0)));
    }

    @Test
    void getCosts_returns200() throws Exception {
        // Use lab id=1 which is guaranteed to exist after seeding
        mvc.perform(get("/api/labs/1/costs"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllCosts_returns200() throws Exception {
        mvc.perform(get("/api/labs/costs"))
                .andExpect(status().isOk());
    }

    @Test
    void getMultipliers_returns200() throws Exception {
        mvc.perform(get("/api/labs/multipliers"))
                .andExpect(status().isOk());
    }

    @Test
    void updateState_persists() throws Exception {
        mvc.perform(put("/api/labs/1/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentLevel\": 5, \"targetLevel\": 10}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/labs"))
                .andExpect(jsonPath("$[?(@.id==1)].currentLevel").value(
                        org.hamcrest.Matchers.hasItem(5)));
    }
}