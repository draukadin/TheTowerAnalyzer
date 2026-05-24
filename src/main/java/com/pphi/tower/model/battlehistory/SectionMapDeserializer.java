package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SectionMapDeserializer extends StdDeserializer<Map<SectionHeader, Section>> {

    public SectionMapDeserializer() {
        super(Map.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<SectionHeader, Section> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        Map<SectionHeader, Section> result = new LinkedHashMap<>();
        p.nextToken(); // START_OBJECT -> first FIELD_NAME
        while (p.currentToken() == JsonToken.FIELD_NAME) {
            SectionHeader header = SectionHeader.valueOf(p.currentName());
            p.nextToken(); // FIELD_NAME -> value START_OBJECT
            result.put(header, (Section) ctxt.readValue(p, header.getType()));
            p.nextToken(); // value END_OBJECT -> next FIELD_NAME or END_OBJECT
        }
        return result;
    }
}
