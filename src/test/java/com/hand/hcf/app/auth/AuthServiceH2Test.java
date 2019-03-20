package com.hand.hcf.app.auth;

/*
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(classes={DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class,
        BaseAuthorizationCodeServices.class, OAuthHelperH2.class,
        JdbcClientDetailsService.class, AppConfigurationTest.class})
public abstract class AuthServiceH2Test {
}

@TestConfiguration
class AppConfigurationTest {
    @Bean
    public BaseTokenService artemisTokenService(DataSource dataSource) {
        BaseTokenService tokenServices = new BaseTokenService();
        tokenServices.setTokenStore(tokenStore(dataSource));
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setTokenEnhancer(tokenEnhancer());
        return tokenServices;
    }

    @Bean
    public TokenStore tokenStore(DataSource dataSource) {
        return new BaseRedisTokenStore(dataSource);
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new BaseTokenEnhancer();
    }
}*/
