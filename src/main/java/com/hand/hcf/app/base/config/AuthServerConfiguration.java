

package com.hand.hcf.app.base.config;

import com.hand.hcf.app.base.security.ArtemisAuthorizationCodeServices;
import com.hand.hcf.app.base.security.ArtemisTokenEnhancer;
import com.hand.hcf.app.base.security.ArtemisRedisTokenStore;
import com.hand.hcf.app.base.security.ArtemisTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.OAuth2RequestValidator;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;

/**
 * Created by markfredchen on 06/06/2017.
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;

    private DataSource dataSource;

    public AuthServerConfiguration(AuthenticationManager authenticationManager,DataSource dataSource) {
        this.authenticationManager = authenticationManager;
        this.dataSource=dataSource;
    }

    @Bean
    public TokenStore tokenStore() {
        return new ArtemisRedisTokenStore(dataSource);
    }

    @Bean
    public JdbcClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new ArtemisTokenEnhancer();
    }

    @Bean
    @Primary
    public AuthorizationServerTokenServices getTokenServices() {
        ArtemisTokenService tokenServices = new ArtemisTokenService();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setTokenEnhancer(tokenEnhancer());
        tokenServices.setAccessTokenValiditySeconds(7200);
        ////tokenServices.setClientDetailsService(this.clientDetailsService());
        //tokenServices.setTokenEnhancer(this.tokenEnhancer());
        //this.addUserDetailsService(tokenServices, this.userDetailsService);
        return tokenServices;
    }

    @Bean
    public ArtemisTokenService artemisTokenService() {
        ArtemisTokenService tokenServices = new ArtemisTokenService();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setTokenEnhancer(tokenEnhancer());
        return tokenServices;
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
            throws Exception {

        endpoints
                .tokenStore(tokenStore())
                .tokenServices(getTokenServices())
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.allowFormAuthenticationForClients();
        oauthServer.checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);

    }

    @Bean
    public OAuth2RequestFactory requestFactory(DataSource dataSource) {
        return new DefaultOAuth2RequestFactory(new JdbcClientDetailsService(dataSource));
    }

    @Bean
    public RedirectResolver redirectResolver() {
        return new DefaultRedirectResolver();
    }

    @Bean
    public OAuth2RequestValidator oAuth2RequestValidator() {
        return new DefaultOAuth2RequestValidator();
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices(DataSource dataSource) {
        return new ArtemisAuthorizationCodeServices(dataSource);
    }
}
