package com.helioscloud.atlantis.persistence;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.domain.CompanySecurity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class CompanySecurityMapperTest extends AuthServiceSelectTest {
    @Autowired
    private CompanySecurityMapper companySecurityMapper;

    @Test
    public void test_query() {
        String s = "007f076b-1c77-4b57-924e-fdd854e9ff6b";
        CompanySecurity companySecurity = companySecurityMapper.testMapper(s);
        assertThat(companySecurity.getCompanyOID().toString()).isEqualTo(s);
    }
}