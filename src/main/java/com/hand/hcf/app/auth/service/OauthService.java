package com.hand.hcf.app.auth.service;

import com.hand.hcf.app.auth.dto.ClientDTO;
import com.hand.hcf.app.auth.persistence.OauthMapper;
import com.hand.hcf.app.auth.security.BaseTokenService;
import com.hand.hcf.app.auth.security.BaseTokenStore;
import com.hand.hcf.app.core.service.BaseService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class OauthService extends BaseService<OauthMapper, ClientDTO> {

    @Autowired
    private OauthMapper oauthMapper;


    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BaseTokenService baseTokenService;

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

    public void updateOauthAccessTokenCompanyByLogin(String login) {
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
                String key = authenticationKeyGenerator.extractKey(authentication);
                Map<String, Object> map = new HashMap<>();
                map.put("authentication", SerializationUtils.serialize(authentication));
                map.put("authenticationId", key);
                oauthMapper.updateOauthAccessTokenById(map);
            });
        }
    }
}
