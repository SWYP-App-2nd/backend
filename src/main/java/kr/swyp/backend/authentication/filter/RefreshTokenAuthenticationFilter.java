package kr.swyp.backend.authentication.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import kr.swyp.backend.authentication.dto.AuthenticationDto.RefreshTokenAuthenticationRequest;
import kr.swyp.backend.authentication.dto.RefreshTokenAuthenticationToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

@Slf4j
public class RefreshTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private static final AntPathRequestMatcher REQUEST_MATCHER = new AntPathRequestMatcher(
            "/auth/renew", "POST");
    private final ObjectMapper objectMapper;

    public RefreshTokenAuthenticationFilter(AuthenticationManager am, ObjectMapper objectMapper) {
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
        String token = this.obtainRefreshToken(request);

        var authRequest = new RefreshTokenAuthenticationToken(token);

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request,
            RefreshTokenAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    private String obtainRefreshToken(HttpServletRequest request) {
        try {
            String messageBody = StreamUtils.copyToString(request.getInputStream(),
                    StandardCharsets.UTF_8);

            RefreshTokenAuthenticationRequest dto = objectMapper.readValue(
                    messageBody, RefreshTokenAuthenticationRequest.class);

            log.info("[토큰 갱신 요청] 토큰 갱신 요청: {}", dto.getRefreshToken());

            return dto.getRefreshToken();
        } catch (IOException e) {
            log.warn("[토큰 갱신 요청] 토큰 갱신 요청 디코딩 실패! {}", e.getMessage());
            throw new AuthenticationCredentialsNotFoundException(
                    "요청 데이터가 올바르지 않아 토큰을 갱신하는 데 실패했습니다.");
        }
    }
}
