package com.pphi.tower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pphi.tower.exceptions.ReportNotFoundException;
import com.pphi.tower.fixtures.BattleHistoryFixtures;
import com.pphi.tower.model.battlediagnostics.FailureType;
import com.pphi.tower.repository.RunRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiagnosticServiceTest {

    @Mock
    private RunRepository repository;

    private DiagnosticService service;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        service = new DiagnosticService(repository, mapper);
    }

    @Test
    void diagnose_validId_delegatesToBattleDiagnostic() throws Exception {
        var history = BattleHistoryFixtures.vampireAuraActive();
        String payload = mapper.writeValueAsString(history);
        when(repository.findPayloadById("run-1")).thenReturn(Optional.of(payload));

        var result = service.diagnose("run-1");

        assertThat(result.primaryFailure()).isEqualTo(FailureType.VAMPIRE_DRAIN_LOCK);
    }

    @Test
    void diagnose_missingId_throwsReportNotFoundException() {
        when(repository.findPayloadById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.diagnose("missing"))
                .isInstanceOf(ReportNotFoundException.class);
    }

    @Test
    void diagnose_malformedJson_throwsRuntimeException() {
        when(repository.findPayloadById("bad")).thenReturn(Optional.of("{not valid json"));

        assertThatThrownBy(() -> service.diagnose("bad"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("bad");
    }
}