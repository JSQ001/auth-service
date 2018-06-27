package com.helioscloud.atlantis;

import com.helioscloud.atlantis.security.ArtemisProvider.ArtemisAuthenticationProvider;
import com.helioscloud.atlantis.security.ArtemisProvider.SSOAuthenticationProvider;
import com.helioscloud.atlantis.security.ArtemisProvider.SSODirectClientAuthenticationProvider;
import com.helioscloud.atlantis.service.SSODetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
//@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class AuthServerConfigTest extends WebSecurityConfigurerAdapter {

    @Autowired
    @SuppressWarnings("static-method")
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(wxAuthProvider());
        auth.authenticationProvider(ssoAuthenticationProvider());
        auth.authenticationProvider(ssoDirectClientAuthenticationProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
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
        authenticationProvider.setSsourl("http://localhost:9999");
        return authenticationProvider;
    }

    @Bean
    public SSODirectClientAuthenticationProvider ssoDirectClientAuthenticationProvider() {
        SSODirectClientAuthenticationProvider authenticationProvider = new SSODirectClientAuthenticationProvider();
        authenticationProvider.setSsourl("http://localhost:9999");
        return authenticationProvider;
    }

//    @Bean
//    public RestTemplate ssoDirectClientRestTemplate() {
//        return new RestTemplate();
//    }
//
//    @Bean
//    public RestTemplate ssoRestTemplate() {
//        return new RestTemplate();
//    }
}
