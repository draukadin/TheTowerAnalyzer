package com.pphi.tower.util;

import com.pphi.tower.model.DissonanceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

class DissonanceBoostUtilityTest {

    static Stream<Arguments> inputs() {
        return Stream.of(
                // boundary: max waves, no echo, attack/utility caps
                Arguments.of(5000, List.of(5000), 0, DissonanceType.ATTACK, bd(5.00)),
                Arguments.of(5000, List.of(5000), 0, DissonanceType.UTILITY, bd(3.00)),
                // real-world mixed tiers, attack
                Arguments.of(0, List.of(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 4856, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), 0, DissonanceType.ATTACK, bd(1.22)),
                // echo level > 0: others contribution scales with echo
                Arguments.of(5000, List.of(5000, 5000), 10, DissonanceType.ATTACK, bd(5.22)),
                // all zeros: no dissonance progress, boost is 1.00
                Arguments.of(0, List.of(0, 0, 0, 0, 0), 5, DissonanceType.ATTACK, bd(1.00)),
                // utility mirror of real-world case: coefficient 2 vs 4
                Arguments.of(0, List.of(5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 4856, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0), 0, DissonanceType.UTILITY, bd(1.11)),
                // single-tier list: others=0, only current contributes
                Arguments.of(3000, List.of(3000), 5, DissonanceType.ATTACK, bd(2.64)),
                // tierPb above 5000 cap: same result as 5000
                Arguments.of(7500, List.of(7500), 0, DissonanceType.ATTACK, bd(5.00)),
                // DEFENSE type: same multiplier as ATTACK (5), same result
                Arguments.of(5000, List.of(5000), 0, DissonanceType.DEFENSE, bd(5.00)),
                // UW type: same multiplier as ATTACK (5), same result
                Arguments.of(5000, List.of(5000), 0, DissonanceType.UW, bd(5.00)),
                // wave=0 and echoLevel=0 and multi-tier: others contribution = 0 (all zeros)
                Arguments.of(0, List.of(0, 0, 0), 0, DissonanceType.ATTACK, bd(1.00)),
                // echoLevel=0 multi-tier (others != 0): others * 1 * 0.005
                Arguments.of(5000, List.of(5000, 5000), 0, DissonanceType.ATTACK, bd(5.02))
        );
    }

    private static BigDecimal bd(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    @MethodSource("inputs")
    @ParameterizedTest
    void testComputeDissonanceBonus(
            int personalBest, List<Integer> tiersPersonalBest, int labLevel,
            DissonanceType dissonanceType, BigDecimal expected) {
        BigDecimal actual = DissonanceBoostUtility.compute(personalBest, tiersPersonalBest, labLevel, dissonanceType);
        Assertions.assertEquals(expected, actual);
    }
}
