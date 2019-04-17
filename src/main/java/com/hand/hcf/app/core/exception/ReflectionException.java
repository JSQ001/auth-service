package com.hand.hcf.app.core.exception;

public class ReflectionException extends RuntimeException {

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(Throwable throwable) {
        super(throwable);
    }

    public ReflectionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
