package ru.s3connector.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import ru.s3connector.client.MinioConnector;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("minio/objects")
public class MinioController {
    private final MinioConnector minioConnector;

    @PutMapping
    public void uploadObject(HttpServletRequest request) throws IOException {
        log.info(String.valueOf(request.getContentLength()));
        minioConnector.putObject(null, request.getInputStream(), -1L);
    }
    @GetMapping("{bucket}/{key}")
    public Resource getObject(@PathVariable String bucket, @PathVariable String key) {
        return minioConnector.getObject(bucket, key);
    }

    @PostMapping("upload/another-service")
    public boolean uploadDataFromAnotherService() {
        return minioConnector.uploadFileFromAnotherService();
    }

}
