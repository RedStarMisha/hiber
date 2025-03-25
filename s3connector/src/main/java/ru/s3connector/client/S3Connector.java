package ru.s3connector.client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.ResponsePublisher;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Connector {
    @Getter
    private final S3Client s3Client;
    @Getter
    private final S3AsyncClient s3AsyncClient;

    private final S3Presigner s3Presigner;

    private final String BUCKET_NAME = "test-bucket";

    private final String PREFIX = "lego/";

    public CompletableFuture<PutObjectResponse> putFlux(long length, String fileName, MediaType mediaType, Flux<ByteBuffer> body) {
        return s3AsyncClient
                .putObject(PutObjectRequest.builder()
                                .bucket(BUCKET_NAME)
                                .contentLength(length)
                                .key(PREFIX + fileName)
                                .contentType(mediaType.toString())
                                .metadata(new HashMap<>())
                                .build(),
                        AsyncRequestBody.fromPublisher(body));
    }

    public CompletableFuture<PutObjectResponse> putFluxWithTags(long length, String fileName, MediaType mediaType, Flux<ByteBuffer> body) {
        Tagging tagging = Tagging.builder()
                .tagSet(Tag.builder()
                                .key("Tag 1")
                                .value("This is tag 1")
                                .build(),
                        Tag.builder()
                                .key("Tag 2")
                                .value("This is tag 2")
                                .build())
                .build();
        return s3AsyncClient
                .putObject(PutObjectRequest.builder()
                                .bucket(BUCKET_NAME)
                                .contentLength(length)
                                .key(PREFIX + fileName)
                                .contentType(mediaType.toString())
                                .metadata(new HashMap<>())
                                .tagging(tagging)
                                .build(),
                        AsyncRequestBody.fromPublisher(body));
    }

    public void createBucket() {
        CreateBucketResponse response = s3Client.createBucket(request -> request.bucket("test-bucket"));
    }

    public List<String> getAllBuckets() {
        ListBucketsResponse response = s3Client.listBuckets();
        return response.buckets().stream()
                .map(Bucket::name)
                .toList();
    }

    public URL uploadObjects(MultipartFile file) {
        String original = file.getOriginalFilename();
        try {
            PutObjectResponse response = s3Client.putObject(req -> {
                        req.bucket("test-bucket")
                                .key("lego/" + original);
                    },
                    RequestBody.fromBytes(file.getBytes()));

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(preReq -> {
                preReq.getObjectRequest(req -> {
                    req.bucket("test-bucket").key("lego/" + original);
                }).signatureDuration(Duration.ofHours(1));
            });
            return presignedRequest.url();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public URL uploadObjectsMultipath(MultipartFile file) {
        try {
            String original = file.getOriginalFilename();
            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                    .bucket("test-bucket")
                    .key("lego/" + original)
                    .build();
            // Делаем запрос на инициализацию многокомпонентной загрузки. В ответ получаем идентицикатор который уже
            // используем при загрузки частей
            CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);


            String uploadId = createResponse.uploadId();

            List<CompletedPart> completedParts = getPartsOfFile(file.getBytes(), original, uploadId);

            CompletedMultipartUpload completedUpload = CompletedMultipartUpload.builder()
                    .parts(completedParts)
                    .build();

            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                    .bucket("test-bucket")
                    .key("lego/" + original)
                    .uploadId(uploadId)
                    .multipartUpload(completedUpload)
                    .build();

            CompleteMultipartUploadResponse completeResponse = s3Client.completeMultipartUpload(completeRequest);


            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(preReq -> {
                preReq.getObjectRequest(req -> {
                    req.bucket("test-bucket").key("lego/" + original);
                }).signatureDuration(Duration.ofHours(1));
            });
            return presignedRequest.url();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<CompletedPart> getPartsOfFile(byte[] bytes, String name, String uploadId) {
        List<CompletedPart> completedParts = new ArrayList<>();
        int partNumber = 1;
        int BUFFER_SIZE = 5 * 1024 * 1024;
//        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE); // Set your preferred part size (5 MB in this example)

// Read the file and upload each part
//        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
        long fileSize = bytes.length;
        int position = 0;

        while (position < fileSize) {
            int remaining = Math.min(BUFFER_SIZE, bytes.length - position);
            ByteBuffer byteBuffer = ByteBuffer.allocate(remaining);

            byteBuffer.put(bytes, position, remaining);

            byteBuffer.flip();

            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket("test-bucket")
                    .key("lego/" + name)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .contentLength((long) remaining)
                    .build();

            UploadPartResponse response = s3Client.uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(byteBuffer));

            completedParts.add(CompletedPart.builder()
                    .partNumber(partNumber)
                    .eTag(response.eTag())
                    .build());

//                buffer.clear();
            position += remaining;
            partNumber++;
        }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return completedParts;
    }

    public byte[] getFile(String name) {
        ResponseBytes<GetObjectResponse> responseBytes =
                s3Client.getObjectAsBytes(req -> req.bucket("test-bucket")
                        .key("lego/" + name));
        return responseBytes.asByteArray();
    }
    public Resource getFileAsStream(String name) {
        ResponseInputStream<GetObjectResponse> responseInputStream =
                s3Client.getObject(req -> req.bucket("test-bucket")
                        .key("lego/" + name));
        Resource resource = new InputStreamResource(responseInputStream);
        return resource;
    }

    public void getTags(String name) {
        GetObjectTaggingResponse tags = s3Client.getObjectTagging(req -> req.bucket("test-bucket")
                .key(name));
        List<Tag> tagSet = tags.tagSet();
        for (Tag tag : tagSet) {
            System.out.println(tag.key());
            System.out.println(tag.value());
        }
    }

    public void getFileList() {
        ListObjectsResponse response = s3Client.listObjects(req -> req.bucket("test-bucket").prefix("lego"));
        System.out.println(response.prefix());
        response.contents().forEach(obj -> log.info(obj.key()));
    }

    public Mono<ResponsePublisher<GetObjectResponse>> getFileAsinc(String name) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key("lego/" + name)
                .bucket("test-bucket")
                .build();
        CompletableFuture<ResponsePublisher<GetObjectResponse>> responseBytes = s3AsyncClient
                .getObject(objectRequest, AsyncResponseTransformer.toPublisher());
        return Mono.fromFuture(responseBytes);
//        return response.thenAccept(objectBytes -> {
//            try {
//                byte[] data = objectBytes.asByteArray();
//                ByteBuffer byteBuffer = ByteBuffer.wrap(data);
//                Path filePath = Paths.get(path);
//                Files.write(filePath, data);
//                logger.info("Successfully obtained bytes from an S3 object");
//            } catch (IOException ex) {
//                throw new RuntimeException("Failed to write data to file", ex);
//            }
//        }).whenComplete((resp, ex) -> {
//            if (ex != null) {
//                throw new RuntimeException("Failed to get object bytes from S3", ex);
//            }
//        });
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

    public List<String> getRules(String name) {
        GetBucketLifecycleConfigurationResponse response = s3Client.getBucketLifecycleConfiguration(req -> req.bucket(name));
        return response.rules().stream().map(LifecycleRule::id)
                .toList();
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void uploadFile(MultipartFile file) {
        String original = file.getOriginalFilename();
        try {
//            PutObjectResponse response = s3Client.putObject(req ->
//                            req.bucket("test-bucket")
//                                    .key("lego/" + original),
//                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket("test-bucket")
                    .key("lego/" + original).build();
            CompletableFuture<PutObjectResponse> future = s3AsyncClient.putObject(request,
                    AsyncRequestBody.fromInputStream(file.getInputStream(), file.getSize(), executorService));

            future.thenAccept(responseFuture -> log.info("complete {}", file.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
