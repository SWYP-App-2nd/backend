package kr.swyp.backend.authentication.handler;

import static kr.swyp.backend.authentication.exception.AuthException.AuthExceptionCode.CREDENTIAL_NOT_FOUND;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.swyp.backend.authentication.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

@RequiredArgsConstructor
public class UsernamePasswordAuthenticationFailureHandler implements
        AuthenticationFailureHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String message = exception.getMessage();

        if (exception instanceof BadCredentialsException) {
            message = "아이디와 비밀번호가 올바른지 확인해 주세요.";
        }
        AuthException authException = new AuthException(CREDENTIAL_NOT_FOUND, message,
                HttpStatus.UNAUTHORIZED);
        handlerExceptionResolver.resolveException(request, response, null, authException);
    }
}
