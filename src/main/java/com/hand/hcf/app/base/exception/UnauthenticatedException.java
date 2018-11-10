

package com.hand.hcf.app.base.exception;

/**
 * Created by markfredchen on 9/19/15.
 */
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
