package com.hand.hcf.app.base.listener;

import com.hand.hcf.app.client.system.AuthClient;
import com.hand.hcf.app.client.system.UserRequestCO;
import com.hand.hcf.core.web.util.HttpRequestUtil;
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
    private AuthClient authClient;

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
            UserRequestCO requestCO=new UserRequestCO();
            requestCO.setUserName(username);
            requestCO.setUserAgent(request.getHeader("User-Agent"));
            requestCO.setIp(HttpRequestUtil.getRealRemoteAddr(request));
            authClient.loginFailed(requestCO);
        }
    }
}
