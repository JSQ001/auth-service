package com.hand.hcf.app.core.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * @description:
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/8/22 17:41
 */
@RestController
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * CORS过滤器，填加CORS头
     * @return CORS过滤器，供Spring使用。
     */
    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        source.registerCorsConfiguration("/**/api/**", config);
        return new CorsFilter(source);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/**/index/**").permitAll()
                .antMatchers("/**/api/**").authenticated()
                .antMatchers("/**/suite/**").permitAll()
                .antMatchers("/**/userInfo/**").permitAll();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer config) {
        config.resourceId("default")
                .authenticationEntryPoint(new AuthExceptionEntryPoint())
                .expressionHandler(oauthWebSecurityExpressionHandler(applicationContext));
    }

    /**
     * 为 OAuth2WebSecurityExpressionHandler 添加 applicationContext
     * @param applicationContext Spring ApplicationContext
     * @return 添加后的 OAuth2WebSecurityExpressionHandler
     */
    @Bean
    public OAuth2WebSecurityExpressionHandler oauthWebSecurityExpressionHandler(
            ApplicationContext applicationContext) {
        OAuth2WebSecurityExpressionHandler expressionHandler =
                new OAuth2WebSecurityExpressionHandler();
        expressionHandler.setApplicationContext(applicationContext);
        return expressionHandler;
    }

}