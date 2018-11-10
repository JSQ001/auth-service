package com.hand.hcf.app.base;

/*
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes={DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class,
        ArtemisAuthorizationCodeServices.class, OAuthHelperH2.class,
        JdbcClientDetailsService.class, AppConfigurationTest.class})
public abstract class AuthServiceH2Test {
}

@TestConfiguration
class AppConfigurationTest {
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
    public TokenStore tokenStore(DataSource dataSource) {
        return new ArtemisRedisTokenStore(dataSource);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new ArtemisTokenEnhancer();
    }
}*/
