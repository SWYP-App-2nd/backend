package kr.swyp.backend.friend.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import kr.swyp.backend.friend.dto.FriendDto.FriendCheckLogResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendListResponse;
import kr.swyp.backend.friend.service.FriendService;
import kr.swyp.backend.member.dto.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('USER', 'ADMIN', 'SUPER_ADMIN')")
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/init")
    public ResponseEntity<FriendCreateListResponse> init(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @RequestBody FriendCreateListRequest request) {
        return ResponseEntity.ok(friendService.init(memberDetails.getMemberId(), request));
    }

    @PostMapping("/record/{friendId}")
    public ResponseEntity<Map<String, String>> recordFriendCheck(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("friendId") UUID friendId) {

        // 친구별 체크 로그 생성 및 친구별 체크율 업데이트
        friendService.recordCheckAndUpdateRate(memberDetails.getMemberId(), friendId);

        return ResponseEntity.ok(Map.of("message", "챙기기 버튼 반영이 완료되었습니다."));
    }

    @GetMapping("/record/{friendId}")
    public ResponseEntity<List<FriendCheckLogResponse>> getFriendCheckLogs(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable("friendId") UUID friendId) {
        List<FriendCheckLogResponse> response = friendService.getCheckLogs(
                memberDetails.getMemberId(), friendId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reminder/{friendId}")
    public ResponseEntity<Map<String, String>> triggeredAlarmCheck(
            @AuthenticationPrincipal MemberDetails memberDetails,
            @PathVariable(value = "friendId") UUID friendId) {

        friendService.updateAlarmCheck(memberDetails.getMemberId(), friendId);
        return ResponseEntity.ok(Map.of("message", "트리거된 알람 개수 반영이 완료되었습니다."));
    }

    @GetMapping("/list")
    public ResponseEntity<List<FriendListResponse>> getFriendList(
            @AuthenticationPrincipal MemberDetails memberDetails) {
        UUID memberId = memberDetails.getMemberId();
        List<FriendListResponse> friendList = friendService.getFriendList(memberId);
        return ResponseEntity.ok(friendList);
    }
}
