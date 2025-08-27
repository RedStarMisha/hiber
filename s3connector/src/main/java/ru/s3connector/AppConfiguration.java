package ru.s3connector;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

@Configuration
public class AppConfiguration {
    @Bean
    public S3Client createS3Client(@Value("${s3.user}") String user,
                                   @Value("${s3.password}") String password,
                                   @Value("${s3.region}") String region,
                                   @Value("${s3.endpoint}") String endpoint) {
        AwsCredentials credentials = AwsBasicCredentials.create(user, password);
        return S3Client
                .builder()
                .endpointOverride(URI.create("http://" + endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.AP_EAST_1)
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080").build();
    }

    @Bean
    public RestClient restClient() {
        return RestClient.create("http://localhost:8080");
    }

    @Bean
    @Qualifier("s3AsyncClient")
    @Primary
    public S3AsyncClient s3Asyncclient(@Value("${s3.user}") String user,
                                  @Value("${s3.password}") String password,
                                  @Value("${s3.region}") String region,
                                  @Value("${s3.endpoint}") String endpoint) {
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .writeTimeout(Duration.ZERO)
                .maxConcurrency(64)
                .build();
        S3Configuration serviceConfiguration = S3Configuration.builder()
                .chunkedEncodingEnabled(true)
                .build();
        S3AsyncClientBuilder b = S3AsyncClient.builder()
                .httpClient(httpClient)
                .endpointOverride(URI.create("http://" + endpoint))
                .region(Region.AP_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create(user, password))
                .serviceConfiguration(serviceConfiguration);
        return b.build();
    }

    /**
     * Специальный высокопроизводительный клиент CRT. Используется для компонентной загрузки (в частности
     * для загрузки файла без указания длины пакета). Ссылка на доку
     * <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/crt-based-s3-client.html">...</a>
     * @param user
     * @param password
     * @param region
     * @param endpoint
     * @return
     */
    @Bean
    @Qualifier("s3AsyncCrtClient")
    public S3AsyncClient s3AsyncCrtClient(@Value("${s3.user}") String user,
                                  @Value("${s3.password}") String password,
                                  @Value("${s3.region}") String region,
                                  @Value("${s3.endpoint}") String endpoint) {
        return S3AsyncClient.crtBuilder()
                .endpointOverride(URI.create("http://" + endpoint))
                .region(Region.AP_EAST_1)
                .credentialsProvider(() -> AwsBasicCredentials.create(user, password))
                .targetThroughputInGbps(20.0)
                .minimumPartSizeInBytes(8 * 1025 * 1024L)
                .build();
    }



    @Bean
    public S3Presigner createS3Presigner(@Value("${s3.user}") String user,
                                         @Value("${s3.password}") String password,
                                         @Value("${s3.region}") String region,
                                         @Value("${s3.endpoint}") String endpoint) {
        AwsCredentials credentials = AwsBasicCredentials.create(user, password);
        return S3Presigner.builder()
                .endpointOverride(URI.create("http://" + endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.AP_EAST_1) // this is not used, but the AWS SDK requires it
                .build();
    }

    @Bean
    public MinioClient minioClient(@Value("${s3.user}") String user,
                                   @Value("${s3.password}") String password,
                                   @Value("${s3.endpoint}") String endpoint) {
        return MinioClient.builder()
                .endpoint("http://" + endpoint)
                .credentials(user, password)
                .build();
    }
}
