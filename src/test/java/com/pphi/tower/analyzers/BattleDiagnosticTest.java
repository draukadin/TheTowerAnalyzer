package com.pphi.tower.analyzers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pphi.tower.model.battlediagnostics.Confidence;
import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlediagnostics.FailureType;
import com.pphi.tower.model.battlehistory.BattleHistory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

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

    @Test
    void run_1wAsWWEg_diagnosesVampireDrainLock_mediumConfidence() {
        DiagnosisResult result = diagnostic.analyzeReport(orbLayerCollapseRun);

        assertEquals(FailureType.VAMPIRE_DRAIN_LOCK, result.primaryFailure());
        assertEquals(Confidence.MEDIUM, result.confidence());
    }
}
