package com.pphi.tower.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.model.battlehistory.Section;
import com.pphi.tower.model.battlehistory.SectionHeader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BattleHistoryDeserializer extends JsonDeserializer<BattleHistory> {

    @Override
    public BattleHistory deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);
        JsonNode sectionMapNode = root.get("sectionMap");

        Map<SectionHeader, Section> sectionMap = new HashMap<>();
        if (sectionMapNode != null) {
            sectionMapNode.fields().forEachRemaining(entry -> {
                try {
                    SectionHeader header = SectionHeader.valueOf(entry.getKey());
                    Section section = (Section) mapper.treeToValue(entry.getValue(), header.getType());
                    sectionMap.put(header, section);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize section: " + entry.getKey(), e);
                }
            });
        }
        return new BattleHistory(sectionMap);
    }
}
