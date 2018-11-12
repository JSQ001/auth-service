

package com.hand.hcf.app.base.web;

import com.hand.hcf.core.security.domain.Authority;
import com.hand.hcf.core.security.domain.PrincipalLite;
import com.hand.hcf.app.base.exception.UnauthenticatedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AccountResource {
    @RequestMapping(value = "/check_token", method = RequestMethod.GET)
    public PrincipalLite checkToken() {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof OAuth2Authentication) {
            OAuth2Authentication auth2Authentication = (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
            if (auth2Authentication.getUserAuthentication() != null) {
                if (auth2Authentication.getPrincipal() instanceof Map) {
                    System.out.println("map");
                }
                return (PrincipalLite) auth2Authentication.getPrincipal();
//                return extractor.extractPrincipal((Map<String, Object>) auth2Authentication.getPrincipal());
            } else {
                PrincipalLite principalLite = new PrincipalLite();
                principalLite.setAuthorities(auth2Authentication.getAuthorities().stream().map(right -> new Authority(right.getAuthority())).collect(Collectors.toSet()));
                principalLite.setLogin((String) auth2Authentication.getPrincipal());
                return principalLite;
            }
        } else {
            throw new UnauthenticatedException();
        }

    }
}
