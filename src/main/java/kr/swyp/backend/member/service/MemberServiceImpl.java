package kr.swyp.backend.member.service;

import java.util.UUID;
import kr.swyp.backend.member.domain.Member;
import kr.swyp.backend.member.domain.MemberNotificationSetting;
import kr.swyp.backend.member.domain.MemberSocialLoginInfo;
import kr.swyp.backend.member.dto.MemberDto.MemberInfoResponse;
import kr.swyp.backend.member.repository.MemberNotificationSettingRepository;
import kr.swyp.backend.member.repository.MemberRepository;
import kr.swyp.backend.member.repository.MemberSocialLoginInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberSocialLoginInfoRepository socialLoginInfoRepository;
    private final MemberNotificationSettingRepository notificationSettingRepository;

    public MemberInfoResponse getMemberInfo(UUID memberId) {
        // Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원을 찾을 수 없습니다."));

        // MemberSocialLoginInfo 조회
        MemberSocialLoginInfo socialLoginInfo = socialLoginInfoRepository.findByMember(member)
                .orElseThrow(() -> new IllegalArgumentException("소셜 로그인 정보를 찾을 수 없습니다."));

        // MemberNotificationSetting 조회
        MemberNotificationSetting notificationSetting = notificationSettingRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("푸시 알림 설정 정보를 찾을 수 없습니다."));

        return MemberInfoResponse.fromEntity(member, socialLoginInfo, notificationSetting);
    }
}