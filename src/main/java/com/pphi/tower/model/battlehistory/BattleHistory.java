package com.pphi.tower.model.battlehistory;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;

public record BattleHistory(
        @JsonDeserialize(using = SectionMapDeserializer.class)
        Map<SectionHeader, Section> sectionMap) { }
