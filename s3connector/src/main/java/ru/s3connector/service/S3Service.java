package ru.s3connector.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.s3connector.UploadResult;
import ru.s3connector.UploadState;
import ru.s3connector.client.S3Connector;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.ResponsePublisher;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Connector s3Connector;


    public List<URL> putObjects(MultipartFile[] files) {
        long start = System.currentTimeMillis();
        List<URL> list = Arrays.stream(files)
                .map(s3Connector::putObject)
                .toList();
        long end = System.currentTimeMillis();
        log.info("single path - " + (end - start));
        return list;
    }

    public void putObjectsAsStream(MultipartFile[] files) {
        for (MultipartFile file : files) {
            s3Connector.putObjectAsStream(file);
        }
    }

    public void putObject(InputStream inputStream, long contentLength, String key) {
        s3Connector.putObjectFromStream(inputStream, contentLength, key);
    }

    public Resource getObject(String bucket, String key) {
        return new InputStreamResource(s3Connector.getObject(bucket, key));
    }

    public CompletableFuture<PutObjectResponse> uploadObjectsWithTags(HttpHeaders headers, Flux<ByteBuffer> data, String name) {
        MediaType mediaType = headers.getContentType();
        long length = headers.getContentLength();

        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return s3Connector.putFluxWithTags(length, name, mediaType, data);
    }

    public CompletableFuture<PutObjectResponse> uploadObjectsFluxTest(HttpHeaders headers, Flux<ByteBuffer> data, String name) {
        MediaType mediaType = headers.getContentType();
        long length = headers.getContentLength();

        if (mediaType == null) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        return s3Connector.putFlux(length, name, mediaType, data);
    }



    public List<URL> uploadObjectsMultipath(MultipartFile[] files) {
        long start = System.currentTimeMillis();

        List<URL> list = List.of(s3Connector.uploadObjectsMultipath(files[0]));
        long end = System.currentTimeMillis();
        log.info("multi path - " + (end - start));
        return list;
    }







    public Mono<ResponsePublisher<GetObjectResponse>> getDataReactive(String name) {
        return s3Connector.getFileAsinc(name);
    }
}
