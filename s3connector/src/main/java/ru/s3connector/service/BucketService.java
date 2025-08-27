package ru.s3connector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class BucketService {
    private final S3AsyncClient s3AsyncClient;

    private final S3Client s3Client;


    public void addBucket(String name) {
        CompletableFuture<CreateBucketResponse> response = s3AsyncClient.createBucket(builder -> builder.bucket(name));
        log.info("{} was created", name);
    }

    public void addRule() {
        LifecycleRule rule = LifecycleRule.builder()
                .id("auto delete")
                .expiration(LifecycleExpiration.builder().days(1).build())
                .status(ExpirationStatus.ENABLED)
                .build();
        BucketLifecycleConfiguration configuration = BucketLifecycleConfiguration.builder()
                .rules(rule)
                .build();

        s3Client.putBucketLifecycleConfiguration(request -> request.bucket("test-bucket")
                .lifecycleConfiguration(configuration));
    }

    public List<String> getAllBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        return response.buckets().stream()
                .map(Bucket::name)
                .toList();
    }

    public List<String> getRules(String name) {
        GetBucketLifecycleConfigurationResponse response = s3Client.getBucketLifecycleConfiguration(req -> req.bucket(name));
        return response.rules().stream().map(LifecycleRule::id)
                .toList();
    }

    public void createBucket() {
        CreateBucketResponse response = s3Client.createBucket(request -> request.bucket("test-bucket"));
    }

    public void getPolicy() {
        GetBucketPolicyRequest request = GetBucketPolicyRequest.builder()
                .bucket("test-bucket").build();
        GetBucketPolicyResponse response = s3Client.getBucketPolicy(request);
        log.info(response.policy());
    }

    public void addPolicyToBucker() {
        PutBucketAclRequest aclRequest = PutBucketAclRequest.builder()
                .bucket("test-bucket")
                .acl(BucketCannedACL.PUBLIC_READ).build();
        String bucketName = "test-bucket";
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


        s3Client.putBucketPolicy(req -> {
            req.bucket(bucketName)
                    .policy(policy)
                    .build();
        });
    }
}
