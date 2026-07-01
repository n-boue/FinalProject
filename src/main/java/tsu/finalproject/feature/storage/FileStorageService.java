package tsu.finalproject.feature.storage;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @NonNull
    public String uploadFile(@NonNull MultipartFile file, @NonNull String folderPrefix) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                                       ? originalFilename.substring(originalFilename.lastIndexOf("."))
                                       : "";
            String uniqueKey = folderPrefix + "/" + UUID.randomUUID() + extension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                        .bucket(bucketName)
                                                        .key(uniqueKey)
                                                        .contentType(file.getContentType())
                                                        .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            return uniqueKey;

        } catch (IOException e) {
            throw new RuntimeException("Failed to read the uploaded file", e);
        }
    }

    @NonNull
    public String getFileUrl(@NonNull String objectKey) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                                                    .bucket(bucketName)
                                                    .key(objectKey)
                                                    .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                                                         .signatureDuration(Duration.ofMinutes(60))
                                                         .getObjectRequest(getObjectRequest)
                                                         .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    public void deleteFile(@NonNull String objectKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                                                              .bucket(bucketName)
                                                              .key(objectKey)
                                                              .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to delete file from storage: " + objectKey, e);
        }
    }
}