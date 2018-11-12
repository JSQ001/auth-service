

package com.hand.hcf.app.base.service;

import com.hand.hcf.app.base.domain.CompanyConfiguration;
import com.hand.hcf.app.base.domain.CompanySecurity;
import com.hand.hcf.app.base.persistence.CompanyConfigurationMapper;
import com.hand.hcf.app.base.persistence.CompanySecurityMapper;
import com.hand.hcf.app.base.persistence.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CompanyService {
	@Autowired
    CompanyMapper companyMapper;
    @Autowired
    CompanyConfigurationMapper companyConfigurationMapper;
    @Autowired
    CompanySecurityMapper companySecurityMapper;

    public List<CompanyConfiguration> findOneByCompanyOID(UUID companyOID) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("company_oid", companyOID);
        return companyConfigurationMapper.selectByMap(paramMap);
    }

    public List<CompanySecurity> getTenantCompanySecurity(Long tenantId) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("tenant_id", tenantId);
        List<CompanySecurity>   companySecurities=companySecurityMapper.selectByMap(paramMap);
        System.out.println(companySecurities);
        return companySecurities;
    }
	public Long findTenantIdByCompanyOID(UUID companyOID) {
        return companyMapper.findTenantIdByCompanyOID(companyOID);
    }
}
