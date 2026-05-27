package com.pphi.tower.web.dto;

import java.util.List;

public record ChatRequest(
        String prompt,
        List<String> contextTypes,
        String reportId1,
        String reportId2,
        List<ConversationTurn> history,
        String promptKey) {}
