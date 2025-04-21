package kr.swyp.backend.friend.service;

import java.util.List;
import java.util.UUID;
import kr.swyp.backend.friend.dto.FriendDto.FriendCheckLogResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendCreateListResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendDetailResponse;
import kr.swyp.backend.friend.dto.FriendDto.FriendDetailUpdateRequest;
import kr.swyp.backend.friend.dto.FriendDto.FriendListResponse;

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

    /**
     * 친구 체크율을 기록하고 업데이트합니다.
     *
     * @param memberId 회원 ID
     * @param friendId 친구 ID
     */
    void recordCheckAndUpdateRate(UUID memberId, UUID friendId);

    /**
     * 친구 목록을 가져옵니다.
     *
     * @param memberId 회원 ID
     * @return 친구 목록 응답 리스트
     */
    List<FriendListResponse> getFriendList(UUID memberId);

    /**
     * 친구의 position을 업데이트합니다.
     *
     * @param memberId    회원 ID
     * @param friendId    친구 ID
     * @param newPosition 새로운 위치
     */
    void updateFriendPosition(UUID memberId, UUID friendId, int newPosition);

    /**
     * 친구 상세 정보를 가져옵니다.
     *
     * @param memberId 회원 ID
     * @param friendId 친구 ID
     * @return 친구 상세 정보 응답
     */
    FriendDetailResponse getFriendDetail(UUID memberId, UUID friendId);

    /**
     * 친구를 업데이트 합니다.
     *
     * @param memberId 회원 ID
     * @param friendId 친구 ID
     * @param request  친구 상세 업데이트 요청
     */
    FriendDetailResponse updateFriend(UUID memberId, UUID friendId,
            FriendDetailUpdateRequest request);

    /**
     * 친구를 삭제합니다.
     *
     * @param memberId 회원 ID
     * @param friendId 친구 ID
     */
    void deleteFriend(UUID memberId, UUID friendId);
}
