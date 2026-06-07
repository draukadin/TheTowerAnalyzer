package com.pphi.tower.service;

import com.pphi.tower.repository.LabRepository;
import com.pphi.tower.repository.LabRepository.LabData;
import com.pphi.tower.repository.LabRepository.LabMultipliers;
import com.pphi.tower.repository.ModuleRepository;
import com.pphi.tower.repository.RelicRepository;
import com.pphi.tower.repository.UwRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TowerTrackerFetcherService {

    private final UwRepository uwRepository;
    private final ModuleRepository moduleRepository;
    private final RelicRepository relicRepository;
    private final LabRepository labRepository;

    public TowerTrackerFetcherService(
            UwRepository uwRepository,
            ModuleRepository moduleRepository,
            RelicRepository relicRepository,
            LabRepository labRepository) {
        this.uwRepository = uwRepository;
        this.moduleRepository = moduleRepository;
        this.relicRepository = relicRepository;
        this.labRepository = labRepository;
    }

    public List<LabData> fetchLabState() {
        return labRepository.getAll();
    }

    public LabMultipliers fetchLabMultipliers() {
        return labRepository.getMultipliers();
    }

    public List<UwRepository.UwPlayerData> fetchUltimateWeapons() {
        return uwRepository.getAllUwState();
    }

    public List<ModuleRepository.ModulePlayerData> fetchModules() {
        return moduleRepository.getAll();
    }

    public List<RelicRepository.RelicData> fetchRelics() {
        return relicRepository.getAll();
    }
}
