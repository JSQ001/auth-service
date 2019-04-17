

package com.hand.hcf.app.core.exception.core;

public class ServiceUnavailableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceUnavailableException() {
        super();
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceUnavailableException(String message) {
        super(message);
    }

    public ServiceUnavailableException(Throwable cause) {
        super(cause);
    }

}
