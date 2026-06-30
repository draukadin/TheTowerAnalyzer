package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VersionHistoryControllerIT extends BaseIntegrationTest {

    @Test
    void getAll_returns200() throws Exception {
        mvc.perform(get("/api/versions"))
                .andExpect(status().isOk());
    }

    @Test
    void create_returns200AndIsPersisted() throws Exception {
        mvc.perform(post("/api/versions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"version\": \"9.9.9\", \"type\": \"minor\", \"changes\": []}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/versions"))
                .andExpect(jsonPath("$[?(@.version=='9.9.9')]").isNotEmpty());
    }

    @Test
    void create_duplicateVersion_returns409() throws Exception {
        String body = "{\"version\": \"9.9.8\", \"type\": \"minor\", \"changes\": []}";
        mvc.perform(post("/api/versions")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mvc.perform(post("/api/versions")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("already exists")));
    }
}
