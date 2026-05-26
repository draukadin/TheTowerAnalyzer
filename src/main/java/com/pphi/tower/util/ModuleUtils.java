package com.pphi.tower.util;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.model.sheets.modules.EquippedModule;
import com.pphi.tower.model.sheets.modules.Module;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ModuleUtils {

    private static final Map<String, Module> MODULES;
    static {
        Map<String, Module> m = new HashMap<>();
        m.put("Astral Deliverance",       new Module.AstralDeliverance());
        m.put("Havoc Bringer",            new Module.HavocBringer());
        m.put("Being Annihilator",        new Module.BeingAnnihilator());
        m.put("Death Penalty",            new Module.DeathPenalty());
        m.put("Shrink Ray",               new Module.ShrinkRay());
        m.put("Amplifying Strike",        new Module.AmplifyingStrike());
        m.put("AntiCubePortal",           new Module.AntiCubePortal());
        m.put("Negative Mass Projector",  new Module.NegativeMassProjector());
        m.put("Space Displacer",          new Module.SpaceDisplacer());
        m.put("Wormhole Redirector",      new Module.WormholeRedirector());
        m.put("Sharp Fortitude",          new Module.SharpFortitude());
        m.put("Orbital Augment",          new Module.OrbitalAugment());
        m.put("Black Hole Digestor",      new Module.BlackHoleDigestor());
        m.put("Galaxy Compressor",        new Module.GalaxyCompressor());
        m.put("Singularity Harness",      new Module.SingularityHarness());
        m.put("Pulsar Harvester",         new Module.PulsarHarvester());
        m.put("Project Funding",          new Module.ProjectFunding());
        m.put("Restorative Bonus",        new Module.RestorativeBonus());
        m.put("Multiverse Nexus",         new Module.MultiverseNexus());
        m.put("Dimension Core",           new Module.DimensionCore());
        m.put("Harmony Conductor",        new Module.HarmonyConductor());
        m.put("Om Chip",                  new Module.OmChip());
        m.put("Magnetic Hook",            new Module.MagneticHook());
        m.put("Primordial Collapse",      new Module.PrimordialCollapse());
        MODULES = Map.copyOf(m);
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
        for (int slot = 0; slot < ModuleSlotLayout.SLOT_COUNT; slot++) {
            int primaryRow = ModuleSlotLayout.FIRST_SLOT_ROW
                    + (slot * ModuleSlotLayout.SLOT_STRIDE)
                    + ModuleSlotLayout.PRIMARY_ROW_OFFSET;
            int assistRow = ModuleSlotLayout.FIRST_SLOT_ROW
                    + (slot * ModuleSlotLayout.SLOT_STRIDE)
                    + ModuleSlotLayout.ASSIST_ROW_OFFSET;
            addToMap(
                    ValueRangeUtils.getValue(valueRanges, primaryRow, ModuleSlotLayout.NAME_COL),
                    ValueRangeUtils.getValue(valueRanges, assistRow,  ModuleSlotLayout.NAME_COL),
                    equippedModuleMap);
        }
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
