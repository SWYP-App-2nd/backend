package kr.swyp.backend.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import java.io.IOException;
import java.util.Optional;
import kr.swyp.backend.authentication.dto.MemberInfo;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleTokenVerificationResult;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleUserInfo;
import kr.swyp.backend.authentication.dto.SocialLoginDto.KakaoSocialLoginResponse;
import kr.swyp.backend.authentication.utils.AppleLoginUtil;
import kr.swyp.backend.member.domain.Member;
import kr.swyp.backend.member.domain.MemberSocialLoginInfo;
import kr.swyp.backend.member.domain.Role;
import kr.swyp.backend.member.enums.RoleType;
import kr.swyp.backend.member.enums.SocialLoginProviderType;
import kr.swyp.backend.member.repository.MemberRepository;
import kr.swyp.backend.member.repository.MemberSocialLoginInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: 차후에 탈퇴한 회원이 소셜 로그인을 다시 시도할 경우, 회원가입이 안되는 문제 해결 필요
@Service
@RequiredArgsConstructor
public class SocialLoginServiceImpl implements SocialLoginService {

    private final AppleLoginUtil appleLoginUtil;
    private final KakaoClientService kakaoClientService;
    private final MemberSocialLoginInfoRepository memberSocialLoginInfoRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MemberInfo getMemberInfoByAccessKeyAndProviderType(String accessToken,
            SocialLoginProviderType providerType) throws JsonProcessingException {
        try {
            KakaoSocialLoginResponse response = kakaoClientService.getAccessTokenInfo(
                    accessToken);
            Optional<MemberSocialLoginInfo> maybeMemberSocialLoginInfo =
                    memberSocialLoginInfoRepository.findByProviderIdAndProviderType(
                            String.valueOf(response.getId()), providerType);

            if (maybeMemberSocialLoginInfo.isPresent()) {
                MemberSocialLoginInfo memberSocialLoginInfo =
                        maybeMemberSocialLoginInfo.get();
                return MemberInfo.builder()
                        .memberId(memberSocialLoginInfo.getMember().getMemberId())
                        .username(memberSocialLoginInfo.getMember().getUsername())
                        .roleType(RoleType.USER)
                        .build();
            }

            Member member = Member.builder()
                    .username(response.getKakaoAccount().getEmail())
                    .nickname(response.getKakaoAccount().getProfile().getNickname())
                    .password(" ")
                    .isActive(true)
                    .build();

            memberRepository.save(member);

            Role role = Role.builder()
                    .member(member)
                    .roleType(RoleType.USER)
                    .build();

            member.getRoles().add(role);

            MemberSocialLoginInfo memberSocialLoginInfo = MemberSocialLoginInfo.builder()
                    .providerId(String.valueOf(response.getId()))
                    .providerType(providerType)
                    .member(member)
                    .build();
            memberSocialLoginInfoRepository.save(memberSocialLoginInfo);
            memberRepository.save(member);

            return MemberInfo.builder()
                    .memberId(member.getMemberId())
                    .username(member.getUsername())
                    .roleType(RoleType.USER)
                    .build();
        } catch (FeignException e) {
            throw new IllegalArgumentException("소셜 로그인 정보를 가져오는 중 에러가 발생했습니다.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MemberInfo getMemberInfoByIdentityTokenAndAuthorizationCodeAndProviderType(
            String identityToken, String authorizationCode, SocialLoginProviderType providerType) {
        AppleTokenVerificationResult result = appleLoginUtil.verifyAppleToken(
                identityToken);

        String appleUserId = result.getSubject();

        Optional<MemberSocialLoginInfo> maybeMemberSocialLoginInfo =
                memberSocialLoginInfoRepository.findByProviderIdAndProviderType(
                        appleUserId, providerType);

        if (maybeMemberSocialLoginInfo.isPresent()) {
            MemberSocialLoginInfo memberSocialLoginInfo =
                    maybeMemberSocialLoginInfo.get();
            return MemberInfo.builder()
                    .memberId(memberSocialLoginInfo.getMember().getMemberId())
                    .username(memberSocialLoginInfo.getMember().getUsername())
                    .roleType(RoleType.USER)
                    .build();
        }

        AppleUserInfo userInfo = appleLoginUtil.getUserInfoFromAuthCode(
                authorizationCode);
        Member member = Member.builder()
                .username(userInfo.getEmail())
                .nickname("하루")
                .password(" ")
                .isActive(true)
                .build();

        memberRepository.save(member);

        Role role = Role.builder()
                .member(member)
                .roleType(RoleType.USER)
                .build();
        member.getRoles().add(role);

        MemberSocialLoginInfo memberSocialLoginInfo = MemberSocialLoginInfo.builder()
                .providerId(appleUserId)
                .providerType(providerType)
                .member(member)
                .build();
        memberSocialLoginInfoRepository.save(memberSocialLoginInfo);
        memberRepository.save(member);

        return MemberInfo.builder()
                .memberId(member.getMemberId())
                .username(member.getUsername())
                .roleType(RoleType.USER)
                .build();
    }
}
