package com.pphi.tower.web.dto;

public record ConversationTurn(String role, String text, String thoughtSignature) {
    public ConversationTurn(String role, String text) {
        this(role, text, null);
    }
}
