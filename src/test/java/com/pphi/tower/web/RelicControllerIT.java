package com.pphi.tower.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RelicControllerIT extends BaseIntegrationTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    @Test
    void create_update_delete_roundtrip() throws Exception {
        // Create
        String created = mvc.perform(post("/api/relics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Relic IT","rarity":"Rare","type":"Standard",
                                 "bonusStat":"Coins","bonusValue":0.02,"obtainCondition":"test"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Relic IT"))
                .andReturn().getResponse().getContentAsString();
        long id = MAPPER.readTree(created).get("id").asLong();

        // Update
        mvc.perform(put("/api/relics/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Test Relic IT Edited","rarity":"Epic","type":"Premium",
                                 "bonusStat":"Damage","bonusValue":0.05,"obtainCondition":"edited"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Relic IT Edited"))
                .andExpect(jsonPath("$.rarity").value("Epic"));

        // Delete
        mvc.perform(delete("/api/relics/" + id))
                .andExpect(status().isNoContent());

        // Gone
        String all = mvc.perform(get("/api/relics"))
                .andReturn().getResponse().getContentAsString();
        for (JsonNode r : MAPPER.readTree(all)) {
            if (r.get("id").asLong() == id) {
                throw new AssertionError("Relic " + id + " should have been deleted");
            }
        }
    }

    @Test
    void create_duplicateName_returns409() throws Exception {
        String body = """
                {"name":"Dup Relic IT","rarity":"Rare","type":"Standard",
                 "bonusStat":"Coins","bonusValue":0.02,"obtainCondition":"test"}""";
        mvc.perform(post("/api/relics").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mvc.perform(post("/api/relics").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("already exists")));
    }

    @Test
    void update_toExistingName_returns409() throws Exception {
        String created = mvc.perform(post("/api/relics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Rename Target IT","rarity":"Rare","type":"Standard",
                                 "bonusStat":"Coins","bonusValue":0.02,"obtainCondition":"test"}"""))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long id = MAPPER.readTree(created).get("id").asLong();
        // Renaming to an existing relic's name (a seeded one) must be rejected.
        mvc.perform(put("/api/relics/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Outbreak","rarity":"Rare","type":"Standard",
                                 "bonusStat":"Coins","bonusValue":0.02,"obtainCondition":"test"}"""))
                .andExpect(status().isConflict());
        // Re-saving with its own name (unchanged) must still succeed.
        mvc.perform(put("/api/relics/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Rename Target IT","rarity":"Epic","type":"Standard",
                                 "bonusStat":"Coins","bonusValue":0.03,"obtainCondition":"test"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rarity").value("Epic"));
    }

    @Test
    void happinessBalloons_isSpelledCorrectly() throws Exception {
        String all = mvc.perform(get("/api/relics"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        boolean found = false;
        for (JsonNode r : MAPPER.readTree(all)) {
            String name = r.get("name").asText();
            if (name.equals("Hapiness Balloons")) {
                throw new AssertionError("Misspelled 'Hapiness Balloons' still present");
            }
            if (name.equals("Happiness Balloons")) found = true;
        }
        if (!found) throw new AssertionError("'Happiness Balloons' relic not found");
    }
}