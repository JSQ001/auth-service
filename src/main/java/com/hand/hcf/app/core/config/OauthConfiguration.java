package com.hand.hcf.app.core.config;

import com.hand.hcf.app.core.service.LoggingRequestInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/8/22 17:41
 */
@Configuration
public class OauthConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    ClientCredentialsResourceDetails resourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    public OAuth2RestTemplate oAuth2RestTemplate() {
        String strategy = System.getProperty("spring.security.strategy");
        if (!StringUtils.hasText(strategy)){
            SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        }
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(this.resourceDetails(), new DefaultOAuth2ClientContext());
        List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors = new ArrayList();
        clientHttpRequestInterceptors.add(new LoggingRequestInterceptor());
        restTemplate.setInterceptors(clientHttpRequestInterceptors);
        return restTemplate;
    }

    @Bean(name = "normalRestTemplate")
    public RestTemplate normalRestTemplate() {
        RestTemplate restTemplate= new RestTemplate();
        return restTemplate;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
