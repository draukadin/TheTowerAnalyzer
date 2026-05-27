package com.pphi.tower.service.context;

public class VersionHistoryContext implements ChatContext {

    private final String versionHistoryData;

    public VersionHistoryContext(String versionHistoryData) {
        this.versionHistoryData = versionHistoryData;
    }

    @Override
    public String getLabel() {
        return "Version History";
    }

    @Override
    public String getContent() {
        return versionHistoryData;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
