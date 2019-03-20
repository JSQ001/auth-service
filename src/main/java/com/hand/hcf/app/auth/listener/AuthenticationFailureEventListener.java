package com.hand.hcf.app.auth.listener;

import com.hand.hcf.app.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by caixiang on 2017/7/20.
 */
@Component
public class AuthenticationFailureEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final HttpServletRequest request;

    @Autowired
    private UserService userService;

    public AuthenticationFailureEventListener(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        if (event.getException() != null && event.getException().getCause() instanceof InvalidTokenException) {
            return;
        }
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof String) {
            String username = (String) principal;
           userService.loginFailed(username,request);
        }
    }
}
