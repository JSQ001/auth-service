/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.hand.hcf.app.base.user.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.system.constant.CacheConstants;
import com.hand.hcf.app.base.user.domain.User;
import com.hand.hcf.app.base.user.dto.UserQO;
import com.hand.hcf.app.base.user.persistence.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = {CacheConstants.USER})
public class UserCacheableService {
    @Autowired
    private UserMapper userMapper;

    //jiu.zhao redis
    //@Cacheable(key="#userOid.toString()")
    public User getByUserOid(UUID userOid) {
        List<User> users= userMapper.selectList(new EntityWrapper<User>().eq("user_oid",userOid));
        if (users.size()>0){
            return users.get(0);
        }
        return null;
    }

    //jiu.zhao redis
    //@Cacheable(key="#userOid.toString().concat(#tenantId.toString())")
    public User getByTenantIdAndUserOid(Long tenantId, UUID userOid) {
        List<User> users=  userMapper.listByQO(UserQO.builder().tenantId(tenantId).userOid(userOid).build());
        if (users.size()>0){
            return users.get(0);
        }
        return null;
    }

    //jiu.zhao redis
    //@Cacheable(key = "#id.toString()")
    public User getById(Long id) {
        return userMapper.selectById(id);
    }


    @CacheEvict(key="#user.userOid.toString()")
    public User evictCacheUserByUserOid(User user) {
        return user;
    }

    @CacheEvict(key="#user.id.toString()")
    public User evictCacheUserByUserId(User user) {
        return user;
    }


    @CacheEvict(key = "#email")
    public void evictCacheUserByEmail(String email) {
    }

    @CacheEvict(key = "#employeeId.concat(#tenantId)")
    public void evictCacheUserByEmployeeId(Long tenantId, String employeeId) {

    }
    @CachePut(key="#user.userOid.toString()")
    public User reloadCacheUserByUserOid(User user) {
        return user;
    }
    @CachePut(key="#user.id.toString()")
    public User reloadCacheUserByUserId(User user) {
        return user;
    }
}
