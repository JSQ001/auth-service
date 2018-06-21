package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.domain.CompanyConfiguration;
import com.helioscloud.atlantis.domain.CompanySecurity;
import com.helioscloud.atlantis.persistence.CompanyConfigurationMapper;
import com.helioscloud.atlantis.persistence.CompanySecurityMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

public class CompanyServiceTest extends AuthServiceSelectTest {

    @Autowired
    private CompanyService companyService;

    @MockBean
    CompanyConfigurationMapper companyConfigurationMapper;

    @MockBean
    CompanySecurityMapper companySecurityMapper;

    @Test
    public void findOneByCompanyOID() {
        given(companyConfigurationMapper.selectByMap(Mockito.anyMap())).willReturn(getCompanyConfiguration());
        companyService.findOneByCompanyOID(UUID.fromString("007f076b-1c77-4b57-924e-fdd854e9ff6b"));
    }

    private List<CompanyConfiguration> getCompanyConfiguration() {
        List<CompanyConfiguration> rs = new ArrayList<>();
        CompanyConfiguration c = new CompanyConfiguration();
        rs.add(c);
        return rs;
    }

    @Test
    public void getTenantCompanySecurity() {
        given(companySecurityMapper.selectByMap(Mockito.anyMap())).willReturn(getCompanySecurity());
        companyService.getTenantCompanySecurity(1L);
    }

    private List<CompanySecurity> getCompanySecurity() {
        List<CompanySecurity> rs = new ArrayList<>();
        CompanySecurity c = new CompanySecurity();
        rs.add(c);
        return rs;
    }
}