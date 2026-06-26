package com.pphi.tower.web;

import com.pphi.tower.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TournamentControllerIT extends BaseIntegrationTest {

    // CSV using condition names that are guaranteed to be seeded by BattleConditionSeeder
    private static final String VALID_CSV =
            ",Legend,Champion,Platinum,Gold,Silver,Copper\n" +
            "0,Orb Resistance,More Bosses,Orb Resistance,Orb Resistance,Orb Resistance,Orb Resistance\n" +
            "1,More Enemies,None,,None,None,None\n";

    // ── Existing endpoints ───────────────────────────────────────────────────────

    @Test
    void getAll_returns200() throws Exception {
        mvc.perform(get("/api/tournaments"))
                .andExpect(status().isOk());
    }

    @Test
    void getConditions_returns200() throws Exception {
        mvc.perform(get("/api/tournaments/conditions"))
                .andExpect(status().isOk());
    }

    @Test
    void getConditions_returnsNonEmptyList() throws Exception {
        mvc.perform(get("/api/tournaments/conditions"))
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    @Test
    void createAndRetrieve() throws Exception {
        mvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\": \"2026-06-25\", \"league\": \"GOLD\", \"conditionIds\": []}"))
                .andExpect(status().isOk());

        mvc.perform(get("/api/tournaments"))
                .andExpect(jsonPath("$.length()").value(greaterThan(0)));
    }

    // ── POST /api/tournaments/import/csv ─────────────────────────────────────────

    @Test
    void importCsv_validCsvAndWednesday_returns200WithSummary() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "export.csv", "text/csv", VALID_CSV.getBytes());

        mvc.perform(multipart("/api/tournaments/import/csv")
                        .file(file)
                        .param("date", "2026-06-24"))   // Wednesday
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-06-24"))
                .andExpect(jsonPath("$.conditionsPerLeague").isMap());
    }

    @Test
    void importCsv_validCsvAndSaturday_returns200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "export.csv", "text/csv", VALID_CSV.getBytes());

        mvc.perform(multipart("/api/tournaments/import/csv")
                        .file(file)
                        .param("date", "2026-06-27"))   // Saturday
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2026-06-27"));
    }

    @Test
    void importCsv_mondayDate_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "export.csv", "text/csv", VALID_CSV.getBytes());

        mvc.perform(multipart("/api/tournaments/import/csv")
                        .file(file)
                        .param("date", "2026-06-22"))   // Monday
                .andExpect(status().isBadRequest());
    }

    @Test
    void importCsv_malformedDate_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "export.csv", "text/csv", VALID_CSV.getBytes());

        mvc.perform(multipart("/api/tournaments/import/csv")
                        .file(file)
                        .param("date", "not-a-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void importCsv_blankDate_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "export.csv", "text/csv", VALID_CSV.getBytes());

        mvc.perform(multipart("/api/tournaments/import/csv")
                        .file(file)
                        .param("date", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void importCsv_emptyCsvFile_returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "export.csv", "text/csv", new byte[0]);

        mvc.perform(multipart("/api/tournaments/import/csv")
                        .file(file)
                        .param("date", "2026-06-24"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void importCsv_csvWithNoKnownTiers_returns400() throws Exception {
        String badCsv = ",Foo,Bar\n0,X,Y\n";
        MockMultipartFile file = new MockMultipartFile(
                "file", "export.csv", "text/csv", badCsv.getBytes());

        mvc.perform(multipart("/api/tournaments/import/csv")
                        .file(file)
                        .param("date", "2026-06-24"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void importCsv_idempotent_secondCallReturns200() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "export.csv", "text/csv", VALID_CSV.getBytes());

        mvc.perform(multipart("/api/tournaments/import/csv")
                        .file(file)
                        .param("date", "2026-06-24"))
                .andExpect(status().isOk());

        MockMultipartFile file2 = new MockMultipartFile(
                "file", "export.csv", "text/csv", VALID_CSV.getBytes());

        mvc.perform(multipart("/api/tournaments/import/csv")
                        .file(file2)
                        .param("date", "2026-06-24"))
                .andExpect(status().isOk());
    }

    // ── POST /api/tournaments/sync ───────────────────────────────────────────────

    @Test
    void sync_noS3Configured_returns200WithEmptyArray() throws Exception {
        mvc.perform(post("/api/tournaments/sync"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    // ── GET /api/tournaments/fetch-from-s3 ──────────────────────────────────────

    @Test
    void fetchFromS3_noS3Configured_returns404() throws Exception {
        mvc.perform(get("/api/tournaments/fetch-from-s3").param("date", "2026-06-24"))
                .andExpect(status().isNotFound());
    }

    @Test
    void fetchFromS3_mondayDate_returns400() throws Exception {
        mvc.perform(get("/api/tournaments/fetch-from-s3").param("date", "2026-06-22"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fetchFromS3_malformedDate_returns400() throws Exception {
        mvc.perform(get("/api/tournaments/fetch-from-s3").param("date", "bad-date"))
                .andExpect(status().isBadRequest());
    }

    // ── POST /api/tournaments + DELETE ──────────────────────────────────────────

    @Test
    void delete_existingEntry_removesIt() throws Exception {
        String createResponse = mvc.perform(post("/api/tournaments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\": \"2026-07-01\", \"league\": \"SILVER\", \"conditionIds\": []}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        long id = mapper.readTree(createResponse).get("id").asLong();

        mvc.perform(delete("/api/tournaments/" + id))
                .andExpect(status().isOk());
    }

    // ── POST /api/tournaments/search ─────────────────────────────────────────────

    @Test
    void findByConditions_emptyList_returns200() throws Exception {
        mvc.perform(post("/api/tournaments/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conditionIds\": []}"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }
}
