

package com.hand.hcf.app.core.security.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class PrincipalLite implements UserDetails {

    private static final long serialVersionUID = 1;

    protected UUID userOid;
    protected Long id;
    protected String login;
    protected String password;
    @Builder.Default
    protected boolean activated = false;
    protected Set<Authority> authorities = new HashSet<>();

    protected Integer status;
    protected String language;

    protected Long tenantId;
    private String userName;
    private String email;
    private String mobile;
    public PrincipalLite() {

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
