package com.hand.hcf.app.core.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

import java.lang.reflect.Method;

@Configuration
public class CacheConfiguration extends CachingConfigurerSupport {
    @Bean
    public KeyGenerator authenticationKeyGenerator() {
        return new KeyGenerator() {
            DefaultAuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append("AccessTokenWithAuthentication");
                if (params.length >= 1 && params[0] != null) {
                    if (params[0] instanceof OAuth2Authentication) {
                        sb.append(authenticationKeyGenerator.extractKey((OAuth2Authentication) params[0]));

                    }
                } else {
                    sb.append(System.currentTimeMillis());
                }

                return sb.toString();
            }
        };
    }

    @Bean
    public KeyGenerator tokenKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append("Authentication");
                if (params.length >= 1 && params[0] != null) {
                    if (params[0] instanceof OAuth2AccessToken) {
                        sb.append(((OAuth2AccessToken) params[0]).getValue());
                    }
                } else {
                    sb.append(System.currentTimeMillis());
                }

                return sb.toString();
            }
        };
    }

    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

}
