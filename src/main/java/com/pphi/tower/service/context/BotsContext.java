package com.pphi.tower.service.context;

public class BotsContext implements ChatContext {

    private final String botsData;

    public BotsContext(String botsData) {
        this.botsData = botsData;
    }

    @Override
    public String getLabel() {
        return "Bots";
    }

    @Override
    public String getContent() {
        return botsData;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
