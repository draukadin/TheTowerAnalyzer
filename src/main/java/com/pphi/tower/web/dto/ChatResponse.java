package com.pphi.tower.web.dto;

import java.util.List;

public record ChatResponse(String reply, List<ConversationTurn> prependedTurns, String thoughtSignature) {}
