package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.config.AppCenterProperties;
import com.helioscloud.atlantis.domain.enumeration.Function;
import com.helioscloud.atlantis.dto.AuthenticationCode;
import com.helioscloud.atlantis.dto.UserDTO;
import com.helioscloud.atlantis.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author qingsheng.chen
 * @date 2017/12/29 12:05
 * @description 扫码登录
 */
@Service
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    @Autowired
    private AppCenterProperties appCenterProperties;
    @Autowired
    private RedisTemplate<String, AuthenticationCode> redisTemplate;
    @Autowired
    private UserService userService;



    public String getAuthentication() {
        AuthenticationCode authenticationCode = new AuthenticationCode();
        authenticationCode.setUuid(UUID.randomUUID().toString())
                .setStatus(AuthenticationCode.INITIAL);
        ValueOperations<String, AuthenticationCode> operations = redisTemplate.opsForValue();
        operations.set(authenticationCode.getUuid(), authenticationCode);
        redisTemplate.expire(authenticationCode.getUuid(), appCenterProperties.getAuthentication().getExpireSecond(), TimeUnit.SECONDS);
        return appCenterProperties.getAuthentication().getDownloadUrl() + "?UUID=" + authenticationCode.getUuid() + Function.QR_PC_LOGIN.urlContent();
    }

    public AuthenticationCode getAuthentication(String uuid) {
        ValueOperations<String, AuthenticationCode> operations = redisTemplate.opsForValue();
        long endTime = System.currentTimeMillis() + appCenterProperties.getAuthentication().getDuration();
        AuthenticationCode authenticationCode = null;
        while (System.currentTimeMillis() < endTime) {
            authenticationCode = operations.get(uuid);
            // 过期/等待登录(第一次等待登录需要返回)/已登陆/初始
            if (authenticationCode == null) {
                return null;
            } else if (AuthenticationCode.WAITING.equals(authenticationCode.getStatus())) {
                if (!authenticationCode.isReturnWaiting()) {
                    authenticationCode.setReturnWaiting(true);
                    long expires = redisTemplate.getExpire(uuid, TimeUnit.SECONDS);
                    operations.set(uuid, authenticationCode);
                    redisTemplate.expire(uuid, expires, TimeUnit.SECONDS);
                    return authenticationCode;
                }
            } else if (AuthenticationCode.LOGGED.equals(authenticationCode.getStatus())) {
                redisTemplate.delete(uuid);
                return authenticationCode;
            }
            try {
                Thread.sleep(appCenterProperties.getAuthentication().getInterval());
            } catch (InterruptedException e) {
                LOGGER.error("Sleep error, {}", e.getMessage());
            }
        }
        return authenticationCode;
    }

    public boolean preLogin(String uuid) {
        ValueOperations<String, AuthenticationCode> operations = redisTemplate.opsForValue();
        AuthenticationCode authenticationCode = operations.get(uuid);
        // code 不存在或者状态为已经登录，不可再登录
        if (authenticationCode == null
                || AuthenticationCode.LOGGED.equals(authenticationCode.getStatus())) {
            return false;
        }
        UserDTO user = userService.findOneByUserOID(SecurityUtils.getCurrentUserOID());
        authenticationCode.setUsername(user.getFullName())
                .setCompanyName(user.getCompanyName())
                .setStatus(AuthenticationCode.WAITING)
                .setReturnWaiting(false);
        long expires = redisTemplate.getExpire(uuid, TimeUnit.SECONDS);
        operations.set(uuid, authenticationCode);
        redisTemplate.expire(uuid, expires, TimeUnit.SECONDS);
        return true;
    }

    public boolean login(String uuid) {
        ValueOperations<String, AuthenticationCode> operations = redisTemplate.opsForValue();
        AuthenticationCode authenticationCode = operations.get(uuid);
        if (authenticationCode == null || AuthenticationCode.LOGGED.equals(authenticationCode.getStatus())) {
            return false;
        }
        authenticationCode.setUserOID(SecurityUtils.getCurrentUserOID());
        authenticationCode.setStatus(AuthenticationCode.LOGGED);
        long expires = redisTemplate.getExpire(uuid, TimeUnit.SECONDS);
        operations.set(uuid, authenticationCode);
        redisTemplate.expire(uuid, expires, TimeUnit.SECONDS);
        return true;
    }
}
