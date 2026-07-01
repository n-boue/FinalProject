package tsu.finalproject.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@Slf4j
public class S3Config {

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                       .region(Region.of(region))
                       .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                       .endpointOverride(URI.create(endpoint))
                       .forcePathStyle(true)
                       .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        S3Configuration s3Configuration = S3Configuration.builder()
                                                  .pathStyleAccessEnabled(true)
                                                  .build();

        return S3Presigner.builder()
                       .region(Region.of(region))
                       .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                       .endpointOverride(URI.create(endpoint))
                       .serviceConfiguration(s3Configuration)
                       .build();
    }

    @Bean
    public CommandLineRunner initializeBucket(S3Client s3Client) {
        return args -> {
            try {
                s3Client.headBucket(HeadBucketRequest.builder()
                                            .bucket(bucketName)
                                            .build());
            } catch (S3Exception e) {
                if (e.statusCode() == 404) {
                    s3Client.createBucket(CreateBucketRequest.builder()
                                                  .bucket(bucketName)
                                                  .build());
                    log.info("S3/MinIO Bucket '{}' created successfully.", bucketName);
                } else {
                    log.error("Could not assert or create bucket: {}", e.getMessage(), e);
                }
            }
        };
    }
}