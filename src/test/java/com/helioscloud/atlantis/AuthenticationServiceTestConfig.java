package com.helioscloud.atlantis;

import com.helioscloud.atlantis.config.AppCenterProperties;
import com.helioscloud.atlantis.security.ArtemisRedisTokenStore;
import com.helioscloud.atlantis.security.ArtemisTokenEnhancer;
import com.helioscloud.atlantis.security.ArtemisTokenService;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@ActiveProfiles("select")
@SpringBootTest(classes={DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class,
         AppCenterProperties.class, AppConfigurationSelectTest.class, OAuthHelperH2.class})
public abstract class AuthenticationServiceTestConfig {
}

@TestConfiguration
@MapperScan(basePackages = "com.helioscloud.atlantis.persistence")
@ComponentScan(basePackages = "com.helioscloud.atlantis.service")
class AppConfigurationSelectTest {
    @Bean
    public ArtemisTokenService artemisTokenService(DataSource dataSource) {
        ArtemisTokenService tokenServices = new ArtemisTokenService();
        tokenServices.setTokenStore(tokenStore(dataSource));
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setTokenEnhancer(tokenEnhancer());
        return tokenServices;
    }

    @Bean
    public JdbcClientDetailsService clientDetailsService(DataSource dataSource) {
        return new JdbcClientDetailsService(dataSource);
    }

    @Bean
    public ArtemisRedisTokenStore tokenStore(DataSource dataSource) {
        return new ArtemisRedisTokenStore(dataSource);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new ArtemisTokenEnhancer();
    }
}