package com.hand.hcf.app.base.config;

import com.hand.hcf.app.base.constant.Constants;
import com.hand.hcf.app.base.exception.ExtOAuthException;
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
        if (com.hand.hcf.app.base.constant.Constants.BAD_CREDENTIALS.equals(message)){
            message=messageService.getMessageFromSource(Constants.USER_BAD_CREDENTIALS);
        }


        return ResponseEntity
                .status(oAuth2Exception.getHttpErrorCode())
                .body(new ExtOAuthException(message));
    }
}
