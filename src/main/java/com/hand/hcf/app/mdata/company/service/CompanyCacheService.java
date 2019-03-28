/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.hand.hcf.app.mdata.company.service;

import com.hand.hcf.app.mdata.company.domain.Company;
import com.hand.hcf.app.mdata.company.dto.CompanyQO;
import com.hand.hcf.app.mdata.company.persistence.CompanyMapper;
import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@CacheConfig(cacheNames = {CacheConstants.COMPANY})
public class CompanyCacheService {
    @Autowired
    CompanyMapper companyMapper;

    @CacheEvict(key = "#currentTenantID.toString()")
    public void evictTenantCompany(Long currentTenantID) {

    }

    @Cacheable(key = "#companyOid.toString()")
    public Company getByCompanyOid(UUID companyOid) {
        return companyMapper.getByQO(CompanyQO.builder().companyOid(companyOid).build()).get(0);
    }

    @CacheEvict(key = "#companyOid.toString()")
    public void evictCompanyByCompanyOid(UUID companyOid) {

    }
}
