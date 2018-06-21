package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.domain.CompanySecurity;
import com.helioscloud.atlantis.dto.UserDTO;
import com.helioscloud.atlantis.persistence.CompanySecurityMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class UserServiceTest extends AuthServiceSelectTest {

    @Autowired
    private UserService userService;

    @MockBean
    CompanySecurityMapper companySecurityMapper;

    @Test
    public void findOneByContactEmail() {
        String s = "yunong.li@hand-china.com";
        UserDTO userDTO = userService.findOneByContactEmail(s);
        assertThat(userDTO.getEmail()).isEqualTo(s);
    }

    @Test
    public void loginCommonCheck() {
        given(companySecurityMapper.selectByMap(Mockito.anyMap())).willReturn(getCompanySecurity());
        String s = "yunong.li@hand-china.com";
        UserDTO userDTO = userService.findOneByContactEmail(s);
        userService.loginCommonCheck(userDTO);
    }

    private List<CompanySecurity> getCompanySecurity() {
        List<CompanySecurity> rs = new ArrayList<>();
        CompanySecurity c = new CompanySecurity();
        c.setPasswordExpireDays(1);
        rs.add(c);
        return rs;
    }
}