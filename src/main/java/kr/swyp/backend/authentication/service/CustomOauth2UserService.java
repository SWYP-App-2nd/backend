package kr.swyp.backend.authentication.service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import kr.swyp.backend.authentication.dto.CustomOauth2User;
import kr.swyp.backend.authentication.dto.OauthAttributes;
import kr.swyp.backend.member.domain.Member;
import kr.swyp.backend.member.domain.MemberOauthInfo;
import kr.swyp.backend.member.domain.Role;
import kr.swyp.backend.member.enums.Oauth2ProviderType;
import kr.swyp.backend.member.enums.RoleType;
import kr.swyp.backend.member.repository.MemberOauthInfoRepository;
import kr.swyp.backend.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberOauthInfoRepository memberOauthInfoRepository;
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User user = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Oauth2ProviderType providerType = Oauth2ProviderType.valueOf(registrationId.toUpperCase());
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = user.getAttributes();

        OauthAttributes extractAttributes = OauthAttributes.from(providerType,
                userNameAttributeName, attributes);

        Member member = getMember(providerType, extractAttributes);

        Set<GrantedAuthority> authoritySet = Set.of(
                new SimpleGrantedAuthority(RoleType.USER.getAuthority()));

        return new CustomOauth2User(authoritySet, attributes, userNameAttributeName,
                member.getMemberId(), member.getUsername(), RoleType.USER);
    }

    private Member getMember(Oauth2ProviderType providerType, OauthAttributes attributes) {
        Optional<MemberOauthInfo> maybeMemberOauthInfo =
                memberOauthInfoRepository.findByProviderTypeAndProviderId(providerType,
                        attributes.getOauth2UserInfo().getId());

        if (maybeMemberOauthInfo.isPresent()) {
            MemberOauthInfo memberOauthInfo = maybeMemberOauthInfo.get();
            return memberOauthInfo.getMember();
        } else {
            Member member = Member.builder()
                    // TODO: 차후에 username을 email로 설정하게 변경
                    .username(attributes.getOauth2UserInfo().getNickname())
                    .nickname(attributes.getOauth2UserInfo().getNickname())
                    .password(" ")
                    .isActive(true)
                    .build();

            memberRepository.save(member);

            Role role = Role.builder()
                    .member(member)
                    .roleType(RoleType.USER)
                    .build();
            member.getRoles().add(role);

            MemberOauthInfo memberOauthInfo = MemberOauthInfo.builder()
                    .providerType(providerType)
                    .providerId(attributes.getOauth2UserInfo().getId())
                    .member(member)
                    .build();

            memberRepository.save(member);
            memberOauthInfoRepository.save(memberOauthInfo);

            return member;
        }
    }

}
