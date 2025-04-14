package kr.swyp.backend.friend.service;

import java.util.List;
import java.util.UUID;
import kr.swyp.backend.friend.dto.FriendDto.FriendCheckLogResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse;

public interface FriendService {

    /**
     * 친구 목록을 생성합니다.
     *
     * @param memberId 친구 목록을 생성할 회원 ID
     * @param request  친구 목록 생성 요청
     * @return 생성된 친구 목록 응답
     */
    FriendCreateListResponse init(UUID memberId, FriendCreateListRequest request);

    /**
     * 친구 체크율을 저장합니다.
     *
     * @param friendId 친구 ID
     * @return 저장된 체크율
     */
    int saveFriendCheckRate(UUID friendId);

    /**
     * 친구 챙기기 버튼 클릭을 기록합니다.
     *
     * @param friendId 친구 ID
     */
    void recordCheckLog(UUID memberId, UUID friendId);

    /**
     * 친구 챙김 로그를 가져옵니다.
     *
     * @param memberId 회원 ID
     * @param friendId 친구 ID
     * @return 친구 챙김 로그 응답 리스트
     */
    List<FriendCheckLogResponse> getCheckLogs(UUID memberId, UUID friendId);

    /**
     * 트리거된 친구 알람 체크를 업데이트합니다.
     *
     * @param memberId 회원 ID
     * @param friendId 친구 ID
     */
    void updateAlarmCheck(UUID memberId, UUID friendId);
}
