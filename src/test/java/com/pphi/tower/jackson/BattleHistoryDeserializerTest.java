package com.pphi.tower.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.model.battlehistory.BattleReport;
import com.pphi.tower.model.battlehistory.SectionHeader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class BattleHistoryDeserializerTest {

    private static ObjectMapper mapper;

    @BeforeAll
    static void buildMapper() {
        mapper = new ObjectMapper();
        SimpleModule m = new SimpleModule();
        m.addDeserializer(BattleHistory.class, new BattleHistoryDeserializer());
        mapper.registerModule(m);
        mapper.findAndRegisterModules();
    }

    private BattleHistory load() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/payload.json")) {
            return mapper.readValue(is, BattleHistory.class);
        }
    }

    @Test
    void deserialize_allSixteenSectionsPresent() throws Exception {
        BattleHistory history = load();
        assertThat(history.sectionMap()).containsKeys(SectionHeader.values());
    }

    @Test
    void deserialize_battleReport_tier() throws Exception {
        BattleHistory history = load();
        BattleReport report = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(report.tier()).isEqualTo(10);
    }

    @Test
    void deserialize_battleReport_wave() throws Exception {
        BattleHistory history = load();
        BattleReport report = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(report.wave()).isEqualTo(6784);
    }

    @Test
    void deserialize_unknownSection_isIgnored() throws Exception {
        String json = "{\"sectionMap\":{}}";
        BattleHistory history = mapper.readValue(json, BattleHistory.class);
        assertThat(history.sectionMap()).isEmpty();
    }

    @Test
    void deserialize_nullSectionMap_producesEmptyMap() throws Exception {
        String json = "{}";
        BattleHistory history = mapper.readValue(json, BattleHistory.class);
        assertThat(history.sectionMap()).isEmpty();
    }

    @Test
    void deserialize_invalidSectionKey_throwsException() {
        String json = "{\"sectionMap\":{\"NOT_A_REAL_SECTION\":{}}}";
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> mapper.readValue(json, BattleHistory.class))
                .isInstanceOf(Exception.class);
    }
}