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
        slots.forEach((slotType, em) -> {
            sb.append(slotType).append(" Module Slot:\n");
            appendSlot(sb, "Primary", em.primarySlot());
            appendSlot(sb, "Assist",  em.assistSlot());
        });
        return sb.toString();
    }

    private void appendSlot(StringBuilder sb, String role, Module module) {
        if (module == null) {
            sb.append(String.format("  %s: (empty)%n", role));
        } else {
            sb.append(String.format("  %s: %s [TYPE: %s]%n", role, module.name(), module.type()));
        }
    }
}
