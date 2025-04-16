package kr.swyp.backend.member.service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kr.swyp.backend.authentication.repository.RefreshTokenRepository;
import kr.swyp.backend.friend.repository.FriendRepository;
import kr.swyp.backend.member.domain.Member;
import kr.swyp.backend.member.domain.MemberCheckRate;
import kr.swyp.backend.member.domain.MemberSocialLoginInfo;
import kr.swyp.backend.member.domain.MemberWithdrawalLog;
import kr.swyp.backend.member.dto.MemberDto.CheckRateResponse;
import kr.swyp.backend.member.dto.MemberDto.MemberInfoResponse;
import kr.swyp.backend.member.dto.MemberDto.MemberWithdrawRequest;
import kr.swyp.backend.member.repository.MemberCheckRateRepository;
import kr.swyp.backend.member.repository.MemberNotificationSettingRepository;
import kr.swyp.backend.member.repository.MemberRepository;
import kr.swyp.backend.member.repository.MemberSocialLoginInfoRepository;
import kr.swyp.backend.member.repository.MemberWithdrawalLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberSocialLoginInfoRepository socialLoginInfoRepository;
    private final MemberNotificationSettingRepository notificationSettingRepository;
    private final MemberWithdrawalLogRepository memberWithdrawalLogRepository;
    private final MemberCheckRateRepository memberCheckRateRepository;
    private final FriendRepository friendRepository;

    @Transactional(readOnly = true)
    public MemberInfoResponse getMemberInfo(UUID memberId) {
        // Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다."));

        // MemberSocialLoginInfo 조회
        MemberSocialLoginInfo socialLoginInfo = socialLoginInfoRepository.findByMember(member)
                .orElseThrow(() -> new NoSuchElementException("소셜 로그인 정보를 찾을 수 없습니다."));

        return MemberInfoResponse.fromEntity(member, socialLoginInfo);
    }

    @Override
    @Transactional
    public void withdrawMember(UUID memberId, MemberWithdrawRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다."));

        // 탈퇴 처리
        member.updateWithdrawnAt();

        // Role 삭제

        member.getRoles().clear();
        memberRepository.save(member);

        // 탈퇴 로그 저장
        memberWithdrawalLogRepository.save(
                MemberWithdrawalLog.builder()
                        .memberId(member.getMemberId())
                        .reasonType(request.getReasonType())
                        .customReason(request.getCustomReason())
                        .build()
        );

        // Refresh Token 삭제
        refreshTokenRepository.deleteAllByMemberId(member.getMemberId());

        // 소셜 로그인 정보 삭제
        socialLoginInfoRepository.deleteByMember(member);

        // 푸시 알림 설정 삭제
        notificationSettingRepository.deleteByMemberId(member.getMemberId());
    }

    @Override
    @Transactional
    public CheckRateResponse saveMemberCheckRate(UUID memberId) {
        // Member 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("해당 회원을 찾을 수 없습니다."));

        // 체크율 계산
        int result = Optional.ofNullable(
                        friendRepository.findAverageCheckRateByMemberId(memberId))
                .map(Double::intValue)
                .orElse(0);

        // MemberCheckRate 저장 또는 업데이트
        memberCheckRateRepository.findByMember(member)
                .ifPresentOrElse(
                        existing -> existing.updateCheckRate(result),
                        () -> {
                            MemberCheckRate newCheckRate = MemberCheckRate.builder()
                                    .member(member)
                                    .checkRate(result)
                                    .build();
                            memberCheckRateRepository.save(newCheckRate);
                        });

        // 결과 반환
        return CheckRateResponse.fromEntity(result);
    }

}