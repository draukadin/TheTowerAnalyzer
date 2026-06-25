package com.pphi.tower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pphi.tower.exceptions.ReportNotFoundException;
import com.pphi.tower.fixtures.BattleHistoryFixtures;
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
class ComparisonServiceTest {

    @Mock
    private RunRepository repository;

    private ComparisonService service;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        service = new ComparisonService(repository, mapper);
    }

    private String serialise(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    @Test
    void compare_returnsListOfThree() throws Exception {
        var h1 = BattleHistoryFixtures.unknownVariance();
        var h2 = BattleHistoryFixtures.killedByBoss();
        when(repository.findPayloadById("id1")).thenReturn(Optional.of(serialise(h1)));
        when(repository.findPayloadById("id2")).thenReturn(Optional.of(serialise(h2)));

        var result = service.compare("id1", "id2");

        assertThat(result).hasSize(3);
    }

    @Test
    void compare_missingId1_throwsReportNotFoundException() {
        when(repository.findPayloadById("id1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.compare("id1", "id2"))
                .isInstanceOf(ReportNotFoundException.class);
    }

    @Test
    void compare_malformedPayload_throwsRuntimeException() {
        when(repository.findPayloadById("id1")).thenReturn(Optional.of("not-valid-json"));
        assertThatThrownBy(() -> service.compare("id1", "id2"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("id1");
    }

    @Test
    void compare_missingId2_throwsReportNotFoundException() throws Exception {
        var h1 = BattleHistoryFixtures.unknownVariance();
        when(repository.findPayloadById("id1")).thenReturn(Optional.of(serialise(h1)));
        when(repository.findPayloadById("id2")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.compare("id1", "id2"))
                .isInstanceOf(ReportNotFoundException.class);
    }
}