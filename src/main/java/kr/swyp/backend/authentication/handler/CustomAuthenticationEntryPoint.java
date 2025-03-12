package kr.swyp.backend.authentication.handler;

import static kr.swyp.backend.authentication.exception.AuthException.AuthExceptionCode.UNAUTHENTICATED;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.swyp.backend.authentication.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;

@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        AuthException authException = new AuthException(UNAUTHENTICATED, "로그인이 필요합니다.",
                HttpStatus.UNAUTHORIZED);
        handlerExceptionResolver.resolveException(request, response, null, authException);
    }
}
