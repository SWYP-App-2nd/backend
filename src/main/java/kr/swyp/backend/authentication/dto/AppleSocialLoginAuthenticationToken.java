package kr.swyp.backend.authentication.dto;

import java.util.Map;
import java.util.Set;
import kr.swyp.backend.member.enums.SocialLoginProviderType;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class AppleSocialLoginAuthenticationToken extends AbstractAuthenticationToken {

    private final String identityToken;
    private final String authorizationCode;
    private final SocialLoginProviderType providerType;

    public AppleSocialLoginAuthenticationToken(String identityToken, String authorizationCode,
            SocialLoginProviderType providerType) {
        super(Set.of());
        this.identityToken = identityToken;
        this.authorizationCode = authorizationCode;
        this.providerType = providerType;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return Map.of("identityToken", identityToken, "authorizationCode", authorizationCode,
                "providerType", providerType);
    }

    @Override
    public Object getPrincipal() {
        return Map.of("identityToken", identityToken, "authorizationCode", authorizationCode,
                "providerType", providerType);
    }
}
