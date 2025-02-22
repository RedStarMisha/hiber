package ru.s3connector.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.s3connector.UploadResult;
import ru.s3connector.service.S3Service;
import ru.s3connector.client.S3Connector;

import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/data")
@RequiredArgsConstructor
@Slf4j
public class DataController {

    private final S3Connector s3Connector;
    private final S3Service s3Service;
    @PostMapping("upload")
    public List<URL> uploadData(@RequestParam MultipartFile[] files) {
        return s3Service.uploadObjects(files);
    }

    @PostMapping(value = "upload/flux-single", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<UploadResult>> uploadHandler(@RequestHeader HttpHeaders headers,
                                                            @RequestBody Flux<ByteBuffer> body,
                                                            @RequestParam String name) {
        Mono<ResponseEntity<UploadResult>> responseEntityMono
                = Mono.fromFuture(s3Service.uploadObjectsFluxTest(headers, body, name))
                .map((response) -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new UploadResult(HttpStatus.CREATED, new String[] {"asd"})));
        long end = System.currentTimeMillis();
        return responseEntityMono;
    }
    @PostMapping(value = "upload/flux-single/tags", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<ResponseEntity<UploadResult>> uploadFluxDataWithTags(@RequestHeader HttpHeaders headers,
                                                            @RequestBody Flux<ByteBuffer> body,
                                                            @RequestParam String name) {
        Mono<ResponseEntity<UploadResult>> responseEntityMono
                = Mono.fromFuture(s3Service.uploadObjectsWithTags(headers, body, name))
                .map((response) -> ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(new UploadResult(HttpStatus.CREATED, new String[] {"asd"})));
        long end = System.currentTimeMillis();
        return responseEntityMono;
    }
    @PostMapping(value = "upload-multipath", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<UploadResult>> uploadDataMultipath(@RequestHeader HttpHeaders headers, @RequestBody Flux<Part> parts) {
        return parts
                .ofType(FilePart.class)
                .flatMap((part) -> s3Service.saveFile(headers, part))
                .collect(Collectors.toList())
                .map((keys) -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new UploadResult(HttpStatus.CREATED,keys)));
    }
    @GetMapping("download")
    public byte[] getData(@RequestParam String fileName) {
        return s3Connector.getFile(fileName);
    }
    @GetMapping("download/tags")
    public void getTags(@RequestParam String fileName) {
        s3Connector.getTags(fileName);
    }

    @GetMapping("reactive")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public Mono<ResponseEntity<Flux<ByteBuffer>>> getDataMono(@RequestParam String name) {
        return s3Service.getDataReactive(name)
                .map(response -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, response.response().contentType())
                        .header(HttpHeaders.CONTENT_LENGTH, Long.toString(response.response().contentLength()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=video.avi")
                        .body(Flux.from(response)));
    }

    @GetMapping("data-list")
    public void getList() {
        s3Connector.getFileList();
    }

    @PostMapping("/upload-files")
    void uploadFileWithoutEntity(@RequestPart("files") Flux<PartEvent> filePartFlux) {
        filePartFlux.doOnNext(filePart -> System.out.println(filePart.name()));
//        return filePartFlux.flatMap(file -> file.transferTo(Paths.get(file.filename())))
//                .then(Mono.just("OK"))
//                .onErrorResume(error -> Mono.just("Error uploading files"));
    }

    @GetMapping("/info")
    public String getInfo() {
        return "hello";
    }
}
