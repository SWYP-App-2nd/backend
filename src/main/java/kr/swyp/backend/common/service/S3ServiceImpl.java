package kr.swyp.backend.common.service;

import java.time.Duration;
import java.util.UUID;
import kr.swyp.backend.common.config.ObjectStorageProperties;
import kr.swyp.backend.common.domain.File;
import kr.swyp.backend.common.dto.FileDto.FileDeleteRequest;
import kr.swyp.backend.common.dto.FileDto.FileDownloadResponse;
import kr.swyp.backend.common.dto.FileDto.FileUploadRequest;
import kr.swyp.backend.common.dto.FileDto.FileUploadResponse;
import kr.swyp.backend.common.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final ObjectStorageProperties properties;
    private final FileRepository fileRepository;

    @Override
    @Transactional
    public FileUploadResponse generatePreSignedUrlForUpload(UUID memberId,
            FileUploadRequest request) {
        String generatedFileName = generateFileName(request.getCategory(), memberId,
                request.getFileName());

        fileRepository.save(File.builder()
                .memberId(memberId)
                .fileName(request.getFileName())
                .category(request.getCategory())
                .contentType(request.getContentType())
                .fileSize(request.getFileSize())
                .build());

        PutObjectRequest s3PutObjectRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(generatedFileName)
                .contentType(request.getContentType())
                .contentLength(request.getFileSize())
                .build();

        PutObjectPresignRequest preSignedPutObjectRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(s3PutObjectRequest)
                .build();

        PresignedPutObjectRequest preSignedPutObject = s3Presigner.presignPutObject(
                preSignedPutObjectRequest);

        return FileUploadResponse.builder()
                .preSignedUrl(preSignedPutObject.url().toString())
                .build();
    }

    @Override
    @Transactional
    public FileDownloadResponse generatePreSignedUrlForDownload(UUID memberId, String category,
            String objectKey) {
        String generatedFileName = generateFileName(category, memberId, objectKey);
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(generatedFileName)
                .build();

        // PreSigned 요청 생성
        GetObjectPresignRequest preSignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest)
                .build();

        // PreSigned URL 생성
        PresignedGetObjectRequest preSignedRequest = s3Presigner.presignGetObject(preSignRequest);

        return FileDownloadResponse.builder()
                .preSignedUrl(preSignedRequest.url().toString())
                .build();
    }

    @Override
    @Transactional
    public Boolean deleteFile(UUID memberId, FileDeleteRequest request) {
        try {
            File file = fileRepository.findByFileNameAndCategoryAndMemberId(request.getFileName(),
                            request.getCategory(), memberId)
                    .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));
            fileRepository.delete(file);

            String generatedFileName = generateFileName(request.getCategory(), memberId,
                    request.getFileName());

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(properties.getBucketName())
                    .key(generatedFileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            return true;
        } catch (S3Exception e) {
            return false;
        }
    }

    private String generateFileName(String category, UUID memberId, String fileName) {
        return String.format("%s/%s/%s", category, memberId, fileName);
    }
}
