/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.hand.hcf.app.base.codingrule.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hand.hcf.app.base.codingrule.domain.CodingRuleObject;
import com.hand.hcf.app.base.codingrule.persistence.CodingRuleObjectMapper;
import com.hand.hcf.app.base.system.constant.CacheConstants;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = {CacheConstants.CODING_RULE_OBJECT})
public class CodingRuleCacheObjectService extends ServiceImpl<CodingRuleObjectMapper, CodingRuleObject> {

    @Cacheable(key = "':ENABLE:'.concat(#tenantId.toString()).concat(#documentTypeCode)")
    public boolean findByTenantIdAndDocumentType(String documentTypeCode, Long tenantId) {
        CodingRuleObject codingRuleObject =
            this.selectOne(new EntityWrapper<CodingRuleObject>()
                .eq("tenant_id", tenantId)
                .eq("company_code","")
                .eq("enabled", true)
                .eq("document_type_code", documentTypeCode)
            );
        return codingRuleObject != null;
    }

    @Cacheable(key = "':ENABLE:'.concat(#tenantId.toString()).concat(#documentTypeCode).concat(#companyCode)")
    public boolean findByCompanyCodeAndDocumentType(String companyCode, String documentTypeCode, Long tenantId) {
        CodingRuleObject codingRuleObject =
            this.selectOne(new EntityWrapper<CodingRuleObject>()
                .eq("tenant_id", tenantId)
                .eq("enabled", true)
                .eq("company_code", companyCode)
                .eq("document_type_code", documentTypeCode)
            );

        return codingRuleObject != null;
    }

    @CachePut(key = "':ENABLE:'.concat(#tenantId.toString()).concat(#documentTypeCode).concat(#companyCode)")
    public Boolean evictByCompanyCodeAndDocumentType(String companyCode, String documentTypeCode, Long tenantId, Boolean isEnabled) {
        return isEnabled;
    }

    @CachePut(key = "':ENABLE:'.concat(#tenantId.toString()).concat(#documentTypeCode)")
    public Boolean evictByTenantIdAndDocumentType(String documentTypeCode, Long tenantId, Boolean isEnabled) {
        return isEnabled;
    }

}
