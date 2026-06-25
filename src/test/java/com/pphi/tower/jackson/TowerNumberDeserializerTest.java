package com.pphi.tower.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.fixtures.TowerNumberFactory;
import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TowerNumberDeserializerTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void deserialize_rawAndSuffix_correctAmount() throws Exception {
        String json = """
                {"display":"5T","raw":5000000000000,"suffix":"T"}
                """;
        var n = mapper.readValue(json, TowerNumber.class);

        assertThat(n.scaleSuffix()).isEqualTo(ScaleSuffix.TRILLION);
        assertThat(n.amount()).isEqualByComparingTo("5");
    }

    @Test
    void deserialize_nullSuffix_emptyString() throws Exception {
        String json = """
                {"display":"500","raw":500,"suffix":""}
                """;
        var n = mapper.readValue(json, TowerNumber.class);

        assertThat(n.scaleSuffix()).isNull();
        assertThat(n.amount()).isEqualByComparingTo("500");
    }

    @Test
    void deserialize_zero() throws Exception {
        String json = """
                {"display":"0.00","raw":0,"suffix":""}
                """;
        var n = mapper.readValue(json, TowerNumber.class);

        assertThat(n.amount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(n.scaleSuffix()).isNull();
    }

    @Test
    void deserialize_unknownSuffix_treatedAsNoSuffix() throws Exception {
        String json = """
                {"display":"999","raw":999,"suffix":"X"}
                """;
        var n = mapper.readValue(json, TowerNumber.class);

        assertThat(n.scaleSuffix()).isNull();
        assertThat(n.amount()).isEqualByComparingTo("999");
    }

    @Test
    void roundTrip_preservesValue() throws Exception {
        var original = TowerNumberFactory.of(3.7, ScaleSuffix.BILLION);
        String json = mapper.writeValueAsString(original);
        var restored = mapper.readValue(json, TowerNumber.class);

        assertThat(restored.scaleSuffix()).isEqualTo(ScaleSuffix.BILLION);
        // amount should be numerically equal (3.7)
        assertThat(restored.amount().doubleValue()).isCloseTo(3.7, org.assertj.core.data.Offset.offset(1e-6));
    }

    @Test
    void roundTrip_zero() throws Exception {
        var original = TowerNumberFactory.zero();
        String json = mapper.writeValueAsString(original);
        var restored = mapper.readValue(json, TowerNumber.class);

        assertThat(restored.amount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(restored.scaleSuffix()).isNull();
    }

    @Test
    void deserialize_extraFields_ignored() throws Exception {
        String json = """
                {"display":"2M","raw":2000000,"suffix":"M","unknown":"extra"}
                """;
        var n = mapper.readValue(json, TowerNumber.class);

        assertThat(n.scaleSuffix()).isEqualTo(ScaleSuffix.MILLION);
        assertThat(n.amount()).isEqualByComparingTo("2");
    }
}