package com.pphi.tower.model.battlediagnostics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record DiagnosisResult(
        @JsonProperty("primaryFailure")       FailureType primaryFailure,
        @JsonProperty("confidence")           Confidence confidence,
        @JsonProperty("explanation")          String explanation,
        @JsonProperty("swarmKillShare")       double swarmKillShare,
        @JsonProperty("heavyKillShare")       double heavyKillShare,
        @JsonProperty("lifeStealRaw")         double lifeStealRaw,
        @JsonProperty("totalDamageTakenRaw")  double totalDamageTakenRaw,
        @JsonProperty("blockEfficiency")      double blockEfficiency,
        @JsonProperty("vampireDensity")       double vampireDensity,
        @JsonProperty("rangedDensity")        double rangedDensity,
        @JsonProperty("observations")         List<Observation> observations) {

    @JsonCreator
    public DiagnosisResult { }

    public boolean isHighConfidence() { return confidence == Confidence.HIGH; }

    public String swarmKillShareFormatted()  { return pct(swarmKillShare); }
    public String heavyKillShareFormatted()  { return pct(heavyKillShare); }
    public String blockEfficiencyFormatted() { return pct(blockEfficiency); }
    public String vampireDensityFormatted()  { return pct(vampireDensity); }
    public String rangedDensityFormatted()   { return pct(rangedDensity); }

    private static String pct(double v) { return String.format("%.2f %%", v * 100.0); }
}
