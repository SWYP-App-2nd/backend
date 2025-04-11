package kr.swyp.backend.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import kr.swyp.backend.common.domain.File;
import kr.swyp.backend.common.dto.FileDto.FileDeleteRequest;
import kr.swyp.backend.common.dto.FileDto.FileDownloadResponse;
import kr.swyp.backend.common.dto.FileDto.FileUploadRequest;
import kr.swyp.backend.common.dto.FileDto.FileUploadResponse;
import kr.swyp.backend.common.repository.FileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class S3ServiceImplTest {

    private final String BUCKET_NAME = "test-bucket";

    @Autowired
    private S3ServiceImpl s3Service;

    @Autowired
    private FileRepository fileRepository;

    @MockitoBean
    private S3Client s3Client;

    @Test
    @DisplayName("파일 업로드 URL을 생성 할 수 있어야 한다.")
    void 파일_업로드_URL을_생성_할_수_있어야_한다() {
        // given
        UUID memberId = UUID.randomUUID();
        FileUploadRequest request = FileUploadRequest.builder()
                .fileName("test.txt")
                .category("test")
                .contentType("text/plain")
                .fileSize(100L)
                .build();

        // when
        FileUploadResponse response = s3Service.generatePreSignedUrlForUpload(memberId, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getPreSignedUrl()).isNotEmpty();
        assertThat(response.getPreSignedUrl()).contains(BUCKET_NAME);
        assertThat(response.getPreSignedUrl()).contains("test.txt");
    }

    @Test
    @DisplayName("파일 다운로드 URL을 생성 할 수 있어야 한다.")
    void 파일_다운로드_URL을_생성_할_수_있어야_한다() {
        // Given
        UUID memberId = UUID.randomUUID();
        String category = "test";
        String fileName = "test.txt";

        // When
        FileDownloadResponse response = s3Service.generatePreSignedUrlForDownload(memberId,
                category, fileName);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPreSignedUrl()).isNotNull();
        assertThat(response.getPreSignedUrl()).contains(BUCKET_NAME);
        assertThat(response.getPreSignedUrl())
                .contains(String.format("%s/%s/%s", category, memberId, fileName));
    }

    @Test
    @DisplayName("파일을 삭제 할 수 있어야 한다.")
    void 파일을_삭제_할_수_있어야_한다() {
        // Given
        UUID memberId = UUID.randomUUID();
        FileDeleteRequest request = FileDeleteRequest.builder()
                .fileName("test.txt")
                .category("test")
                .build();

        File file = File.builder()
                .memberId(memberId)
                .fileName(request.getFileName())
                .category(request.getCategory())
                .contentType("text/plain")
                .fileSize(100L)
                .build();
        fileRepository.save(file);

        when(s3Client.deleteObject(any(DeleteObjectRequest.class))).thenReturn(
                any(DeleteObjectResponse.class));

        // When
        Boolean result = s3Service.deleteFile(memberId, request);

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("파일을 삭제할 시에 파일이 없는 경우 예외를 발생시켜야 한다.")
    void 파일을_삭제할_시_파일이_없는_경우_예외를_발생시켜야_한다() {
        // Given
        UUID memberId = UUID.randomUUID();
        FileDeleteRequest request = FileDeleteRequest.builder()
                .fileName("non-existent.txt")
                .category("test")
                .build();

        Throwable throwable = catchThrowable(
                () -> s3Service.deleteFile(memberId, request));

        // When & Then
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("파일을 찾을 수 없습니다.");
    }
}