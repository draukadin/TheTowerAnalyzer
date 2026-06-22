package com.pphi.tower.config;

import org.springframework.stereotype.Service;

@Service
public class SetupStateService {

    public enum Step { CONFIG, COMPLETE }

    private final AwsProperties aws;

    public SetupStateService(AwsProperties aws) {
        this.aws = aws;
    }

    public Step currentStep() {
        String playerId = aws.getPlayerId();
        return (playerId == null || playerId.isBlank()) ? Step.CONFIG : Step.COMPLETE;
    }

    public boolean isComplete() {
        return currentStep() == Step.COMPLETE;
    }
}