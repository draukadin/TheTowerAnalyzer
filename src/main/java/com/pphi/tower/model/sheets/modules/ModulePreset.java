package com.pphi.tower.model.sheets.modules;

import java.util.List;

public interface ModulePreset {

    Preset preset();
    default String sheetName() { return "Presets"; };
    List<String> range();

    static ModulePreset getModulePreset(final Preset preset) {
        return switch (preset) {
            case TOURNAMENT -> new Tournament();
            case FARMING -> new Farming();
            default -> throw new RuntimeException(String.format("No preset defined for %s", preset));
        };
    }

    class Farming implements ModulePreset {
        @Override public Preset preset() { return Preset.FARMING; }

        @Override
        public List<String> range() {
            return List.of("I3:J22");
        }
    }

    class Tournament implements ModulePreset {
        @Override public Preset preset() { return Preset.TOURNAMENT; }

        @Override public List<String> range() {
            return List.of("L3:M23");
        }
    }
}
