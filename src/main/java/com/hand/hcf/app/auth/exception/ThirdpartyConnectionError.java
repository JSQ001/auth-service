package com.hand.hcf.app.auth.exception;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * @author li.qu
 * @date ${date}.
 */
public class ThirdpartyConnectionError extends OAuth2Exception {

    public ThirdpartyConnectionError(String msg, Throwable t) {
        super(msg, t);
    }

    public ThirdpartyConnectionError(String msg) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "connection_fail";
    }
}
