package ru.get;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewaySpringApp {
    public static void main(String[] args) {
        SpringApplication.run(GatewaySpringApp.class, args);
    }
}
