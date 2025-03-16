package kr.swyp.backend.authentication.dto;

import java.util.Collection;
import java.util.Set;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class RefreshTokenAuthenticationToken extends AbstractAuthenticationToken {

    private final String refreshToken;

    public RefreshTokenAuthenticationToken(String refreshToken) {
        super(Set.of());
        this.refreshToken = refreshToken;
        setAuthenticated(false);
    }

    public RefreshTokenAuthenticationToken(String refreshToken,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.refreshToken = refreshToken;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return refreshToken;
    }

    @Override
    public Object getPrincipal() {
        return refreshToken;
    }
}
