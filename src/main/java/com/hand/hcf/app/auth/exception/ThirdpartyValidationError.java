package com.hand.hcf.app.auth.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author li.qu
 * @date ${date}.
 */
public class ThirdpartyValidationError extends OAuth2Exception {

    public ThirdpartyValidationError(String msg, Throwable t) {
        super(msg, t);
    }

    public ThirdpartyValidationError(String msg) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "validation_fail";
    }
}
