package ru.s3connector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.s3connector.UploadResult;
import ru.s3connector.UploadState;
import ru.s3connector.client.S3Connector;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveS3Service {

    private final S3Connector s3Connector;

    private final S3AsyncClient s3AsyncClient;


    private final int BUFFER_SIZE = 5 * 1024 * 1024;

    public Mono<UploadResult> putObject(HttpHeaders headers, Flux<ByteBuffer> data, String name) {
        MediaType mediaType = headers.getContentType();
        long length = headers.getContentLength();

        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        return Mono.fromFuture(s3Connector.putFlux(length, name, mediaType, data))
                .map(response -> new UploadResult(HttpStatus.CREATED, List.of("asd")));
    }



    public Mono<String> saveFile(HttpHeaders headers, FilePart part) {
        log.info(part.filename());
        Map<String, String> metadata = new HashMap<String, String>();
        String filename = part.filename();
        metadata.put("filename", filename);
        MediaType mt = part.headers().getContentType();
        if (mt == null) {
            mt = MediaType.APPLICATION_OCTET_STREAM;
        }
        String bucket = "test-bucket";
        UploadState uploadState = new UploadState(bucket, part.filename());
        CompletableFuture<CreateMultipartUploadResponse> uploadRequest = s3AsyncClient
                .createMultipartUpload(CreateMultipartUploadRequest.builder()
                        .contentType(mt.toString())
                        .key(part.filename())
                        .metadata(metadata)
                        .bucket(bucket)
                        .build());

        return Mono
                .fromFuture(uploadRequest)
                .flatMapMany((response) -> {
                    uploadState.uploadId = response.uploadId();
                    return part.content();
                })
                .bufferUntil((buffer) -> {
                    uploadState.buffered += buffer.readableByteCount();
                    if (uploadState.buffered >= BUFFER_SIZE) {
                        uploadState.buffered = 0;
                        return true;
                    } else {
                        return false;
                    }
                })
                .map(this::concatBuffers)
                .flatMap((buffer) -> uploadPart(uploadState, buffer))
                .reduce(uploadState, (state, completedPart) -> {
                    state.completedParts.put(completedPart.partNumber(), completedPart);
                    return state;
                })
                .flatMap(this::completeUpload)
                .map((response) -> {
                    return uploadState.filekey;
                });
    }

    private ByteBuffer concatBuffers(List<DataBuffer> buffers) {
        log.info("[I198] creating BytBuffer from {} chunks", buffers.size());

        int partSize = 0;
        for (DataBuffer b : buffers) {
            partSize += b.readableByteCount();
        }

        ByteBuffer partData = ByteBuffer.allocate(partSize);
        buffers.forEach((buffer) -> {
            partData.put(buffer.asByteBuffer());
        });

        // Reset read pointer to first byte
        partData.rewind();

        log.info("[I208] partData: size={}", partData.capacity());
        return partData;

    }

    private Mono<CompleteMultipartUploadResponse> completeUpload(UploadState state) {
        log.info("[I202] completeUpload: bucket={}, filekey={}, completedParts.size={}", state.bucket, state.filekey, state.completedParts.size());

        CompletedMultipartUpload multipartUpload = CompletedMultipartUpload.builder()
                .parts(state.completedParts.values())
                .build();

        return Mono.fromFuture(s3AsyncClient.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
                .bucket(state.bucket)
                .uploadId(state.uploadId)
                .multipartUpload(multipartUpload)
                .key(state.filekey)
                .build()));
    }

    private Mono<CompletedPart> uploadPart(UploadState uploadState, ByteBuffer buffer) {
        final int partNumber = ++uploadState.partCounter;
        log.info("[I218] uploadPart: partNumber={}, contentLength={}", partNumber, buffer.capacity());

        CompletableFuture<UploadPartResponse> request = s3AsyncClient
                .uploadPart(UploadPartRequest.builder()
                                .bucket(uploadState.bucket)
                                .key(uploadState.filekey)
                                .partNumber(partNumber)
                                .uploadId(uploadState.uploadId)
                                .contentLength((long) buffer.capacity())
                                .build(),
                        AsyncRequestBody.fromPublisher(Mono.just(buffer)));

        return Mono
                .fromFuture(request)
                .map((uploadPartResult) -> {
                    log.info("[I230] uploadPart complete: part={}, etag={}", partNumber, uploadPartResult.eTag());
                    return CompletedPart.builder()
                            .eTag(uploadPartResult.eTag())
                            .partNumber(partNumber)
                            .build();
                });
    }
}
