package com.pphi.tower.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Repository
@ConditionalOnBean(S3Client.class)
public class S3TournamentRepository {

    private static final String PREFIX = "tournaments/";
    private static final String CONTENT_TYPE = "text/csv";

    private final S3Client s3;

    public S3TournamentRepository(S3Client s3) {
        this.s3 = s3;
    }

    public void put(String bucket, String key, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(CONTENT_TYPE)
                .contentLength((long) bytes.length)
                .build();
        s3.putObject(req, RequestBody.fromBytes(bytes));
    }

    public List<String> listKeys(String bucket) {
        return listKeys(bucket, PREFIX);
    }

    public List<String> listKeys(String bucket, String prefix) {
        List<String> keys = new ArrayList<>();
        String continuationToken = null;
        do {
            var req = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(prefix)
                    .continuationToken(continuationToken)
                    .build();
            var resp = s3.listObjectsV2(req);
            resp.contents().stream()
                    .map(S3Object::key)
                    .filter(k -> k.endsWith(".csv"))
                    .forEach(keys::add);
            continuationToken = resp.isTruncated() ? resp.nextContinuationToken() : null;
        } while (continuationToken != null);
        return keys;
    }

    public String downloadAsString(String bucket, String key) throws IOException {
        GetObjectRequest req = GetObjectRequest.builder().bucket(bucket).key(key).build();
        try (ResponseInputStream<GetObjectResponse> stream = s3.getObject(req)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
