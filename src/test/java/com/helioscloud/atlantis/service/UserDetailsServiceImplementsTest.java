package com.helioscloud.atlantis.service;


import com.helioscloud.atlantis.domain.UserLoginBind;
import com.helioscloud.atlantis.exception.UserNotActivatedException;
import com.helioscloud.atlantis.persistence.CompanySecurityMapper;
import com.helioscloud.atlantis.persistence.UserLoginBindMapper;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

public class UserDetailsServiceImplementsTest {

   /* @Autowired
    private UserDetailsServiceImplements userDetailsServiceImplements;

    @MockBean
    UserLoginBindMapper userLoginBindMapper;
    @MockBean
    CompanySecurityMapper companySecurityMapper;

    @Test
    public void loadUserByUsername_success() {
        ArgumentCaptor<UserLoginBind> captora = ArgumentCaptor.forClass(UserLoginBind.class);
        given(userLoginBindMapper.selectById(captora.capture())).willReturn(new UserLoginBind());
        given(companySecurityMapper.selectByMap(null)).willReturn(new ArrayList<>());

        UserDetails details = userDetailsServiceImplements.loadUserByUsername("GTLKemail003@huilianyi.com");
        assertThat(details).isNotNull();

        assertThatThrownBy(() -> userDetailsServiceImplements.loadUserByUsername("decdaglal@126.com"))
                .isInstanceOf(UserNotActivatedException.class)
                .hasMessage("user.not.activated");

        details = userDetailsServiceImplements.loadUserByUsername("18587520392");
        assertThat(details).isNotNull();

        assertThatThrownBy(() -> userDetailsServiceImplements.loadUserByUsername("15200027956_LEAVED_1510726074333"))
                .isInstanceOf(UserNotActivatedException.class)
                .hasMessage("user.was.leaved");

    }*/
}