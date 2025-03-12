package kr.swyp.backend.common.exception.handler;

import kr.swyp.backend.authentication.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorInfo> handleAuthException(AuthException e) {
        log.info("[인증 과정에 오류 발생] {}", e.getMessage());
        return responseException(e.getCode(), e.getMessage(), e.getHttpStatus());
    }

    private ResponseEntity<ErrorInfo> responseException(String code, String message,
            HttpStatus status) {
        ErrorInfo errorInfo = new ErrorInfo(code, message);
        return ResponseEntity.status(status).body(errorInfo);
    }

    private record ErrorInfo(String code, String message) {

    }
}
