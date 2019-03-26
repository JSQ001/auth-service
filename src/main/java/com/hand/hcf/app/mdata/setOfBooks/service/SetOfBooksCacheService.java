package com.hand.hcf.app.mdata.setOfBooks.service;

import com.hand.hcf.app.mdata.system.constant.CacheConstants;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by fanfuqiang 2018/11/20
 */
@Service
@Transactional
@CacheConfig(cacheNames = {CacheConstants.SET_OF_BOOKS})
public class SetOfBooksCacheService {
    @CacheEvict(key = "'tenantSOB'.concat(#tenantId.toString())")
    public void evictTenantSetOfBooks(Long tenantId) {
    }
}
