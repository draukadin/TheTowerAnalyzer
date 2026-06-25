package com.pphi.tower.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.fixtures.TowerNumberFactory;
import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TowerNumberSerializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void serialize_withSuffix_hasDisplayRawSuffix() throws Exception {
        var n = TowerNumberFactory.of(5.0, ScaleSuffix.TRILLION);
        var node = mapper.readTree(mapper.writeValueAsString(n));

        assertThat(node.get("display").asText()).isEqualTo("5.0T");
        assertThat(node.get("raw").asDouble()).isEqualTo(5_000_000_000_000.0);
        assertThat(node.get("suffix").asText()).isEqualTo("T");
    }

    @Test
    void serialize_nullSuffix_emptyString() throws Exception {
        var n = new TowerNumber(BigDecimal.valueOf(500), null);
        var node = mapper.readTree(mapper.writeValueAsString(n));

        assertThat(node.get("display").asText()).isEqualTo("500");
        assertThat(node.get("raw").asDouble()).isEqualTo(500.0);
        assertThat(node.get("suffix").asText()).isEmpty();
    }

    @Test
    void serialize_zero_noSuffix() throws Exception {
        var n = TowerNumberFactory.zero();
        var node = mapper.readTree(mapper.writeValueAsString(n));

        assertThat(node.get("display").asText()).isEqualTo("0.00");
        assertThat(node.get("raw").asDouble()).isEqualTo(0.0);
        assertThat(node.get("suffix").asText()).isEmpty();
    }

    @Test
    void serialize_allThreeFieldsPresent() throws Exception {
        var n = TowerNumberFactory.of(1.5, ScaleSuffix.MILLION);
        var node = mapper.readTree(mapper.writeValueAsString(n));

        assertThat(node.fieldNames()).toIterable()
                .contains("display", "raw", "suffix");
    }

    @Test
    void serialize_billion_rawCorrect() throws Exception {
        var n = TowerNumberFactory.of(2.5, ScaleSuffix.BILLION);
        var node = mapper.readTree(mapper.writeValueAsString(n));

        assertThat(node.get("raw").asDouble()).isEqualTo(2_500_000_000.0);
        assertThat(node.get("suffix").asText()).isEqualTo("B");
    }
}