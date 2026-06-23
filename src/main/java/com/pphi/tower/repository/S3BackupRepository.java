package com.pphi.tower.repository;

import com.pphi.tower.model.s3.S3BackupObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.MetadataDirective;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;
import software.amazon.awssdk.services.s3.model.TaggingDirective;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Raw S3 access for database backups (Action item 8). Conditional on an
 * {@link S3Client} bean, so legacy Drive-only users never load it.
 *
 * <p>The two-tag retention scheme: the newest backup carries {@code type=backup-latest}
 * (exempt from lifecycle, kept forever); superseded backups are demoted to
 * {@code type=backup}, which the bucket lifecycle rule expires 30 days later.
 */
@Repository
@ConditionalOnBean(S3Client.class)
public class S3BackupRepository {

    static final String TAG_KEY = "type";
    static final String TAG_LATEST = "backup-latest";
    static final String TAG_DEMOTED = "backup";
    private static final String CONTENT_TYPE = "application/octet-stream";

    private final S3Client s3;

    public S3BackupRepository(S3Client s3) {
        this.s3 = s3;
    }

    /** Upload a new backup, tagging it {@code type=backup-latest}. */
    public void putLatest(String bucket, String key, Path file) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(CONTENT_TYPE)
                .tagging(taggingFor(TAG_LATEST))
                .build();
        s3.putObject(req, RequestBody.fromFile(file));
    }

    /**
     * Demote a backup from {@code backup-latest} to {@code backup} via an in-place
     * {@code CopyObject}. The copy rewrites the object, resetting its {@code LastModified}
     * so the 30-day lifecycle expiry clock starts at the moment of supersession.
     * {@code MetadataDirective=REPLACE} makes the self-copy legal.
     */
    public void demote(String bucket, String key) {
        CopyObjectRequest req = CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey(key)
                .destinationBucket(bucket)
                .destinationKey(key)
                .contentType(CONTENT_TYPE)
                .metadataDirective(MetadataDirective.REPLACE)
                .taggingDirective(TaggingDirective.REPLACE)
                .tagging(taggingFor(TAG_DEMOTED))
                .build();
        s3.copyObject(req);
    }

    /** List backups under {@code <playerId>/backups/}, newest first, with their latest flag. */
    public List<S3BackupObject> listBackups(String bucket, String prefix) {
        List<S3Object> objects = new ArrayList<>();
        String continuationToken = null;
        do {
            var req = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(prefix)
                    .continuationToken(continuationToken)
                    .build();
            var resp = s3.listObjectsV2(req);
            resp.contents().stream()
                    .filter(o -> o.key().endsWith(".db"))
                    .forEach(objects::add);
            continuationToken = resp.isTruncated() ? resp.nextContinuationToken() : null;
        } while (continuationToken != null);

        List<S3BackupObject> backups = new ArrayList<>();
        for (S3Object o : objects) {
            backups.add(new S3BackupObject(
                    o.key(),
                    fileName(o.key()),
                    o.size(),
                    o.lastModified(),
                    TAG_LATEST.equals(typeTag(bucket, o.key()))));
        }
        backups.sort((a, b) -> b.lastModified().compareTo(a.lastModified()));
        return backups;
    }

    /** Download an object's bytes to a local file (used for restore staging). */
    public void downloadToFile(String bucket, String key, Path target) {
        GetObjectRequest req = GetObjectRequest.builder().bucket(bucket).key(key).build();
        try (ResponseInputStream<GetObjectResponse> in = s3.getObject(req);
             OutputStream out = Files.newOutputStream(target)) {
            in.transferTo(out);
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("Failed to download backup to " + target, e);
        }
    }

    /** Current value of the {@code type} tag, or {@code null} if untagged. */
    private String typeTag(String bucket, String key) {
        var resp = s3.getObjectTagging(GetObjectTaggingRequest.builder()
                .bucket(bucket).key(key).build());
        return resp.tagSet().stream()
                .filter(t -> TAG_KEY.equals(t.key()))
                .map(Tag::value)
                .findFirst()
                .orElse(null);
    }

    private static Tagging taggingFor(String value) {
        return Tagging.builder()
                .tagSet(Tag.builder().key(TAG_KEY).value(value).build())
                .build();
    }

    private static String fileName(String key) {
        int slash = key.lastIndexOf('/');
        return slash >= 0 ? key.substring(slash + 1) : key;
    }
}
