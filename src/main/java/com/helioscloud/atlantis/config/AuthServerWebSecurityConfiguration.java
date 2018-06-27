/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.config;

import com.helioscloud.atlantis.security.ArtemisProvider.ArtemisAuthenticationProvider;
import com.helioscloud.atlantis.security.ArtemisProvider.SSOAuthenticationProvider;
import com.helioscloud.atlantis.security.ArtemisProvider.SSODirectClientAuthenticationProvider;
import com.helioscloud.atlantis.service.SSODetailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import javax.annotation.PostConstruct;

/**
 * Created by markfredchen on 07/06/2017.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthServerWebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AuthenticationEventPublisher publisher;

    @Value("${single-sign.server.host}")
    private String ssourl;
    @Value("${single-sign.server.client-id}")
    private String clientId;
    @Value("${single-sign.server.client-secret}")
    private String clientSecret;
    @Value("${single-sign.server.access-token-uri}")
    private String accessTokenUri;

    public AuthServerWebSecurityConfiguration(UserDetailsService userDetailsService, AuthenticationManagerBuilder builder, AuthenticationEventPublisher publisher) {
        this.userDetailsService = userDetailsService;
        this.authenticationManagerBuilder = builder;
        this.publisher = publisher;
    }

    @PostConstruct
    public void init() throws Exception {
        authenticationManagerBuilder
                .authenticationEventPublisher(publisher)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        authenticationManagerBuilder.authenticationProvider(wxAuthProvider());
        authenticationManagerBuilder.authenticationProvider(ssoAuthenticationProvider());
        authenticationManagerBuilder.authenticationProvider(ssoDirectClientAuthenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
     //   return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/test/**")
                .antMatchers("/h2-console/**")
                .antMatchers(HttpMethod.GET, "/api/qr/authorization")
                .antMatchers(HttpMethod.GET, "/api/qr/authorization/*")
        ;
    }

    @Bean
    public ArtemisAuthenticationProvider wxAuthProvider() throws Exception {
        ArtemisAuthenticationProvider provider = new ArtemisAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SSODetailService ssoDetailService() {
        return new SSODetailService();
    }

    @Bean
    public SSOAuthenticationProvider ssoAuthenticationProvider() {
        SSOAuthenticationProvider authenticationProvider = new SSOAuthenticationProvider();
        authenticationProvider.setSsourl(ssourl);
        return authenticationProvider;
    }

    @Bean
    public OAuth2RestTemplate ssoOAuth2RestTemplate() {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        details.setAccessTokenUri(accessTokenUri);

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(details, new DefaultOAuth2ClientContext());

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(10000);
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setReadTimeout(10000);

        restTemplate.setRequestFactory(httpRequestFactory);
        return restTemplate;
    }

    @Bean
    public SSODirectClientAuthenticationProvider ssoDirectClientAuthenticationProvider() {
        SSODirectClientAuthenticationProvider authenticationProvider = new SSODirectClientAuthenticationProvider();
        authenticationProvider.setSsourl(ssourl);
        return authenticationProvider;
    }
  /*  @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();

    }*/
}

