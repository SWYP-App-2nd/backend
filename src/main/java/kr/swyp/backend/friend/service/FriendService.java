package kr.swyp.backend.friend.service;

import java.util.UUID;
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
}
