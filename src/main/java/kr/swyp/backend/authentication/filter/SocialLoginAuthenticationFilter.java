package kr.swyp.backend.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import kr.swyp.backend.authentication.dto.SocialLoginAuthenticationToken;
import kr.swyp.backend.authentication.dto.SocialLoginDto.SocialLoginRequest;
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

        SocialLoginRequest token = this.obtainSocialLoginToken(request);
        var authRequest = new SocialLoginAuthenticationToken(token.getAccessToken(),
                token.getProviderType());

        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request,
            SocialLoginAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    private SocialLoginRequest obtainSocialLoginToken(HttpServletRequest request) {
        try {
            String messageBody = StreamUtils.copyToString(request.getInputStream(),
                    StandardCharsets.UTF_8);

            return objectMapper.readValue(
                    messageBody, SocialLoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationCredentialsNotFoundException(
                    "요청 데이터가 올바르지 않아 소셜 로그인에 실패했습니다.");
        }
    }
}