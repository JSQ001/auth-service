/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.handchina.yunmart.artemis.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.handchina.yunmart.artemis.domain.Authority;
import com.helioscloud.atlantis.dto.UserDTO;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by markfredchen on 16/1/6.
 */
@Data
public class PrincipalLite implements UserDetails {

    private static final long serialVersionUID = 1;

    protected UUID userOID;
    protected Long id;
    protected String login;
    @JsonIgnore
    protected String password;
    protected boolean activated = false;
    protected Set<Authority> authorities = new HashSet<>();

    protected Integer status;
    protected String language;

    protected Long tenantId;
    protected Long companyId;
    protected UUID companyOID;
    protected Long setOfBooksId;

    private String employeeID;
    private String fullName;
    private String email;
    private String mobile;
    public PrincipalLite() {

    }

    public PrincipalLite(UserDTO u) {
        this.setId(u.getId());
        this.setUserOID(u.getUserOID());
        this.setLogin(u.getLogin());
        this.setPassword(u.getPassword());
        this.setActivated(u.isActivated());
        //延迟加载问题
        Set<Authority> authorities = new HashSet<Authority>();
        authorities.addAll(u.getAuthorities());
        this.setAuthorities(authorities);
        this.setStatus(u.getStatus());
        this.setLanguage(u.getLanguage());
        this.setTenantId(u.getTenantId());
        this.setSetOfBooksId(u.getSetOfBooksId());
        this.setCompanyId(u.getCompanyId());
        this.setCompanyOID(u.getCompanyOID());
        this.setEmployeeID(u.getEmployeeID());
        this.setFullName(u.getFullName());
        this.setEmail(u.getEmail());
        this.setMobile(u.getMobile());
    }

    @Override
    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.activated;
    }
}
