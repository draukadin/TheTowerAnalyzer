package com.pphi.tower.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.pphi.tower.model.TowerNumber;

import java.io.IOException;
import java.math.BigDecimal;

public class TowerNumberSerializer extends JsonSerializer<TowerNumber> {

    @Override
    public void serialize(TowerNumber value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("display", value.toString());
        gen.writeNumberField("raw", toRaw(value));
        gen.writeStringField("suffix", value.scaleSuffix() != null
                ? value.scaleSuffix().getSuffix()
                : "");
        gen.writeEndObject();
    }

    private double toRaw(TowerNumber tn) {
        if (tn == null) return 0.0;
        BigDecimal base = tn.amount() != null ? tn.amount() : BigDecimal.ZERO;
        if (tn.scaleSuffix() == null) return base.doubleValue();
        return base.multiply(tn.scaleSuffix().getScientificNotation()).doubleValue();
    }
}
