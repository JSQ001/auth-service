/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.hand.hcf.app.mdata.legalEntity.service;

import com.hand.hcf.app.mdata.legalEntity.domain.LegalEntity;
import com.hand.hcf.app.mdata.legalEntity.persistence.LegalEntityMapper;
import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import com.hand.hcf.core.service.BaseI18nService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@CacheConfig(cacheNames = {CacheConstants.LEGAL_ENTITY})
public class LegalEntityCacheService {
    @Autowired
    private LegalEntityMapper legalEntityMapper;

    @Autowired
    private BaseI18nService baseI18nService;

    //jiu.zhao redis
    //@Cacheable(key = "#legalEntityOid.toString()")
    public LegalEntity getLegalEntityByOid(UUID legalEntityOid) {
        LegalEntity param = new LegalEntity();
        param.setLegalEntityOid(legalEntityOid);
        return legalEntityMapper.selectOne(param);
    }

    @CacheEvict(key = "#tenantId.toString()")
    public void evictTenantLegalEntity(Long tenantId) {

    }

    @CachePut(key = "#legalEntity.id.toString()")
    public LegalEntity reloadCacheLegalEntityById(LegalEntity legalEntity) {
        return legalEntity;
    }

    @CachePut(key = "#legalEntity.legalEntityOid.toString()")
    public LegalEntity reloadCacheLegalEntityByOid(LegalEntity legalEntity) {
        return legalEntity;
    }
}
