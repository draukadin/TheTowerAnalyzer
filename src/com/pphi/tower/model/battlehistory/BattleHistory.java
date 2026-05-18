package com.pphi.tower.model.battlehistory;

import java.util.Map;

public record BattleHistory(Map<SectionHeader, Section> sectionMap) { }
