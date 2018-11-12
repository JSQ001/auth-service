
package com.hand.hcf.app.base.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;


@Service
public class ArtemisClientDetailService
    implements org.springframework.security.oauth2.provider.ClientDetailsService {

    @Autowired
    JdbcClientDetailsService clientDetailsService;

    @Cacheable(value = "clientCache", keyGenerator = "wiselyKeyGenerator")
    public ClientDetails loadClientByClientId(String clientId) throws InvalidClientException {
        ClientDetails details = clientDetailsService.loadClientByClientId(clientId);
        return details;
    }
}
