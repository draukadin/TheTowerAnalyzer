package com.pphi.tower.service.context;

public class ModulesContext implements ChatContext {

    private final String inventoryData;
    private final String subStats;

    public ModulesContext(String inventoryData, String subStats) {
        this.inventoryData = inventoryData;
        this.subStats = subStats;
    }

    @Override
    public String getLabel() {
        return "Module Inventory";
    }

    @Override
    public String getContent() {
        return "## Module Inventory\n\n" + inventoryData +
               "## Module Sub-Stats\n\n" + subStats;
    }
}
