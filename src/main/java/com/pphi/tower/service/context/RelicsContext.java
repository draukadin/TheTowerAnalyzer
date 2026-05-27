package com.pphi.tower.service.context;

public class RelicsContext implements ChatContext {

    private final String relicsData;

    public RelicsContext(String relicsData) {
        this.relicsData = relicsData;
    }

    @Override
    public String getLabel() {
        return "Relics";
    }

    @Override
    public String getContent() {
        return relicsData;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
