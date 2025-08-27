package ru.s3connector.client;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Vector;


@Component
@Slf4j
@RequiredArgsConstructor
public class MinioConnector {
    private final MinioClient minioClient;

    private final FileClient fileClient;


    @Value("${s3.bucket-name}")
    private String bucketName;

    @SneakyThrows
    public void putObject(String keyName, InputStream dataStream, Long contentLength) {
        keyName = keyName == null ? "test-key" : keyName;
        String contentType = "application/octet-stream";
            ObjectWriteResponse response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(keyName)
                            .stream(dataStream, contentLength, 10485760) // -1 means unknown size
                            .contentType(contentType)
                            .build()
            );
            dataStream.close();
            log.info("Object with key {} was saved to s3", keyName);
    }

    public boolean uploadFileFromAnotherService() {
        putObjectFromDataBuffer(fileClient.getData());
        return true;
    }
    @SneakyThrows
    public boolean putObjectFromDataBuffer(Flux<DataBuffer> file) throws RuntimeException {
        String original = "22.exe";
        String contentType = "application/octet-stream";
        try (InputStream inputStream = DataBufferUtils.subscriberInputStream(file, 1)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(original)
                            .stream(inputStream, -1, 10485760) // -1 means unknown size
                            .contentType(contentType)
                            .build()
            );
            return true;
        }
    }

    @PostConstruct
    public void createBucket() throws ServerException, InsufficientDataException, ErrorResponseException,
            IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        boolean found =
                minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (found) {
            log.info("Bucket {} exists", bucketName);
        } else {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName).build());
            log.info("S3 Bucket {} was created", bucketName);
            addPolicyToBucket();
        }
        addBucketLifecycle();
    }

    @SneakyThrows
    private void addPolicyToBucket() {
        String policy = "{\n" +
                "  \"Version\": \"2012-10-17\",\n" +
                "  \"Statement\": [\n" +
                "    {\n" +
                "      \"Effect\": \"Allow\",\n" +
                "      \"Principal\": \"*\",\n" +
                "      \"Action\": [\"s3:GetObject\"],\n" +
                "      \"Resource\": [\"arn:aws:s3:::" + bucketName + "/*\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder()
                .bucket(bucketName)
                .config(policy).build());
        log.info("Policy for bucket {} was added", bucketName);
    }


    @SneakyThrows
    private void addBucketLifecycle() {
        Optional<LifecycleConfiguration> lifecycleConfiguration = Optional.ofNullable(minioClient.getBucketLifecycle(GetBucketLifecycleArgs.builder().bucket(bucketName).build()));
        lifecycleConfiguration.ifPresent(configuration -> configuration.rules().forEach(conf ->
                log.info("Current LifeCycle Expiration days - {}", conf.expiration().days())));

        LifecycleConfiguration config = buildLifecycleConfiguration();
        minioClient.setBucketLifecycle(SetBucketLifecycleArgs.builder()
                .bucket(bucketName)
                .config(config)
                .build());
        log.info("Lifecycle for bucket {} was added", bucketName);
    }

    @NotNull
    private LifecycleConfiguration buildLifecycleConfiguration() {
        List<LifecycleRule> lifecycleRules = List.of(
                new LifecycleRule(
                        Status.ENABLED,
                        null,
                        new Expiration((ZonedDateTime) null, 1, null),
                        new RuleFilter(""),
                        "Cleaning rule",
                        null,
                        null,
                        null
                ));
        return new LifecycleConfiguration(lifecycleRules);
    }

    @SneakyThrows
    public Resource getObject(String bucket, String key) {
        try {
            return new InputStreamResource(minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(key).build()));
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

}
