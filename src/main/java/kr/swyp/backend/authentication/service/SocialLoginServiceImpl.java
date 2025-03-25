package kr.swyp.backend.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import java.util.Optional;
import kr.swyp.backend.authentication.dto.MemberInfo;
import kr.swyp.backend.authentication.dto.SocialLoginDto.KakaoSocialLoginResponse;
import kr.swyp.backend.member.domain.Member;
import kr.swyp.backend.member.domain.MemberSocialLoginInfo;
import kr.swyp.backend.member.domain.Role;
import kr.swyp.backend.member.enums.RoleType;
import kr.swyp.backend.member.enums.SocialLoginProviderType;
import kr.swyp.backend.member.repository.MemberRepository;
import kr.swyp.backend.member.repository.MemberSocialLoginInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialLoginServiceImpl implements SocialLoginService {

    private final KakaoClientService kakaoClientService;
    private final MemberSocialLoginInfoRepository memberSocialLoginInfoRepository;
    private final MemberRepository memberRepository;

    @Override
    public MemberInfo getMemberInfoBySocialIdAndProviderType(String accessToken,
            SocialLoginProviderType providerType) throws JsonProcessingException {
        try {
            switch (providerType) {
                case KAKAO -> {
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

                    // TODO: username을 Email로 변경 필요
                    Member member = Member.builder()
                            .username(response.getKakaoAccount().getProfile().getNickname())
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
                }
                case APPLE -> {
                    // TODO: APPPLE 로그인 구현
                    return null;
                }
                default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인 타입입니다.");
            }
        } catch (FeignException e) {
            throw new IllegalArgumentException("소셜 로그인 정보를 가져오는 중 에러가 발생했습니다.");
        }
    }
}
