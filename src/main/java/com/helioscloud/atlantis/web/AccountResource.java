/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.web;

import com.helioscloud.atlantis.domain.Authority;
import com.helioscloud.atlantis.security.PrincipalLite;
import com.helioscloud.atlantis.exception.UnauthenticatedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Project Name:auth-service
 * Package Name:com.helioscloud.atlantis.web
 * Date:2018/5/12
 * Create By:zongyun.zhou@hand-china.com
 */
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
