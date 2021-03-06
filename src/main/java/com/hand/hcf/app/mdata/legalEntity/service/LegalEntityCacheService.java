/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.hand.hcf.app.mdata.legalEntity.service;

import com.hand.hcf.app.mdata.legalEntity.domain.LegalEntity;
import com.hand.hcf.app.mdata.legalEntity.persistence.LegalEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LegalEntityCacheService {
    @Autowired
    private LegalEntityMapper legalEntityMapper;


    public LegalEntity getLegalEntityByOid(UUID legalEntityOid) {
        LegalEntity param = new LegalEntity();
        param.setLegalEntityOid(legalEntityOid);
        return legalEntityMapper.selectOne(param);
    }

    public void evictTenantLegalEntity(Long tenantId) {

    }

    public LegalEntity reloadCacheLegalEntityById(LegalEntity legalEntity) {
        return legalEntity;
    }

    public LegalEntity reloadCacheLegalEntityByOid(LegalEntity legalEntity) {
        return legalEntity;
    }
}
