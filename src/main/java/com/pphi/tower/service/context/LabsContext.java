package com.pphi.tower.service.context;

public class LabsContext implements ChatContext {

    private final String labsData;

    public LabsContext(String labsData) {
        this.labsData = labsData;
    }

    @Override
    public String getLabel() {
        return "Laboratories";
    }

    @Override
    public String getContent() {
        return labsData;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
