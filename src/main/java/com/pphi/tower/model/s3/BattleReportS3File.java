package com.pphi.tower.model.s3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * S3-backed battle report. The S3 key format is:
 *   <playerId>/<timestamp>_<runType>[_<dissonanceType>].txt
 * e.g. player123/2026-06-21T12-34-56-789Z_Dissonance_Attack.txt
 *
 * Tower era is not available in the S3 key. We inject "Tower Era\t" as line 2
 * (same slot as BattleReportDriveFile) so BattleReport's field count stays correct;
 * TowerEra.parse("") returns TowerEra(1,0,0) as the default era.
 *
 * The id stored in the runs table is filename() only (no player-prefix slash),
 * matching the Drive-backed id format so REST routing stays slash-free.
 */
public record BattleReportS3File(String key, String rawContents) {

    /** Contents with the synthetic Tower Era line injected at position 1. */
    public String contents() {
        List<String> lines = rawContents.lines().collect(Collectors.toCollection(ArrayList::new));
        if (lines.size() > 1) {
            lines.add(1, "Tower Era\t");
        }
        return String.join(System.lineSeparator(), lines);
    }

    /** Used as the primary key in the runs table. */
    public String id() {
        return key;
    }

    /** Filename segment after the last '/'. */
    public String filename() {
        int slash = key.lastIndexOf('/');
        return slash >= 0 ? key.substring(slash + 1) : key;
    }

    /**
     * Extracts the run type from the filename.
     * filename = "<timestamp>_<runType>[_<dissonanceType>].txt"
     * split("_")[1] gives the run type token (may have ".txt" appended for single-word types).
     */
    public String runType() {
        String[] parts = filename().split("_");
        if (parts.length < 2) return "Unknown";
        return parts[1].replace(".txt", "");
    }

    /** Returns the dissonance sub-type (e.g. "Attack") or null for non-Dissonance runs. */
    public String dissonanceType() {
        String[] parts = filename().split("_");
        if (parts.length < 3 || !"Dissonance".equalsIgnoreCase(parts[1])) return null;
        return parts[2].replace(".txt", "");
    }
}
