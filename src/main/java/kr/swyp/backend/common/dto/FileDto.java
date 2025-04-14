package kr.swyp.backend.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FileDto {

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileUploadRequest {

        @NotNull(message = "파일을 입력해 주세요.")
        private String fileName;

        @NotNull(message = "Content Type을 입력해 주세요.")
        private String contentType;

        @NotNull(message = "파일 사이즈를 입력해 주세요.")
        @Positive(message = "파일 사이즈는 양수여야 합니다.")
        private Long fileSize;

        @NotNull(message = "파일 카테고리를 입력해 주세요.")
        private String category;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileUploadResponse {

        private String preSignedUrl;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileDownloadResponse {

        private String preSignedUrl;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileDeleteRequest {

        @NotNull(message = "삭제할 파일명은 필수 값 입니다.")
        private String fileName;

        @NotNull(message = "삭제할 파일 카테고리는 필수 값 입니다.")
        private String category;
    }
}
