package com.hand.hcf.app.auth.config;

import com.hand.hcf.app.auth.constant.Constants;
import com.hand.hcf.app.auth.exception.ExtOAuthException;
import com.hand.hcf.core.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;


@Component
public class ExtWebResponseExceptionTranslator implements WebResponseExceptionTranslator {

    @Autowired
    MessageService messageService;

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e)  {

        OAuth2Exception oAuth2Exception = (OAuth2Exception) e;

        String message=oAuth2Exception.getMessage();
        if (Constants.BAD_CREDENTIALS.equals(message)){
            message=messageService.getMessageFromSource(Constants.USER_BAD_CREDENTIALS);
        }


        return ResponseEntity
                .status(oAuth2Exception.getHttpErrorCode())
                .body(new ExtOAuthException(message));
    }
}
