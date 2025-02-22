package ru.get.testclient;

import org.springframework.boot.autoconfigure.SpringBootApplication;

public class TestClientApp {
    public static void main(String[] args) {
        String serverUrl = "http://localhost:7220/data/upload/flux-test"; // URL вашего сервера
//        String serverUrl = "http://localhost:7220/data/info"; // URL вашего сервера
        String filePath = "testclient/s3info.txt"; // Путь к файлу для отправки
        TestClient client = new TestClient(serverUrl);
//        client.uploadFile(filePath)
//                .doOnNext(response -> System.out.println("Ответ от сервера: " + response))
//                .blockLast(); // Ожидаем завершения обработки всех данных
        client.uploadSync(filePath);
//        client.getInfo()
//                .doOnNext(System.out::println)
//                .blockFirst();

    }
}
