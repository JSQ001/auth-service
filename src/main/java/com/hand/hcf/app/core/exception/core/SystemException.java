

package com.hand.hcf.app.core.exception.core;

public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SystemException() {
        super();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

}
