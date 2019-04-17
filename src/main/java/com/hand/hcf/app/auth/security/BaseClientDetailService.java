
package com.hand.hcf.app.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;


@Service
public class BaseClientDetailService
    implements org.springframework.security.oauth2.provider.ClientDetailsService {

    @Autowired
    JdbcClientDetailsService clientDetailsService;

    @Override
    //jiu.zhao redis
    //@Cacheable(value = "clientCache", keyGenerator = "wiselyKeyGenerator")
    public ClientDetails loadClientByClientId(String clientId)  {
        return clientDetailsService.loadClientByClientId(clientId);
    }
}
