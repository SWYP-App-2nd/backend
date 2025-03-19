package kr.swyp.backend.authentication.handler;

import static kr.swyp.backend.authentication.exception.AuthException.AuthExceptionCode.OAUTH_ERROR;

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
public class Oauth2AuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        log.info("소셜 로그인에 실패! {}", exception.getMessage());
        handlerExceptionResolver.resolveException(request, response, null, new AuthException(
                OAUTH_ERROR, exception.getMessage(), HttpStatus.UNAUTHORIZED));
    }
}