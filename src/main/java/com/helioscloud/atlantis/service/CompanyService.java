/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.domain.CompanyConfiguration;
import com.helioscloud.atlantis.domain.CompanySecurity;
import com.helioscloud.atlantis.persistence.CompanyConfigurationMapper;
import com.helioscloud.atlantis.persistence.CompanyMapper;
import com.helioscloud.atlantis.persistence.CompanySecurityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Project Name:auth-service
 * Package Name:com.helioscloud.atlantis.service
 * Date:2018/5/16
 * Create By:zongyun.zhou@hand-china.com
 */
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
