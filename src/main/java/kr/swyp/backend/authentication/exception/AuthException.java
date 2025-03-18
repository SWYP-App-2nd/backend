package kr.swyp.backend.authentication.exception;

import kr.swyp.backend.common.exception.CommonException;
import org.springframework.http.HttpStatus;

public class AuthException extends CommonException {

    public AuthException(AuthExceptionCode code, String message, HttpStatus httpStatus) {
        super(code.name(), message, httpStatus);
    }

    public enum AuthExceptionCode {
        UNAUTHENTICATED,
        CREDENTIAL_NOT_FOUND,
        TOKEN_ERROR,
        PERMISSION_DENIED,
        TOKEN_RENEW_FAILED,
        OAUTH_ERROR,
    }
}
