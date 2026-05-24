package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BattleHistoryTest {

    @Test
    void testDeserializeBattleHistory() throws IOException {
        File file = new File("src/test/resources/payload.json");
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
        BattleHistory battleHistory = objectMapper.readValue(file, BattleHistory.class);
        Assertions.assertNotNull(battleHistory);
        Assertions.assertNotNull(battleHistory.sectionMap().get(SectionHeader.ENEMIES_HIT_BY));
    }
}