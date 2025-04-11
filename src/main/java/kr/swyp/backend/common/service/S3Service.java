package kr.swyp.backend.common.service;

import java.util.UUID;
import kr.swyp.backend.common.dto.FileDto.FileDeleteRequest;
import kr.swyp.backend.common.dto.FileDto.FileDownloadResponse;
import kr.swyp.backend.common.dto.FileDto.FileUploadRequest;
import kr.swyp.backend.common.dto.FileDto.FileUploadResponse;

public interface S3Service {

    /**
     * 객체 업로드를 위한 PreSignedUrl을 생성한다.
     *
     * @param request 업로드 요청 정보
     * @return 생성된 PreSignedUrl
     */
    FileUploadResponse generatePreSignedUrlForUpload(UUID memberId, FileUploadRequest request);

    /**
     * 객체 다운로드를 위한 PreSignedUrl을 생성한다..
     *
     * @param objectKey 다운로드할 객체의 키(경로와 파일명)
     * @return 생성된 PreSignedUrl
     */
    FileDownloadResponse generatePreSignedUrlForDownload(UUID memberId, String category,
            String objectKey);

    /**
     * S3에서 파일을 삭제한다..
     *
     * @param request 삭제 요청 정보
     * @return 삭제 성공 여부
     */
    Boolean deleteFile(UUID memberId, FileDeleteRequest request);
}

