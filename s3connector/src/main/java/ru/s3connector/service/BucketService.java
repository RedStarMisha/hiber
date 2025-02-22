package ru.s3connector.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BucketService {
    private final S3AsyncClient s3AsyncClient;


    public void addBucket(String name) {
        CompletableFuture<CreateBucketResponse> response = s3AsyncClient.createBucket(builder -> builder.bucket(name));
    }
}
