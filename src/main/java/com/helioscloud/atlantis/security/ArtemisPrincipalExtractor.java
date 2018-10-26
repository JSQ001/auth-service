/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.security;

import com.handchina.yunmart.artemis.security.PrincipalLite;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;
import java.util.UUID;

/**
 * Project Name:auth-service
 * Package Name:com.helioscloud.atlantis.security
 * Date:2018/5/12
 * Create By:zongyun.zhou@hand-china.com
 */
public class ArtemisPrincipalExtractor implements PrincipalExtractor {

    @Override
    public PrincipalLite extractPrincipal(Map<String, Object> map) {
        PrincipalLite principal = new PrincipalLite();
        principal.setLanguage((String) map.get("language"));
        principal.setLogin((String) map.get("login"));
        if (map.get("userOID") != null) {
            principal.setUserOID(UUID.fromString((String) map.get("userOID")));
        }
        principal.setCompanyId((Long) map.get("companyId"));
        if (map.get("companyOID") != null) {
            principal.setCompanyOID(UUID.fromString((String) map.get("companyOID")));
        }
        principal.setTenantId((Long) map.get("tenantId"));
        principal.setTenantId((Long) map.get("setOfBooksId"));
//        ((ArrayList<Map<String, String>>)map.get("authorities")).forEach(a ->
//                principal.getAuthorities().add(a.get("name")));
        return principal;
    }
}
