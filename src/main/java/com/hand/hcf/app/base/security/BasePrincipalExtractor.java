

package com.hand.hcf.app.base.security;

import com.hand.hcf.core.security.domain.PrincipalLite;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;
import java.util.UUID;

public class BasePrincipalExtractor implements PrincipalExtractor {

    @Override
    public PrincipalLite extractPrincipal(Map<String, Object> map) {
        PrincipalLite principal = new PrincipalLite();
        principal.setLanguage((String) map.get("language"));
        principal.setLogin((String) map.get("login"));
        if (map.get("userOID") != null) {
            principal.setUserOid(UUID.fromString((String) map.get("userOID")));
        }
        principal.setCompanyId((Long) map.get("companyId"));
        if (map.get("companyOID") != null) {
            principal.setCompanyOid(UUID.fromString((String) map.get("companyOID")));
        }
        principal.setTenantId((Long) map.get("tenantId"));
        principal.setTenantId((Long) map.get("setOfBooksId"));
        return principal;
    }
}