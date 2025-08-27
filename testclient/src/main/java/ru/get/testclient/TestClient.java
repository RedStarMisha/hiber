package ru.get.testclient;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Files;

@Component
public class TestClient {
    private final WebClient webClient;
    private final HttpClient httpClient;

    public TestClient(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
        httpClient = HttpClient.newBuilder()
                .build();
    }

    public void getFileDefault() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/file/default?file=22.exe"))
                .GET()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }
    public void getFileResource() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/file/resource?file=22.exe"))
                .GET()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }
    public void getFileStream() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/file/stream?file=22.exe"))
                .GET()
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    public void uploadSync(String filePath) {
        File file = new File(filePath);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:7220/data/upload/flux-single"))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(Files.readAllBytes(file.toPath())))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException e) {
            System.out.println("Error");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Flux<String> uploadFile(String filePath) {
        File file = new File(filePath);
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        long contentLength;

        // Читаем файл в массив байтов
        byte[] fileBytes;
        try {
            fileBytes = Files.readAllBytes(file.toPath());
            contentLength = Files.size(file.toPath());
        } catch (Exception e) {
            return Flux.error(e);
        }

        // Создаем Flux<DataBuffer> из массива байтов
        Flux<ByteBuffer> byteBufferFlux = Flux.create(sink -> {
            for (byte b : fileBytes) {
                sink.next(ByteBuffer.wrap(new byte[]{b}));
            }
            sink.complete();
        });

        // Отправляем файл на сервер
        return webClient.post()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Указываем тип содержимого
                .body(BodyInserters.fromDataBuffers(byteBufferFlux.map(dataBufferFactory::wrap))) // Вставляем Flux<DataBuffer> в тело запроса
                .header("Content-Length", String.valueOf(contentLength)) // Установка Content-Length
                .retrieve() // Получаем ответ от сервера
                .bodyToFlux(String.class); // Ожидаем строки в ответе
    }

    public Flux<String> getInfo() {
        return webClient.get()
                .retrieve() // Получаем ответ от сервера
                .bodyToFlux(String.class); // Ожидаем строки в ответе
    }
}
