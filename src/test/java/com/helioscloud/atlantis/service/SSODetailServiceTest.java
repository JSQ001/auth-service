package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.persistence.CompanySecurityMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class SSODetailServiceTest extends AuthServiceSelectTest {

    @Autowired
    private SSODetailService service;

    @MockBean
    CompanySecurityMapper companySecurityMapper;

    @Test
    public void loadUserByUsername() {
        String s = "yunong.li@hand-china.com";
        given(companySecurityMapper.selectByMap(Mockito.anyMap())).willReturn(new ArrayList<>());

        UserDetails userDetails = service.loadUserByUsername(s);
        assertThat(userDetails.getUsername()).isEqualTo("18602619951");
    }
}