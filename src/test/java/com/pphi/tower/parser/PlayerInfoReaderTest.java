package com.pphi.tower.parser;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayerInfoReaderTest {

    private static final Path DAT_FILE =
            Paths.get("resources/playerInfo.dat");

    @Test
    void readsKeyPlayerFields() throws IOException {
        PlayerInfoReader reader = new PlayerInfoReader(false);
        Map<String, Object> data = reader.read(DAT_FILE);

        assertFalse(data.isEmpty(), "parsed map should not be empty");

        // Spot-check a few fields expected to be present with reasonable values
        assertTrue(data.containsKey("cells"), "should contain 'cells'");
        assertTrue(data.containsKey("coins"), "should contain 'coins'");
        assertTrue(data.containsKey("gems"),  "should contain 'gems'");
        assertTrue(data.containsKey("currentTier"), "should contain 'currentTier'");

        // Print all extracted values for visual inspection
        System.out.println("=== Player Data (" + data.size() + " fields) ===");
        String[] keysOfInterest = {
            "cells", "coins", "gems", "keys", "tokens", "stones", "bits", "medals",
            "currentTier", "highestWaveThisTier", "roundsStartedThisTier",
            "upgradeTierUnlocked", "upgradeDefenseTierUnlocked", "upgradeUtilityTierUnlocked",
            "upgradeAttackTierEverUnlocked", "upgradeDefenseTierEverUnlocked", "upgradeUtilityTierEverUnlocked",
            "playTime", "saveRevision", "versionNumber", "dataVersion",
            "gameSpeedMemory", "pauseBattleInsideMenus"
        };
        for (String key : keysOfInterest) {
            Object val = data.get(key);
            System.out.printf("  %-45s = %s%n", key, formatValue(val));
        }
    }

    private String formatValue(Object val) {
        if (val == null)          return "<null>";
        if (val instanceof Long l)  return l + " (0x" + Long.toHexString(l) + ")";
        if (val instanceof Short sh) return sh + " (0x" + Integer.toHexString(sh & 0xFFFF) + ")";
        if (val instanceof boolean[] a) return "boolean[" + a.length + "]";
        if (val instanceof long[]   a)  return "long["    + a.length + "]";
        if (val instanceof short[]  a)  return "short["   + a.length + "]";
        if (val instanceof int[]    a)  return "int["     + a.length + "]";
        return val.toString();
    }
}
