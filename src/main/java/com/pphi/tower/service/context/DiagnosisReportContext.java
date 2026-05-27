package com.pphi.tower.service.context;

import com.pphi.tower.model.battlediagnostics.DiagnosisResult;
import com.pphi.tower.model.battlediagnostics.Observation;

public class DiagnosisReportContext implements ChatContext {

    private final DiagnosisResult diagnosis;
    private final String reportId;

    public DiagnosisReportContext(DiagnosisResult diagnosis, String reportId) {
        this.diagnosis = diagnosis;
        this.reportId = reportId;
    }

    @Override
    public String getLabel() {
        return "Diagnosis Report";
    }

    @Override
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== The Tower Battle Run Diagnosis ===\n\n");
        sb.append("Report ID: ").append(reportId).append("\n\n");
        sb.append("Primary Failure: ").append(diagnosis.primaryFailure()).append("\n");
        sb.append("Confidence: ").append(diagnosis.confidence()).append("\n");
        sb.append("Explanation: ").append(diagnosis.explanation()).append("\n\n");

        sb.append("--- Diagnostic Metrics ---\n");
        sb.append(String.format("  %-26s %s%n",  "Swarm Kill Share:",    diagnosis.swarmKillShareFormatted()));
        sb.append(String.format("  %-26s %s%n",  "Heavy Kill Share:",    diagnosis.heavyKillShareFormatted()));
        sb.append(String.format("  %-26s %s%n",  "Block Efficiency:",    diagnosis.blockEfficiencyFormatted()));
        sb.append(String.format("  %-26s %s%n",  "Vampire Density:",     diagnosis.vampireDensityFormatted()));
        sb.append(String.format("  %-26s %s%n",  "Ranged Density:",      diagnosis.rangedDensityFormatted()));
        sb.append(String.format("  %-26s %.2f%n","Life Steal (raw):",    diagnosis.lifeStealRaw()));
        sb.append(String.format("  %-26s %.2f%n","Total Damage Taken:",  diagnosis.totalDamageTakenRaw()));
        sb.append("\n");

        if (diagnosis.observations() != null && !diagnosis.observations().isEmpty()) {
            sb.append("--- Observations ---\n");
            for (Observation obs : diagnosis.observations()) {
                sb.append("  [").append(obs.label()).append("]\n");
                sb.append("  ").append(obs.detail()).append("\n\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return getContent();
    }
}
