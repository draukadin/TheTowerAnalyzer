package com.pphi.tower.analyzers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pphi.tower.fixtures.BattleHistoryFixtures;
import com.pphi.tower.model.battlediagnostics.Confidence;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlediagnostics.FailureType;
import com.pphi.tower.model.battlehistory.BattleHistory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BattleDiagnosticTest {

    private static final BattleDiagnostic diagnostic = new BattleDiagnostic();
    private static final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static BattleHistory orbLayerCollapseRun;

    @BeforeAll
    static void loadFixtures() throws IOException {
        orbLayerCollapseRun = mapper.readValue(
                new File("src/test/resources/orb_layer_collapse_run.json"),
                BattleHistory.class);
    }

    // ── Existing JSON fixture test (preserved) ───────────────────────────────

    @Test
    void run_1wAsWWEg_diagnosesVampireDrainLock_mediumConfidence() {
        DiagnosisResult result = diagnostic.analyzeReport(orbLayerCollapseRun);

        assertEquals(FailureType.VAMPIRE_DRAIN_LOCK, result.primaryFailure());
        assertEquals(Confidence.MEDIUM, result.confidence());
    }

    // ── Parameterized: all failure-type checks ────────────────────────────────

    static Stream<Arguments> diagnosticScenarios() {
        return Stream.of(
                // Check 1a: Vampire aura fully suppresses regen (lifeSteal+towerRegen=0)
                Arguments.of("Check 1a - VampireAuraActive",
                        BattleHistoryFixtures.vampireAuraActive(),
                        FailureType.VAMPIRE_DRAIN_LOCK, Confidence.MEDIUM),

                // Check 1b: killedBy Vampire + life steal suppressed, density >= 3% → HIGH
                Arguments.of("Check 1b - VampireDrainLock HIGH",
                        BattleHistoryFixtures.vampireDrainLock(),
                        FailureType.VAMPIRE_DRAIN_LOCK, Confidence.HIGH),

                // Check 2: killedBy Boss, density >= 2% → HIGH
                Arguments.of("Check 2 - BossPressureCollapse HIGH",
                        BattleHistoryFixtures.killedByBoss(),
                        FailureType.BOSS_PRESSURE_COLLAPSE, Confidence.HIGH),

                // Check 3: killedBy Ranged, healthy swarm, rangedDensity >= 15% → HIGH
                Arguments.of("Check 3 - RangedSlipThrough HIGH",
                        BattleHistoryFixtures.rangedSlipThrough(),
                        FailureType.RANGED_SLIP_THROUGH, Confidence.HIGH),

                // Check 4: orbShare < 25%, swarmShare < 55% → HIGH
                Arguments.of("Check 4 - OrbLayerCollapse HIGH",
                        BattleHistoryFixtures.orbLayerCollapse(),
                        FailureType.ORB_LAYER_COLLAPSE, Confidence.HIGH),

                // Check 5: swarmShare < 55% but orbShare >= 25% → MEDIUM
                Arguments.of("Check 5 - CrowdControlBreach MEDIUM",
                        BattleHistoryFixtures.ccBreach(),
                        FailureType.CROWD_CONTROL_BREACH, Confidence.MEDIUM),

                // Check 6: heavyShare > 40%, killedBy Tank → MEDIUM
                Arguments.of("Check 6 - EliteOverwhelm MEDIUM",
                        BattleHistoryFixtures.eliteOverwhelm(),
                        FailureType.ELITE_OVERWHELM, Confidence.MEDIUM),

                // Check 7: blockEfficiency < 30% → MEDIUM
                Arguments.of("Check 7 - DefenceSaturation MEDIUM",
                        BattleHistoryFixtures.defenceSaturation(),
                        FailureType.DEFENCE_SATURATION, Confidence.MEDIUM),

                // Check 9: missileKills > 0, missileShare < 5% → LOW
                Arguments.of("Check 9 - InterceptGap LOW",
                        BattleHistoryFixtures.interceptGap(),
                        FailureType.INTERCEPT_GAP, Confidence.LOW),

                // Check 10: deathWaveActive, dwKillShare < 10% → MEDIUM
                Arguments.of("Check 10 - DeathWaveDesync MEDIUM",
                        BattleHistoryFixtures.deathWaveDesync(),
                        FailureType.DEATH_WAVE_DESYNC, Confidence.MEDIUM),

                // Check 11: rayKills > 0, rayShare < 3% → LOW
                Arguments.of("Check 11 - DeathRayUnderperformance LOW",
                        BattleHistoryFixtures.deathRayUnderperformance(),
                        FailureType.DEATH_RAY_UNDERPERFORMANCE, Confidence.LOW),

                // Fallthrough: all indicators healthy
                Arguments.of("Fallthrough - UnknownVariance LOW",
                        BattleHistoryFixtures.unknownVariance(),
                        FailureType.UNKNOWN_DUE_TO_VARIANCE, Confidence.LOW)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("diagnosticScenarios")
    void diagnosticCheck(String label, BattleHistory history,
                         FailureType expectedType, Confidence expectedConf) {
        DiagnosisResult result = diagnostic.analyzeReport(history);
        assertThat(result.primaryFailure())
                .as("%s — primaryFailure", label)
                .isEqualTo(expectedType);
        assertThat(result.confidence())
                .as("%s — confidence", label)
                .isEqualTo(expectedConf);
    }

    // ── Secondary observation assertions ────────────────────────────────────

    @Test
    void result_alwaysHasNonNullObservations() {
        DiagnosisResult result = diagnostic.analyzeReport(BattleHistoryFixtures.unknownVariance());
        assertThat(result.observations()).isNotNull();
    }

    @Test
    void result_alwaysHasNonBlankExplanation() {
        DiagnosisResult result = diagnostic.analyzeReport(BattleHistoryFixtures.vampireAuraActive());
        assertThat(result.explanation()).isNotBlank();
    }

    @Test
    void vampireDrainLock_explanation_mentionsLifeSteal() {
        DiagnosisResult result = diagnostic.analyzeReport(BattleHistoryFixtures.vampireDrainLock());
        assertThat(result.explanation()).containsIgnoringCase("life steal");
    }

    @Test
    void bossPressureCollapse_explanation_mentionsBoss() {
        DiagnosisResult result = diagnostic.analyzeReport(BattleHistoryFixtures.killedByBoss());
        assertThat(result.explanation()).containsIgnoringCase("boss");
    }

    @Test
    void defenceSaturation_metricsPopulated() {
        DiagnosisResult result = diagnostic.analyzeReport(BattleHistoryFixtures.defenceSaturation());
        assertThat(result.blockEfficiency()).isLessThan(0.30);
    }

    @Test
    void orbLayerCollapse_swarmSharePopulated() {
        DiagnosisResult result = diagnostic.analyzeReport(BattleHistoryFixtures.orbLayerCollapse());
        assertThat(result.swarmKillShare()).isLessThan(0.55);
    }
}