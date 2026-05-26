package com.pphi.tower.model.sheets.modules;

import com.pphi.tower.model.sheets.GoogleSheet;

import java.util.List;

public interface Module extends GoogleSheet {
    String type();
    @Override default String sheetName() { return "Inventory"; };
    @Override List<String> ranges();
    default String name() { return this.getClass().getSimpleName(); };
    default String sheetId() { return "modules"; }

    class AstralDeliverance implements Module {
        @Override public String type() { return "Cannon"; }
        @Override public List<String> ranges() { return List.of("F3:H14"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class BeingAnnihilator implements Module {
        @Override public String type() { return "Cannon"; }
        @Override public List<String> ranges() { return List.of("K3:M14"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class DeathPenalty implements Module {
        @Override public String type() { return "Cannon"; }
        @Override public List<String> ranges() { return List.of("P3:R14"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class HavocBringer implements Module {
        @Override public String type() { return "Cannon"; }
        @Override public List<String> ranges() { return List.of("U3:W14"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class ShrinkRay implements Module {
        @Override public String type() { return "Cannon"; }
        @Override public List<String> ranges() { return List.of("Z3:AB14"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class AmplifyingStrike implements Module {
        @Override public String type() { return "Cannon"; }
        @Override public List<String> ranges() { return List.of("AE3:AG14"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class AntiCubePortal implements Module {
        @Override public String type() { return "Armor"; }
        @Override public List<String> ranges() { return List.of("F16:H27"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class NegativeMassProjector implements Module {
        @Override public String type() { return "Armor"; }
        @Override public List<String> ranges() { return List.of("K16:M27"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class SpaceDisplacer implements Module {
        @Override public String type() { return "Armor"; }
        @Override public List<String> ranges() { return List.of("P16:R27"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class WormholeRedirector implements Module {
        @Override public String type() { return "Armor"; }
        @Override public List<String> ranges() { return List.of("U16:W27"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class SharpFortitude implements Module {
        @Override public String type() { return "Armor"; }
        @Override public List<String> ranges() { return List.of("Z16:AB27"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class OrbitalAugment implements Module {
        @Override public String type() { return "Armor"; }
        @Override public List<String> ranges() { return List.of("AE16:AG27"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class BlackHoleDigestor implements Module {
        @Override public String type() { return "Generator"; }
        @Override public List<String> ranges() { return List.of("F29:H40"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class GalaxyCompressor implements Module {
        @Override public String type() { return "Generator"; }
        @Override public List<String> ranges() { return List.of("K29:M40"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class SingularityHarness implements Module {
        @Override public String type() { return "Generator"; }
        @Override public List<String> ranges() { return List.of("P29:R40"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class PulsarHarvester implements Module {
        @Override public String type() { return "Generator"; }
        @Override public List<String> ranges() { return List.of("U29:W40"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class ProjectFunding implements Module {
        @Override public String type() { return "Generator"; }
        @Override public List<String> ranges() { return List.of("Z29:AB40"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class RestorativeBonus implements Module {
        @Override public String type() { return "Generator"; }
        @Override public List<String> ranges() { return List.of("AE29:AG40"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class MultiverseNexus implements Module {
        @Override public String type() { return "Core"; }
        @Override public List<String> ranges() { return List.of("F42:H53"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class DimensionCore implements Module {
        @Override public String type() { return "Core"; }
        @Override public List<String> ranges() { return List.of("K42:M53"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class HarmonyConductor implements Module {
        @Override public String type() { return "Core"; }
        @Override public List<String> ranges() { return List.of("P42:R53"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class OmChip implements Module {
        @Override public String type() { return "Core"; }
        @Override public List<String> ranges() { return List.of("U42:W53"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class MagneticHook implements Module {
        @Override public String type() { return "Core"; }
        @Override public List<String> ranges() { return List.of("Z42:AB53"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
    class PrimordialCollapse implements Module {
        @Override public String type() { return "Core"; }
        @Override public List<String> ranges() { return List.of("AE42:AG53"); }
        @Override public String toString() { return String.format("%s - %s", this.name(), this.type()); }
    }
}
