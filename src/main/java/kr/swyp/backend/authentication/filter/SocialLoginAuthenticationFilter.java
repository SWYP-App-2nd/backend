package kr.swyp.backend.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import kr.swyp.backend.authentication.dto.AppleSocialLoginAuthenticationToken;
import kr.swyp.backend.authentication.dto.KakaoSocialLoginAuthenticationToken;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AbstractSocialLoginRequest;
import kr.swyp.backend.authentication.dto.SocialLoginDto.AppleSocialLoginRequest;
import kr.swyp.backend.authentication.dto.SocialLoginDto.KakaoSocialLoginRequest;
import kr.swyp.backend.member.enums.SocialLoginProviderType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

public class SocialLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher REQUEST_MATCHER = new AntPathRequestMatcher(
            "/auth/social", "POST");
    private final ObjectMapper objectMapper;

    public SocialLoginAuthenticationFilter(AuthenticationManager am, ObjectMapper objectMapper) {
        super(REQUEST_MATCHER, am);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        AbstractAuthenticationToken authRequest;
        AbstractSocialLoginRequest token = this.obtainSocialLoginToken(request);
        if (token.getProviderType().equals(SocialLoginProviderType.KAKAO)) {
            KakaoSocialLoginRequest kakaoToken = (KakaoSocialLoginRequest) token;
            authRequest = new KakaoSocialLoginAuthenticationToken(kakaoToken.getAccessToken(),
                    kakaoToken.getProviderType());
        } else if (token.getProviderType().equals(SocialLoginProviderType.APPLE)) {
            AppleSocialLoginRequest appleToken = (AppleSocialLoginRequest) token;
            authRequest = new AppleSocialLoginAuthenticationToken(appleToken.getIdentityToken(),
                    appleToken.getAuthorizationCode(), appleToken.getProviderType());
        } else {
            throw new AuthenticationServiceException("지원하지 않는 소셜 로그인입니다.");
        }

        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request,
            AbstractAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    private AbstractSocialLoginRequest obtainSocialLoginToken(HttpServletRequest request) {
        try {
            String messageBody = StreamUtils.copyToString(request.getInputStream(),
                    StandardCharsets.UTF_8);

            // Jackson이 자동으로 올바른 하위 타입으로 변환
            AbstractSocialLoginRequest loginRequest = objectMapper.readValue(messageBody,
                    AbstractSocialLoginRequest.class);

            // providerType null 확인
            if (loginRequest.getProviderType() == null) {
                throw new AuthenticationCredentialsNotFoundException("소셜 로그인 제공자 타입이 누락되었습니다.");
            }

            return loginRequest;
        } catch (IOException e) {
            throw new AuthenticationCredentialsNotFoundException(
                    "요청 데이터를 파싱할 수 없습니다: " + e.getMessage(), e);
        }
    }
}