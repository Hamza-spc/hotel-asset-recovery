package com.hotel.lostfound.inventory.storage;

import com.hotel.lostfound.inventory.ObjectStorage;
import java.io.InputStream;
import java.net.URI;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
class MinioObjectStorage implements ObjectStorage {

    private final S3Client s3;
    private final String bucket;

    MinioObjectStorage(MinioProperties properties) {
        this.bucket = properties.bucket();
        this.s3 = S3Client.builder()
                .endpointOverride(URI.create(properties.endpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.accessKey(), properties.secretKey())))
                .region(Region.of(properties.region()))
                .forcePathStyle(true)
                .build();
        ensureBucket();
    }

    private void ensureBucket() {
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (S3Exception ex) {
            s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        }
    }

    @Override
    public String store(String objectKey, String contentType, InputStream content, long size) {
        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(objectKey)
                        .contentType(contentType)
                        .contentLength(size)
                        .build(),
                RequestBody.fromInputStream(content, size));
        return objectKey;
    }

    @Override
    public StoredObject load(String objectKey) {
        var response = s3.getObject(GetObjectRequest.builder().bucket(bucket).key(objectKey).build());
        try {
            String contentType = response.response().contentType() == null
                    ? "application/octet-stream"
                    : response.response().contentType();
            return new StoredObject(contentType, response.readAllBytes());
        } catch (Exception ex) {
            throw new IllegalStateException("Could not read stored photo " + objectKey, ex);
        }
    }
}
