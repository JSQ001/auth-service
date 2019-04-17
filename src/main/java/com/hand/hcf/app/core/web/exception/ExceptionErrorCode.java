package com.hand.hcf.app.core.web.exception;

import org.springframework.http.HttpStatus;

public enum ExceptionErrorCode {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST),

    OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND),

    UNAUTHENTICATED(HttpStatus.FORBIDDEN),

    SECURITY_VIOLATION(HttpStatus.FORBIDDEN),

    AUTHENTICATION_FAILED(HttpStatus.FORBIDDEN),

    SYSTEM_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR),

    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE),

    ENCRYPT_FAIL(HttpStatus.NOT_ACCEPTABLE),

    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),

    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),

    CONFLICT(HttpStatus.CONFLICT);

    HttpStatus httpStatus;

    ExceptionErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
