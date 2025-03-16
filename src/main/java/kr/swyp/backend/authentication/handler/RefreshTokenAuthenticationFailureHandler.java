package kr.swyp.backend.authentication.handler;

import static kr.swyp.backend.authentication.exception.AuthException.AuthExceptionCode.TOKEN_RENEW_FAILED;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.swyp.backend.authentication.exception.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@RequiredArgsConstructor
public class RefreshTokenAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        log.debug("토큰 갱신 실패! {}", exception.getMessage());

        handlerExceptionResolver.resolveException(request, response, null,
                new AuthException(TOKEN_RENEW_FAILED, exception.getMessage(),
                        HttpStatus.UNAUTHORIZED));
    }
}
