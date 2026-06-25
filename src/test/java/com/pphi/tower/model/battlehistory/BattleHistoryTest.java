package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pphi.tower.model.TowerNumber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BattleHistoryTest {

    private static BattleHistory history;

    @BeforeAll
    static void loadFixture() throws IOException {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        history = mapper.readValue(new File("src/test/resources/payload.json"), BattleHistory.class);
    }

    @Test
    void testDeserializeBattleHistory() {
        Assertions.assertNotNull(history);
        Assertions.assertNotNull(history.sectionMap().get(SectionHeader.ENEMIES_HIT_BY));
    }

    @Test
    void allSixteenSectionsPresent() {
        assertThat(history.sectionMap()).containsKeys(SectionHeader.values());
    }

    @Test
    void battleReport_tier_wave_killedBy() {
        var report = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(report.tier()).isEqualTo(10);
        assertThat(report.wave()).isEqualTo(6784);
        assertThat(report.killedBy()).isEqualTo("Fast");
    }

    @Test
    void battleReport_towerNumbers_notNull() {
        var report = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(report.coinsEarned()).isNotNull();
        assertThat(report.cellsEarned()).isNotNull();
        assertThat(report.coinsPerHour()).isNotNull();
        assertThat(report.cellsPerHour()).isNotNull();
    }

    @Test
    void towerNumberFields_roundTripRawValue() throws Exception {
        // Serialize back to JSON and re-parse — raw field must survive
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String json = mapper.writeValueAsString(history);
        BattleHistory restored = mapper.readValue(json, BattleHistory.class);

        var origReport = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        var restReport = (BattleReport) restored.sectionMap().get(SectionHeader.BATTLE_REPORT);

        TowerNumber origCoins = origReport.coinsEarned();
        TowerNumber restCoins = restReport.coinsEarned();
        assertThat(restCoins.scaleSuffix()).isEqualTo(origCoins.scaleSuffix());
        assertThat(restCoins.amount().doubleValue())
                .isCloseTo(origCoins.amount().doubleValue(), org.assertj.core.data.Offset.offset(1e-3));
    }
}