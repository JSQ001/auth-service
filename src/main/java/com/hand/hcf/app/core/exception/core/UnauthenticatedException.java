

package com.hand.hcf.app.core.exception.core;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException() {
        super("user.not.login");
    }

    protected String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UnauthenticatedException(String message) {
        super(message);
    }

    public UnauthenticatedException(String code, String message) {
        this(message);
        this.setCode(code);
    }
}
