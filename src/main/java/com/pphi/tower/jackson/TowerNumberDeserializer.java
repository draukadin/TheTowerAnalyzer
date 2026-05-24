package com.pphi.tower.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerNumber;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class TowerNumberDeserializer extends StdDeserializer<TowerNumber> {

    public TowerNumberDeserializer() {
        super(TowerNumber.class);
    }

    @Override
    public TowerNumber deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        BigDecimal raw = null;
        String suffix = "";

        p.nextToken(); // START_OBJECT -> first FIELD_NAME
        while (p.currentToken() == JsonToken.FIELD_NAME) {
            String field = p.currentName();
            p.nextToken(); // FIELD_NAME -> value
            switch (field) {
                case "raw"    -> raw = p.getDecimalValue();
                case "suffix" -> suffix = p.getText();
                default       -> p.skipChildren();
            }
            p.nextToken(); // value -> next FIELD_NAME or END_OBJECT
        }

        ScaleSuffix scaleSuffix = ScaleSuffix.fromSuffix(suffix);
        BigDecimal amount;
        if (scaleSuffix != null && raw != null) {
            amount = raw.divide(scaleSuffix.getScientificNotation(), 10, RoundingMode.HALF_UP)
                        .stripTrailingZeros();
        } else {
            amount = raw != null ? raw : BigDecimal.ZERO;
        }
        return new TowerNumber(amount, scaleSuffix);
    }
}
