package com.hand.hcf.app.base.service;

import com.hand.hcf.app.base.component.RedisHelper;
import com.hand.hcf.app.base.constant.CacheConstants;
import com.hand.hcf.app.base.dto.ClientDTO;
import com.hand.hcf.app.base.persistence.OauthMapper;
import com.hand.hcf.app.base.security.BaseTokenService;
import com.hand.hcf.app.base.security.BaseTokenStore;
import com.hand.hcf.core.security.domain.PrincipalLite;
import com.hand.hcf.core.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OauthService extends BaseService<OauthMapper, ClientDTO> {

    @Autowired
    private OauthMapper oauthMapper;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private ApplicationContext applicationContext;

    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public void deleteOauthAccessToken(String login){
        List<String> dbResult=baseMapper.findRefreshByLogin(login);
        if (!CollectionUtils.isEmpty(dbResult)){
            dbResult.stream().forEach(token->baseMapper.deleteRefreshByToken(token));
        }

        baseMapper.deleteOauthTokenByLogin(login);
    }

    public List<ClientDTO> listCompanyClient(UUID companyOid){
      return baseMapper.getCompanyClient(companyOid);
    }

    public List<ClientDTO> listTenantClient(Long tenantId){
        return baseMapper.getTenantClient(tenantId);
    }

    public void updateOauthAccessTokenCompanyByLogin(String login, Long companyId, UUID companyOid) {

        BaseTokenService baseTokenService = new BaseTokenService();
        BaseTokenStore baseTokenStore = (BaseTokenStore) applicationContext.getBean("tokenStore");
        List<String> tokenIds = oauthMapper.findAuthenticationIdByLogin(login);
        if (!org.springframework.util.CollectionUtils.isEmpty(tokenIds)) {
            tokenIds.stream().forEach(u -> {
                String tokenValue = baseTokenStore.getTokenValueByTokenId(u);
                OAuth2Authentication authentication;
                try {
                    authentication = baseTokenService.loadAuthentication(tokenValue);
                } catch (InvalidTokenException e) {
                    //token 过期，不做操作，已有删除操作
                    return;
                }
                PrincipalLite principal = (PrincipalLite) authentication.getPrincipal();
                principal.setCompanyOid(companyOid);
                principal.setCompanyId(companyId);
                String key = authenticationKeyGenerator.extractKey(authentication);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("authentication", SerializationUtils.serialize(authentication));
                map.put("authenticationId", key);
                oauthMapper.updateOauthAccessTokenById(map);
                //删除authentication缓存对象
                redisHelper.deleteByKey(CacheConstants.TOKEN.concat("::Authentication").concat(tokenValue));
                redisHelper.deleteByKey(CacheConstants.TOKEN.concat("::").concat(tokenValue));
            });
        }
    }
}
