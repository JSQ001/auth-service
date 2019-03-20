package com.hand.hcf.app.auth.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

@JsonSerialize(using = OAuthExceptionSerializer.class)
public class ExtOAuthException extends OAuth2Exception {

    public ExtOAuthException(String msg) {
        super(msg);
    }
}
