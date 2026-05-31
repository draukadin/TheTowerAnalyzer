package com.pphi.tower.service.context;

import com.google.common.base.MoreObjects;

public class LabPlanningContext implements ChatContext {

    private final String labPlanningData;

    public LabPlanningContext(String labPlanningData) {
        this.labPlanningData = labPlanningData;
    }

    @Override
    public String getLabel() {
        return "Lab Slot Planning";
    }

    @Override
    public String getContent() {
        return labPlanningData;
    }

    @Override
    public String toString() {
        return getContent();
    }
}
