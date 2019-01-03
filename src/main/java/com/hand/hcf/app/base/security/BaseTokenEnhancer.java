
package com.hand.hcf.app.base.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Map;

public class BaseTokenEnhancer implements TokenEnhancer {
    @Autowired
    JdbcClientDetailsService clientDetailsService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
                                     OAuth2Authentication authentication) {
        // 将client details 中的additional information放在token当中
        String clientId = authentication.getOAuth2Request().getClientId();
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        Map<String, Object> additionalInfo = clientDetails.getAdditionalInformation();
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
