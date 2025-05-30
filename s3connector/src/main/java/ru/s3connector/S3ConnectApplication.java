package ru.s3connector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class S3ConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(S3ConnectApplication.class, args);
    }
}
