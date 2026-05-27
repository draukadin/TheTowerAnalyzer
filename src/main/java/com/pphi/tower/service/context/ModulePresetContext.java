package com.pphi.tower.service.context;

import com.pphi.tower.model.sheets.modules.EquippedModule;
import com.pphi.tower.model.sheets.modules.Module;
import com.pphi.tower.model.sheets.modules.Preset;

import java.util.Map;

public class ModulePresetContext implements ChatContext {

    private final Preset preset;
    private final Map<String, EquippedModule> slots;

    public ModulePresetContext(Preset preset, Map<String, EquippedModule> slots) {
        this.preset = preset;
        this.slots  = slots;
    }

    @Override
    public String getLabel() {
        return switch (preset) {
            case FARMING    -> "Farming Module Preset";
            case TOURNAMENT -> "Tournament Module Preset";
        };
    }

    @Override
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Slot Type | Role | Module Name | Module Type |\n");
        sb.append("| :--- | :--- | :--- | :--- |\n");
        slots.forEach((slotType, em) -> {
            moduleRow(sb, slotType, "Primary", em.primarySlot());
            moduleRow(sb, slotType, "Assist",  em.assistSlot());
        });
        return sb.toString();
    }

    private void moduleRow(StringBuilder sb, String slotType, String role, Module module) {
        if (module == null) {
            sb.append(String.format("| %s | %s | (empty) | - |%n", slotType, role));
        } else {
            sb.append(String.format("| %s | %s | %s | %s |%n",
                    slotType, role, module.name(), module.type()));
        }
    }

    @Override
    public String toString() {
        return getContent();
    }
}
