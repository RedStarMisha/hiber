package ru.s3connector.client;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.s3connector.model.MyContentStreamProvider;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.core.async.ResponsePublisher;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
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
@Slf4j
public class S3Connector {
    @Getter
    private final S3Client s3Client;
    @Getter
    private final S3AsyncClient s3AsyncClient;

    private final S3AsyncClient s3AsyncCrtClient;

    private final S3Presigner s3Presigner;

    private final MinioClient minioClient;

    public S3Connector(S3Client s3Client,
                       @Qualifier("s3AsyncClient") S3AsyncClient s3AsyncClient,
                       @Qualifier("s3AsyncCrtClient") S3AsyncClient s3AsyncCrtClient,
                       S3Presigner s3Presigner,
                       MinioClient minioClient) {
        this.s3Client = s3Client;
        this.s3AsyncClient = s3AsyncClient;
        this.s3AsyncCrtClient = s3AsyncCrtClient;
        this.s3Presigner = s3Presigner;
        this.minioClient = minioClient;
    }

    @Value("${s3.bucket-name}")
    private String bucketName;

    private final String PREFIX = "lego/";

    public CompletableFuture<PutObjectResponse> putFlux(long length, String fileName, MediaType mediaType, Flux<ByteBuffer> body) {
        return s3AsyncClient
                .putObject(PutObjectRequest.builder()
                                .bucket(bucketName)
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
                                .bucket(bucketName)
                                .contentLength(length)
                                .key(PREFIX + fileName)
                                .contentType(mediaType.toString())
                                .metadata(new HashMap<>())
                                .tagging(tagging)
                                .build(),
                        AsyncRequestBody.fromPublisher(body));
    }


    public URL putObject(MultipartFile file) {
        String original = file.getOriginalFilename();
        try {
            PutObjectResponse response = s3Client.putObject(req -> {
                        req.bucket(bucketName)
                                .key(original);
                    },
                    RequestBody.fromBytes(file.getBytes()));

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(preReq -> {
                preReq.getObjectRequest(req -> {
                    req.bucket(bucketName).key(original);
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

    public InputStream getObject(String bucket, String key) {
        return s3Client.getObject(req -> req.bucket(bucket)
                        .key(key));
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


    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @SneakyThrows
    public boolean putObjectAsStream(MultipartFile file) {
        String original = file.getOriginalFilename();
        String contentType = "application/octet-stream";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(contentType)
                .key(original).build();
        s3Client.putObject(request,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return true;
    }
    @SneakyThrows
    public boolean putObjectAsStream(InputStream inputStream, long contentLength, String key) {
        String contentType = "application/octet-stream";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .contentType(contentType)
                .key(key).build();
//        s3Client.putObject(request, RequestBody.fromContentProvider(ContentStreamProvider.fromInputStream(
//                new BufferedInputStream(inputStream)), contentType));
        s3Client.putObject(request, RequestBody.fromContentProvider(new MyContentStreamProvider(inputStream), contentType));
        return true;
    }

    /**
     * Загрузка объекта в режиме потока при неизвестной длине пакета с помощью CRT клиента
     * @param inputStream
     * @param contentLength
     * @param key
     */
    @SneakyThrows
    public void putObjectFromStream(InputStream inputStream, long contentLength, String key) {
        BlockingInputStreamAsyncRequestBody body =
                AsyncRequestBody.forBlockingInputStream(null); // 'null' indicates a stream will be provided later.
        CompletableFuture<PutObjectResponse> responseFuture =
                s3AsyncCrtClient.putObject(r -> r.bucket(bucketName).key(key), body);

        // Provide the stream of data to be uploaded.
        body.writeInputStream(new BufferedInputStream(inputStream));

        responseFuture.join();
        inputStream.close();// Wait for the response.
    }



    @SneakyThrows
    public boolean putObjectAsStreamAsync(MultipartFile file) {
        String original = file.getOriginalFilename();
        String contentType = "application/octet-stream";
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(original).build();
        CompletableFuture<PutObjectResponse> future = s3AsyncClient.putObject(request,
                AsyncRequestBody.fromInputStream(file.getInputStream(), file.getSize(), executorService));

        future.thenAccept(responseFuture -> log.info("complete {}", file.getOriginalFilename()));
        return true;
    }
}
