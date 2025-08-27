package ru.s3connector.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.HttpClients;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ru.s3connector.model.FileWrapper;
import ru.s3connector.model.RequestToServiceException;
import ru.s3connector.model.ResponseFromServiceException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileClient {
    private final WebClient webClient;

    private final RestClient restClient;

//    public FileWrapper getResultAsStream() {
//        FileWrapper fileWrapper = new FileWrapper();
//        Flux<ByteBuffer> dataBufferFlux =  webClient.get()
//                .uri(uriBuilder -> uriBuilder.path("file/resource")
//                        .queryParam("file", "22.exe")
//                        .build())
//                .exchangeToFlux(clientResponse -> {
//                    fileWrapper.setContentLength(clientResponse.headers().contentLength());
//                    return clientResponse.body(BodyExtractors.toDataBuffers());
//                })
//                .flatMapSequential(dataBuffer -> Flux.fromIterable(dataBuffer::readableByteBuffers));
//        fileWrapper.setDataBufferFlux(dataBufferFlux);
//        return fileWrapper;
//    }
    public HttpResponse<InputStream> getResultAsStream() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/file/resource?file=22.exe"))
                .version(HttpClient.Version.HTTP_2)
                .GET()
                .build();
        return checkResponse(HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream()));
    }
    public MultipartFile getResultAsResource() throws URISyntaxException, IOException, InterruptedException {
        return restClient
                .get()
                .uri("/file/resource?file=22.exe")
                .retrieve()
                .body(MultipartFile.class);
    }

    public Flux<DataBuffer> getData() {
        return webClient.get()
                .uri("/file/resource?file=video3.avi")
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .retrieve()
                .bodyToFlux(DataBuffer.class); // the magic happens here
    }

//    public void sendFile() {
//        webClient.post()
//                .uri("asd")
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(BodyInserters.fromResource(new InputStreamResource(inputStream)));
//    }

    protected HttpResponse<InputStream> checkResponse(HttpResponse<InputStream> response) {
        int code = response.statusCode();
        URI uri = response.uri();
        if (code >= 500) {
            try (InputStream inputStream = response.body()) {
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                log.error(body);
                throw new ResponseFromServiceException(String.format("Code %d on %s. Body ---  %s", code, uri.getPath(), body));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (code >= 400) {
            try (InputStream inputStream = response.body()) {
                if (inputStream != null) {
                    String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    log.error(body);
                    throw new RequestToServiceException(String.format("Code %d on %s. Body ---  %s", code, uri.getPath(), body));
                } else {
                    throw new RequestToServiceException(String.format("Code %d. Incorrect request with no body on the path --- %s",
                            code, uri.getPath()));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return response;
    }
}
