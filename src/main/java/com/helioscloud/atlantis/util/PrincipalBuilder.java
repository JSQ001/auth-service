package com.helioscloud.atlantis.util;

import com.cloudhelios.atlantis.security.domain.Authority;
import com.cloudhelios.atlantis.security.domain.PrincipalLite;
import com.helioscloud.atlantis.dto.UserDTO;

import java.util.HashSet;
import java.util.Set;


/**
 * <p> </p>
 *
 * @Author: bin.xie
 * @Date: 2018/10/26
 */
public class PrincipalBuilder {

    public static PrincipalLite builder(UserDTO u){
        PrincipalLite principalLite = new PrincipalLite();
        principalLite.setId(u.getId());
        principalLite.setUserOID(u.getUserOID());
        principalLite.setLogin(u.getLogin());
        principalLite.setPassword(u.getPassword());
        principalLite.setActivated(u.isActivated());
        //延迟加载问题
        Set<Authority> authorities = new HashSet<Authority>();
        authorities.addAll(u.getAuthorities());
        principalLite.setAuthorities(authorities);
        principalLite.setStatus(u.getStatus());
        principalLite.setLanguage(u.getLanguage());
        principalLite.setTenantId(u.getTenantId());
        principalLite.setSetOfBooksId(u.getSetOfBooksId());
        principalLite.setCompanyId(u.getCompanyId());
        principalLite.setCompanyOID(u.getCompanyOID());
        principalLite.setEmployeeID(u.getEmployeeID());
        principalLite.setFullName(u.getFullName());
        principalLite.setEmail(u.getEmail());
        principalLite.setMobile(u.getMobile());
        return principalLite;
    }
}
