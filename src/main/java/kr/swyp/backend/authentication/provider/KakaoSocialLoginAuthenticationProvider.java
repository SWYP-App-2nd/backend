package kr.swyp.backend.authentication.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import kr.swyp.backend.authentication.dto.KakaoSocialLoginAuthenticationToken;
import kr.swyp.backend.authentication.dto.MemberInfo;
import kr.swyp.backend.authentication.service.SocialLoginService;
import kr.swyp.backend.member.dto.MemberDetails;
import kr.swyp.backend.member.enums.RoleType;
import kr.swyp.backend.member.enums.SocialLoginProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RequiredArgsConstructor
public class KakaoSocialLoginAuthenticationProvider implements AuthenticationProvider {

    private final SocialLoginService socialLoginService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        Map<String, Object> socialLoginInfoMap =
                (Map<String, Object>) authentication.getPrincipal();
        try {
            MemberInfo memberInfo = socialLoginService.getMemberInfoByAccessKeyAndProviderType(
                    (String) socialLoginInfoMap.get("accessToken"),
                    (SocialLoginProviderType) socialLoginInfoMap.get("providerType"));

            MemberDetails memberDetails = new MemberDetails(memberInfo.getMemberId(),
                    memberInfo.getUsername(), "", getAuthorities(memberInfo.getRoleType()));

            return new UsernamePasswordAuthenticationToken(memberDetails, "",
                    memberDetails.getAuthorities());
        } catch (IllegalArgumentException | JsonProcessingException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    private Collection<GrantedAuthority> getAuthorities(RoleType roleType) {
        return Collections.singletonList(new SimpleGrantedAuthority(roleType.name()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return KakaoSocialLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
