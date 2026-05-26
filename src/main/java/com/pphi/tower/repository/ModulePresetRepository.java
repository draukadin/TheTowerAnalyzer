package com.pphi.tower.repository;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.config.AppConfig;
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
    private final AppConfig appConfig;

    public ModulePresetRepository(
            GoogleSheetsRepository sheetsRepository,
            AppConfig appConfig) {
        this.sheetsRepository = sheetsRepository;
        this.appConfig = appConfig;
    }

    public Map<String, EquippedModule> getActiveModules(final Preset preset) {
        final String sheetId = preset.sheetId();
        final ModulePreset modulePreset = ModulePreset.getModulePreset(preset);

        try {
            final List<ValueRange> modulePresetValues = sheetsRepository.readRanges(sheetId, modulePreset.sheetName(),
                    modulePreset.range());
            return ModuleUtils.getEquippedModules(modulePresetValues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
