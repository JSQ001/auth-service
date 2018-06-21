package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.domain.CompanyConfiguration;
import com.helioscloud.atlantis.domain.ConfigurationDetail;
import com.helioscloud.atlantis.exception.UserNotActivatedException;
import com.helioscloud.atlantis.persistence.CompanyConfigurationMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

public class WxServiceTest extends AuthServiceSelectTest {

    @Autowired
    private WxService wxService;

    @MockBean
    CompanyConfigurationMapper companyConfigurationMapper;

    @Test
    public void authenticate() {
    }

    @Test
    public void buildBaseJson() {
    }

    @Test
    public void getCompanyConfiguration() {
    }

    @Test
    public void httpsPostJson() {
    }

    @Test
    public void loadUserByUsername() {
        given(companyConfigurationMapper.selectByMap(Mockito.anyMap())).willReturn(getaCompanyConfiguration());

        assertThatThrownBy(() -> wxService.loadUserByUsername("yxsF2vHmIzEBKF0h7X7U8PSEx4ay7hrBICDmVMwWvZY", "ww284c3d009e081043", "ww7004f021bfb6099a", ""))
                .isInstanceOf(UserNotActivatedException.class)
                .hasMessage("user.not.found");
    }

    private List<CompanyConfiguration> getaCompanyConfiguration() {
        List<CompanyConfiguration> rs = new ArrayList<>();
        CompanyConfiguration c = new CompanyConfiguration();
        ConfigurationDetail configuration = new ConfigurationDetail();
        ConfigurationDetail.WxConfiguration wxConfiguration = new ConfigurationDetail.WxConfiguration();
        wxConfiguration.setCorpId("a");
        wxConfiguration.setSecretKey("b");
        configuration.setWxConfiguration(wxConfiguration);
        c.setConfiguration(configuration);
        rs.add(c);
        return rs;
    }
}