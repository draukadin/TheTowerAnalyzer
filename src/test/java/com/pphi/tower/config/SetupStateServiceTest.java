package com.pphi.tower.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.pphi.tower.config.SetupStateService.Step;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetupStateServiceTest {

    @Mock AwsProperties aws;

    SetupStateService sut;

    @BeforeEach
    void setUp() { sut = new SetupStateService(aws); }

    @ParameterizedTest @NullAndEmptySource @ValueSource(strings = {" "})
    void noPlayerId_returnsConfigStep(String id) {          // 1.1
        when(aws.getPlayerId()).thenReturn(id);
        assertThat(sut.currentStep()).isEqualTo(Step.CONFIG);
        assertThat(sut.isComplete()).isFalse();
    }

    @Test
    void validPlayerId_returnsCompleteStep() {              // 1.2
        when(aws.getPlayerId()).thenReturn("abc123");
        assertThat(sut.currentStep()).isEqualTo(Step.COMPLETE);
        assertThat(sut.isComplete()).isTrue();
    }

    @Test
    void stepEnum_hasExactlyConfigAndComplete() {           // 1.3
        assertThat(Step.values())
                .containsExactlyInAnyOrder(Step.CONFIG, Step.COMPLETE);
    }
}
