package com.hand.hcf.app.core.exception;

public class SqlProcessInterceptorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SqlProcessInterceptorException(String message) {
        super(message);
    }

    public SqlProcessInterceptorException(Throwable throwable) {
        super(throwable);
    }

    public SqlProcessInterceptorException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
