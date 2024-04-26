package com.koreanguide.koreanguidebackend.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String endPoint;

    @Autowired
    public S3Config(@Value("${cloud.aws.credentials.access-key}") String accessKey,
                    @Value("${cloud.aws.credentials.secret-key}") String secretKey,
                    @Value("${cloud.aws.region.static}") String region,
                    @Value("${cloud.aws.s3.endpoint}") String endPoint) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.endPoint = endPoint;
    }

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        return (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, region))
                .build();
    }
}
