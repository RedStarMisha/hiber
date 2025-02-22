package ru.get.caffeinetest;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Weigher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

//@SpringBootApplication
public class CaffeineTestApp {
    public static void main(String[] args) {
//        SpringApplication.run(CaffeineTestApp.class, args);
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(300, TimeUnit.SECONDS)
                .maximumWeight(10)
                .weigher(Weigher.singletonWeigher())
                .build();
        cache.put("test", "value");
        Optional<String> result = Optional.ofNullable(cache.getIfPresent("test"));
        result.ifPresentOrElse(System.out::println, () -> System.out.println("not found"));
    }
}
