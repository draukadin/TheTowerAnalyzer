package com.pphi.tower.model.s3;

import java.time.Instant;

/**
 * A single database backup object stored under {@code <player_id>/backups/}.
 *
 * @param key          full S3 key
 * @param fileName     last path segment (e.g. {@code analyzer_2026-06-22_10-30-00.db})
 * @param size         object size in bytes
 * @param lastModified S3 {@code LastModified} (reset on demotion, see Action item 8)
 * @param latest       true if tagged {@code type=backup-latest} (the kept-forever copy)
 */
public record S3BackupObject(
        String key,
        String fileName,
        long size,
        Instant lastModified,
        boolean latest) {
}
