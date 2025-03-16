package kr.swyp.backend.authentication.provider;

import java.util.Collection;
import java.util.Collections;
import kr.swyp.backend.authentication.dto.MemberInfo;
import kr.swyp.backend.authentication.dto.RefreshTokenAuthenticationToken;
import kr.swyp.backend.authentication.service.RefreshTokenService;
import kr.swyp.backend.member.dto.MemberDetails;
import kr.swyp.backend.member.enums.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@RequiredArgsConstructor
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final RefreshTokenService refreshTokenService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String refreshToken = (String) authentication.getPrincipal();

        try {
            MemberInfo memberInfo = refreshTokenService.getMemberInfoByRefreshTokenString(
                    refreshToken);

            refreshTokenService.removeRefreshToken(refreshToken);

            MemberDetails memberDetails = new MemberDetails(memberInfo.getMemberId(),
                    memberInfo.getUsername(), "", getAuthorities(memberInfo.getRoleType()));

            return new UsernamePasswordAuthenticationToken(memberDetails, "",
                    memberDetails.getAuthorities());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(e.getMessage());
        }
    }

    private Collection<GrantedAuthority> getAuthorities(RoleType roleType) {
        return Collections.singletonList(new SimpleGrantedAuthority(roleType.name()));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return RefreshTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}