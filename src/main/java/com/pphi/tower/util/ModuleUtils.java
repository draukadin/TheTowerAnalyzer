package com.pphi.tower.util;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.model.sheets.modules.EquippedModule;
import com.pphi.tower.model.sheets.modules.Module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ModuleUtils {

    private static final Map<String, Module> MODULES = new HashMap<>();
    static {
        MODULES.put("Astral Deliverance", new Module.AstralDeliverance());
        MODULES.put("Havoc Bringer", new Module.HavocBringer());
        MODULES.put("Being Annihilator", new Module.BeingAnnihilator());
        MODULES.put("Death Penalty", new Module.DeathPenalty());
        MODULES.put("Shrink Ray", new Module.ShrinkRay());
        MODULES.put("Amplifying Strike", new Module.AmplifyingStrike());
        MODULES.put("AntiCubePortal", new Module.AntiCubePortal());
        MODULES.put("Negative Mass Projector", new Module.NegativeMassProjector());
        MODULES.put("Space Displacer", new Module.SpaceDisplacer());
        MODULES.put("Wormhole Redirector", new Module.WormholeRedirector());
        MODULES.put("Sharp Fortitude", new Module.SharpFortitude());
        MODULES.put("Orbital Augment", new Module.OrbitalAugment());
        MODULES.put("Black Hole Digestor", new Module.BlackHoleDigestor());
        MODULES.put("Galaxy Compressor", new Module.GalaxyCompressor());
        MODULES.put("Singularity Harness", new Module.SingularityHarness());
        MODULES.put("Pulsar Harvester", new Module.PulsarHarvester());
        MODULES.put("Project Funding", new Module.ProjectFunding());
        MODULES.put("Restorative Bonus", new Module.RestorativeBonus());
        MODULES.put("Multiverse Nexus", new Module.MultiverseNexus());
        MODULES.put("Dimension Core", new Module.DimensionCore());
        MODULES.put("Harmony Conductor", new Module.HarmonyConductor());
        MODULES.put("Om Chip", new Module.OmChip());
        MODULES.put("Magnetic Hook", new Module.MagneticHook());
        MODULES.put("Primordial Collapse", new Module.PrimordialCollapse());
    }

    private ModuleUtils() {}

    public static Module getModuleByName(final String name) {
        if (name.isBlank()) {
            return null;
        }
        return Optional.ofNullable(MODULES.get(name))
                .orElseThrow(() -> new RuntimeException(String.format("No module defined for %s", name)));
    }

    public static Map<String, EquippedModule> getEquippedModules(final List<ValueRange> valueRanges) {
        Map<String, EquippedModule> equippedModuleMap = new HashMap<>();
        addToMap(
                ValueRangeUtils.getValue(valueRanges, 1, 1),
                ValueRangeUtils.getValue(valueRanges, 2, 1),
                equippedModuleMap);
        addToMap(
                ValueRangeUtils.getValue(valueRanges, 7, 1),
                ValueRangeUtils.getValue(valueRanges, 8, 1),
                equippedModuleMap);
        addToMap(
                ValueRangeUtils.getValue(valueRanges, 13, 1),
                ValueRangeUtils.getValue(valueRanges, 14, 1),
                equippedModuleMap);
        addToMap(
                ValueRangeUtils.getValue(valueRanges, 19, 1),
                ValueRangeUtils.getValue(valueRanges, 20, 1),
                equippedModuleMap);
        return equippedModuleMap;
    }

    private static EquippedModule create(String primaryName, String assistName) {
        return new EquippedModule(getModuleByName(primaryName), getModuleByName(assistName));
    }

    private static void addToMap(String primaryName, String assistName, Map<String, EquippedModule> map) {
        EquippedModule equippedModule = create(primaryName, assistName);
        map.put(equippedModule.type(), equippedModule);
    }
}
