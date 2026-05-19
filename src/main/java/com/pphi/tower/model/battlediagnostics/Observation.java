package com.pphi.tower.model.battlediagnostics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Observation(
        @JsonProperty("label")  String label,
        @JsonProperty("detail") String detail) {

    @JsonCreator
    public Observation { }
}
