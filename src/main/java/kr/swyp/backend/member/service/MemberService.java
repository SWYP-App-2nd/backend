package kr.swyp.backend.member.service;

import java.util.UUID;
import kr.swyp.backend.member.dto.MemberDto.MemberInfoResponse;

public interface MemberService {

    MemberInfoResponse getMemberInfo(UUID memberId);
}
