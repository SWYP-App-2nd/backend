package kr.swyp.backend.authentication.dto;

import java.util.Map;
import java.util.Set;
import kr.swyp.backend.member.enums.SocialLoginProviderType;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SocialLoginAuthenticationToken extends AbstractAuthenticationToken {

    private final String accessToken;
    private final SocialLoginProviderType providerType;

    public SocialLoginAuthenticationToken(String accessToken,
            SocialLoginProviderType providerType) {
        super(Set.of());
        this.accessToken = accessToken;
        this.providerType = providerType;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return Map.of("accessToken", accessToken, "providerType", providerType);
    }

    @Override
    public Object getPrincipal() {
        return Map.of("accessToken", accessToken, "providerType", providerType);
    }
}
