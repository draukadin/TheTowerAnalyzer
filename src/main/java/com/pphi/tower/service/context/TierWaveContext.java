package com.pphi.tower.service.context;

public class TierWaveContext implements ChatContext {

    private final String tierWaveData;

    public TierWaveContext(String tierWaveData) {
        this.tierWaveData = tierWaveData;
    }

    @Override
    public String getLabel() {
        return "Tier / Wave";
    }

    @Override
    public String getContent() {
        return tierWaveData;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
