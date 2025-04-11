package kr.swyp.backend.common.config;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final ObjectStorageProperties properties;

    public AwsCredentialsProvider awsCredentialsProvider() {
        AwsCredentials awsCredentials = AwsBasicCredentials.create(properties.getAccessKey(),
                properties.getSecretKey());
        return StaticCredentialsProvider.create(awsCredentials);
    }

    @Bean
    public S3Client amazonS3Client() {
        return S3Client.builder()
                .credentialsProvider(awsCredentialsProvider())
                .endpointOverride(URI.create(properties.getEndPoint()))
                .region(Region.of(properties.getRegionName()))
                .build();
    }

    @Bean
    public S3Presigner amazonS3Presigner() {
        return S3Presigner.builder()
                .credentialsProvider(awsCredentialsProvider())
                .endpointOverride(URI.create(properties.getEndPoint()))
                .region(Region.of(properties.getRegionName()))
                .build();
    }
}
