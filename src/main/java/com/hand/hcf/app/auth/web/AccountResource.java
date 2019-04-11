

package com.hand.hcf.app.auth.web;

import com.hand.hcf.app.auth.service.AuthUserService;
import com.hand.hcf.core.exception.core.UnauthenticatedException;
import com.hand.hcf.core.security.domain.Authority;
import com.hand.hcf.core.security.domain.PrincipalLite;
import jline.internal.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountResource {
    @Autowired
    private AuthUserService userService;
    @GetMapping(value = "/check_token")
    public PrincipalLite checkToken() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2Authentication) {
            OAuth2Authentication auth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            if (auth2Authentication.getUserAuthentication() != null) {
                if (auth2Authentication.getPrincipal() instanceof Map) {
                    Log.info("auth2Authentication is map");
                }
                return (PrincipalLite) auth2Authentication.getPrincipal();
            } else {
                String clientId = auth2Authentication.getOAuth2Request().getClientId();
                PrincipalLite principalLite = userService.getUserByOauthClientId(clientId);
                if(null == principalLite){
                    principalLite = new PrincipalLite();
                }
                principalLite.setLogin((String) auth2Authentication.getPrincipal());
                principalLite.setAuthorities(auth2Authentication.getAuthorities().stream().map(right -> new Authority(right.getAuthority())).collect(Collectors.toSet()));
                return principalLite;
            }
        } else {
            throw new UnauthenticatedException();
        }

    }
}
