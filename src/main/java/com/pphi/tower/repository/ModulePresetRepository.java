package com.pphi.tower.repository;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.model.sheets.GoogleSheet;
import com.pphi.tower.model.sheets.modules.EquippedModule;
import com.pphi.tower.model.sheets.modules.ModulePreset;
import com.pphi.tower.model.sheets.modules.Preset;
import com.pphi.tower.util.ModuleUtils;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Repository
public class ModulePresetRepository {

    private final GoogleSheetsRepository sheetsRepository;

    public ModulePresetRepository(GoogleSheetsRepository sheetsRepository) {
        this.sheetsRepository = sheetsRepository;
    }

    public Map<String, EquippedModule> getActiveModules(final Preset preset) {
        final ModulePreset modulePreset = ModulePreset.getModulePreset(preset);
        final GoogleSheet ref = new GoogleSheet.Ref(preset.sheetId(), modulePreset.sheetName(), modulePreset.range());

        try {
            final List<ValueRange> modulePresetValues = sheetsRepository.readRanges(ref);
            return ModuleUtils.getEquippedModules(modulePresetValues);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load module preset: " + preset, e);
        }
    }
}
