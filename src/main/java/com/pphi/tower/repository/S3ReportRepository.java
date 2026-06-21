package com.pphi.tower.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectTaggingRequest;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.s3.model.Tag;
import software.amazon.awssdk.services.s3.model.Tagging;

import java.io.IOException;
import java.util.List;

@Repository
@ConditionalOnBean(S3Client.class)
public class S3ReportRepository {

    private final S3Client s3;

    public S3ReportRepository(S3Client s3) {
        this.s3 = s3;
    }

    public List<String> listKeys(String bucket, String playerId) {
        List<String> keys = new java.util.ArrayList<>();
        String continuationToken = null;
        do {
            var req = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(playerId + "/")
                    .continuationToken(continuationToken)
                    .build();
            var resp = s3.listObjectsV2(req);
            resp.contents().stream()
                    .map(S3Object::key)
                    .filter(k -> k.endsWith(".txt"))
                    .forEach(keys::add);
            continuationToken = resp.isTruncated() ? resp.nextContinuationToken() : null;
        } while (continuationToken != null);
        return keys;
    }

    public String downloadAsString(String bucket, String key) throws IOException {
        GetObjectRequest req = GetObjectRequest.builder().bucket(bucket).key(key).build();
        try (ResponseInputStream<GetObjectResponse> stream = s3.getObject(req)) {
            return new String(stream.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    public void markProcessed(String bucket, String key) {
        s3.putObjectTagging(PutObjectTaggingRequest.builder()
                .bucket(bucket)
                .key(key)
                .tagging(Tagging.builder()
                        .tagSet(Tag.builder().key("processed").value("true").build())
                        .build())
                .build());
    }
}
