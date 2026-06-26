package com.pphi.tower.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ModuleLevelTableTest {

    // ── shardsForLevel boundary coverage ────────────────────────────────────

    @ParameterizedTest
    @CsvSource({
            "1,   0",
            "2,   7",
            "5,   7",
            "6,  12",
            "10, 12",
            "11, 20",
            "15, 20",
            "16, 25",
            "20, 25",
            "21, 40",
            "25, 40",
            "26, 50",
            "30, 50",
            "31, 75",
            "35, 75",
            "36, 90",
            "40, 90",
            "41,120",
            "50,120",
            "51,180",
            "60,180",
            "61,250",
            "70,250",
            "71,350",
            "80,350",
            "81,500",
            "90,500",
            "91,700",
            "100,700",
            "101,1000",
            "110,1000",
            "111,1300",
            "120,1300",
            "121,1800",
            "130,1800",
            "131,2500",
            "140,2500",
            "141,3000",
            "150,3000",
            "151,4000",
            "160,4000",
            "161,5000",
            "162,5125",
            "201,10000",
            "202,10250",
            "203,10500",
            "300,34750"
    })
    void shardsForLevel_knownTiers(int level, long expected) {
        assertThat(ModuleLevelTable.shardsForLevel(level)).isEqualTo(expected);
    }

    @Test
    void shardsForLevel_levelZero_returnsZero() {
        assertThat(ModuleLevelTable.shardsForLevel(0)).isZero();
    }

    // ── coinsForLevel boundary coverage ─────────────────────────────────────

    @ParameterizedTest
    @CsvSource({
            "1,          0",
            "2,      10000",
            "5,      10000",
            "6,      25000",
            "10,     25000",
            "11,     45000",
            "15,     45000",
            "16,     60000",
            "20,     60000",
            "21,    120000",
            "25,    120000",
            "26,    180000",
            "30,    180000",
            "31,    350000",
            "35,    350000",
            "36,    500000",
            "40,    500000",
            "41,   1000000",
            "50,   1000000",
            "51,   3000000",
            "60,   3000000",
            "61,  25000000",
            "70,  25000000",
            "71, 100000000",
            "80, 100000000",
            "81, 350000000",
            "100,350000000",
            "101,8000000000",
            "120,8000000000",
            "121,32000000000",
            "140,32000000000",
            "141,500000000000",
            "160,500000000000",
            "161,10000000000000",
            "162,60000000000000",
            "163,110000000000000"
    })
    void coinsForLevel_knownTiers(int level, long expected) {
        assertThat(ModuleLevelTable.coinsForLevel(level)).isEqualTo(expected);
    }

    // ── cumulativeShardsTo ───────────────────────────────────────────────────

    @Test
    void cumulativeShardsTo_level1_isZero() {
        assertThat(ModuleLevelTable.cumulativeShardsTo(1)).isZero();
    }

    @Test
    void cumulativeShardsTo_level2_equalsLevel2Cost() {
        assertThat(ModuleLevelTable.cumulativeShardsTo(2)).isEqualTo(7);
    }

    @Test
    void cumulativeShardsTo_level5_isSumOf2to5() {
        long expected = 7 * 4;
        assertThat(ModuleLevelTable.cumulativeShardsTo(5)).isEqualTo(expected);
    }

    // ── shardsRemainingTo ────────────────────────────────────────────────────

    @Test
    void shardsRemainingTo_alreadyAtTarget_returnsZero() {
        assertThat(ModuleLevelTable.shardsRemainingTo(10, 10)).isZero();
    }

    @Test
    void shardsRemainingTo_pastTarget_returnsZero() {
        assertThat(ModuleLevelTable.shardsRemainingTo(20, 10)).isZero();
    }

    @Test
    void shardsRemainingTo_oneStep_equalsNextLevelCost() {
        long cost = ModuleLevelTable.shardsForLevel(6);
        assertThat(ModuleLevelTable.shardsRemainingTo(5, 6)).isEqualTo(cost);
    }

    @Test
    void shardsRemainingTo_multiStep_equalsDifferencOfCumulatives() {
        long expected = ModuleLevelTable.cumulativeShardsTo(20) - ModuleLevelTable.cumulativeShardsTo(10);
        assertThat(ModuleLevelTable.shardsRemainingTo(10, 20)).isEqualTo(expected);
    }

    // ── cumulativeCoinsTo ────────────────────────────────────────────────────

    @Test
    void cumulativeCoinsTo_level1_isZero() {
        assertThat(ModuleLevelTable.cumulativeCoinsTo(1)).isZero();
    }

    @Test
    void cumulativeCoinsTo_level2_equalsLevel2Cost() {
        assertThat(ModuleLevelTable.cumulativeCoinsTo(2)).isEqualTo(10_000);
    }

    // ── coinsRemainingTo ────────────────────────────────────────────────────

    @Test
    void coinsRemainingTo_alreadyAtTarget_returnsZero() {
        assertThat(ModuleLevelTable.coinsRemainingTo(50, 50)).isZero();
    }

    @Test
    void coinsRemainingTo_multiStep_equalsDifferenceOfCumulatives() {
        long expected = ModuleLevelTable.cumulativeCoinsTo(10) - ModuleLevelTable.cumulativeCoinsTo(5);
        assertThat(ModuleLevelTable.coinsRemainingTo(5, 10)).isEqualTo(expected);
    }

    // ── MAX_LEVEL constant ───────────────────────────────────────────────────

    @Test
    void maxLevel_is300() {
        assertThat(ModuleLevelTable.MAX_LEVEL).isEqualTo(300);
    }
}