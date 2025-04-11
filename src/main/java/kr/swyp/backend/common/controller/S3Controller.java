package kr.swyp.backend.common.controller;

import jakarta.validation.Valid;
import kr.swyp.backend.common.dto.FileDto.FileDeleteRequest;
import kr.swyp.backend.common.dto.FileDto.FileDownloadResponse;
import kr.swyp.backend.common.dto.FileDto.FileUploadRequest;
import kr.swyp.backend.common.dto.FileDto.FileUploadResponse;
import kr.swyp.backend.common.service.S3Service;
import kr.swyp.backend.member.dto.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
@PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'SUPER_ADMIN')")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody FileUploadRequest request) {
        FileUploadResponse response = s3Service.generatePreSignedUrlForUpload(
                memberDetails.getMemberId(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<FileDownloadResponse> downloadFile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestParam String category,
            @RequestParam String objectKey) {
        FileDownloadResponse response = s3Service.generatePreSignedUrlForDownload(
                memberDetails.getMemberId(), category, objectKey);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteFile(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @Valid @RequestBody FileDeleteRequest request) {
        s3Service.deleteFile(memberDetails.getMemberId(), request);
        return ResponseEntity.noContent().build();
    }
}
