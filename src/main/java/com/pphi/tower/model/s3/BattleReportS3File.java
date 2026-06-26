package com.pphi.tower.model.s3;

/**
 * S3-backed battle report. The S3 key format is:
 *   <playerId>/<timestamp>_<runType>[_<dissonanceType>].txt
 * e.g. player123/2026-06-21T12-34-56-789Z_Dissonance_Attack.txt
 *
 * The Lambda (ingest-report.mjs) reads the player's current version from DDB
 * and injects "Tower Era\t<version>" at line 2 before uploading, so the content
 * is complete as stored.
 *
 * The id stored in the runs table is filename() only (no player-prefix slash),
 * matching the Drive-backed id format so REST routing stays slash-free.
 */
public record BattleReportS3File(String key, String rawContents) {

    /**
     * Returns the raw content as-is. The Lambda injects "Tower Era\t<version>"
     * at line 2 before uploading to S3, so no synthetic injection is needed here.
     */
    public String contents() {
        return rawContents;
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
