package com.pphi.tower.service.context;

public class LabTierListContext implements ChatContext {

    private final String tierListData;

    public LabTierListContext(String tierListData) {
        this.tierListData = tierListData;
    }

    @Override
    public String getLabel() {
        return "Lab Tier List";
    }

    @Override
    public String getContent() {
        return tierListData;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
