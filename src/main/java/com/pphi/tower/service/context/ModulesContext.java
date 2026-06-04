package com.pphi.tower.service.context;

import com.pphi.tower.repository.CurrencySnapshotRepository.Module;
import java.util.List;

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

    /**
     * Returns structured module levels for snapshot persistence.
     * TODO: populate when module inventory data is available in structured form
     *       rather than as a pre-formatted string.
     */
    public List<Module> getModules() {
        return List.of();
    }

    @Override
    public String toString() {
        return getContent();
    }
}
