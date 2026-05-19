package roomescape.exception;

import roomescape.dto.error.ErrorCode;

public class AuthenticationException extends RuntimeException {
    private final ErrorCode errorCode;

    public AuthenticationException(String message) {
        this(ErrorCode.AUTHENTICATION_REQUIRED, message);
    }

    public AuthenticationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
