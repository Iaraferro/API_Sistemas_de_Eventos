package org.acme.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@ApplicationScoped
public class S3ClientProducer {

    @Produces
    public S3Client s3Client() {
        String endpoint = "http://localhost:9000";
        String accessKey = "admin";
        String secretKey = "password";
        String region = "us-east-1";

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .region(Region.of(region))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build()
                )
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
    }
}
