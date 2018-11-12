

package com.hand.hcf.app.base.security;

import com.hand.hcf.app.base.constant.CacheConstants;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import javax.sql.DataSource;

/**
 * Project Name:artemis
 * Package Name:com.handchina.yunmart.artemis.security
 * Date:2018/5/11
 * Create By:zongyun.zhou@hand-china.com
 */
@CacheConfig(cacheNames = {CacheConstants.TOKEN})
public class ArtemisRedisTokenStore extends ArtemisTokenStore {
    public ArtemisRedisTokenStore(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Cacheable(key = "#tokenValue")
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return super.readAccessToken(tokenValue);
    }

    @Override
    @CacheEvict(key = "#token.value")
    public void removeAccessToken(OAuth2AccessToken token) {
        super.removeAccessToken(token);
    }

    @Override
    @Cacheable(key = "'Authentication'.concat(#token.value)")
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return super.readAuthentication(token);
    }
}
