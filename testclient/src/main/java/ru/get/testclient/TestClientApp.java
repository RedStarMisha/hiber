package ru.get.testclient;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestClientApp {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
//        ExecutorService executorService = Executors.newFixedThreadPool(3);
//        executorService.execute(() -> {
//            TestClient testClient = new TestClient("asd");
//            long fullRes = 0;
//
//            for (int i = 0; i < 50; i++) {
//                try {
//                    long start = System.currentTimeMillis();
//                    testClient.getFileResource();
//                    long finish = System.currentTimeMillis();
//                    long delta = finish - start;
//                    fullRes += delta;
//
//                } catch (URISyntaxException | IOException | InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            System.out.println("resource - " + fullRes / 50);
//
//        });
//        executorService.execute(() -> {
//            TestClient testClient = new TestClient("asd");
//            long fullRes = 0;
//            for (int i = 0; i < 50; i++) {
//                try {
//                    long start = System.currentTimeMillis();
//                    testClient.getFileDefault();
//                    long finish = System.currentTimeMillis();
//                    long delta = finish - start;
//                    fullRes += delta;
//
//                } catch (URISyntaxException | IOException | InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            System.out.println("default - " + fullRes / 50);
//
//        });
//        executorService.execute(() -> {
//            TestClient testClient = new TestClient("asd");
//            long fullRes = 0;
//            for (int i = 0; i < 50; i++) {
//                try {
//                    long start = System.currentTimeMillis();
//                    testClient.getFileStream();
//                    long finish = System.currentTimeMillis();
//                    long delta = finish - start;
//                    fullRes += delta;
//                } catch (URISyntaxException | IOException | InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            System.out.println("stream - " + fullRes / 50);
//        });

        Mono<UUID> mono = Mono.just(UUID.randomUUID());
        Flux<UUID> uuidFlux = Flux.generate(sink -> sink.next(UUID.randomUUID()));
//        Flux<UUID> uuidFlux = Flux.generate(sink -> sink.next(mono.block()));
        Mono<Long> delay = Mono.delay(Duration.ofSeconds(2));
        uuidFlux
                .map(UUID::toString)
                .doOnNext(System.out::println)
//                .repeat(5)
                .repeatWhen(completed ->
                        completed
                                .zipWith(delay)
                                .filter(tuple -> !tuple.getT1().toString().startsWith("1"))
                                .map(Tuple2::getT2)
                )
                .takeUntil(uuid -> uuid.startsWith("1"))
                .subscribe();

//        for (int i = 0; i < 50; i++) {
//            long start = System.currentTimeMillis();
//            testClient.getFileResource();
//            long finish = System.currentTimeMillis();
//            long delta = finish - start;
//            fullRes+=delta;
//        }
//        System.out.println("resource - " + fullRes / 50);
//        long fullDef = 0;
//        for (int i = 0; i < 50; i++) {
//            long start = System.currentTimeMillis();
//            testClient.getFileDefault();
//            long finish = System.currentTimeMillis();
//            long delta = finish - start;
//            fullDef+=delta;
//        }
//        System.out.println("default - " + fullDef / 50);

//        executorService.shutdown();
    }
}
