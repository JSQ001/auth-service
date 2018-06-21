package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.AuthenticationServiceTestConfig;
import com.helioscloud.atlantis.OAuthHelperH2;
import com.helioscloud.atlantis.dto.AuthenticationCode;
import com.helioscloud.atlantis.dto.UserDTO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class AuthenticationServiceTest extends AuthenticationServiceTestConfig {

  /*  @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private OAuthHelperH2 helper;

    @MockBean
    private RedisTemplate<String, AuthenticationCode> redisTemplate;

    @MockBean
    private UserService userService;

    @Before
    public void init() {
        given(redisTemplate.opsForValue()).willReturn(getOperations());
        given(userService.findOneByUserOID(Mockito.any())).willReturn(getUser());
    }

    private UserDTO getUser() {
        UserDTO u = new UserDTO();
        u.setAuthorities(new HashSet<>());
        return u;
    }

    @Test
    public void getAuthentication() {
        String url = authenticationService.getAuthentication();
        assertThat(url.contains("http://downloads.huilianyi.com/app/sit")).isTrue();
    }

    private ValueOperations<String, AuthenticationCode> getOperations() {

        ValueOperations<String, AuthenticationCode> rs = new ValueOperations<String, AuthenticationCode>() {
            @Override
            public void set(String s, AuthenticationCode authenticationCode) {

            }

            @Override
            public void set(String s, AuthenticationCode authenticationCode, long l, TimeUnit timeUnit) {

            }

            @Override
            public Boolean setIfAbsent(String s, AuthenticationCode authenticationCode) {
                return null;
            }

            @Override
            public void multiSet(Map<? extends String, ? extends AuthenticationCode> map) {

            }

            @Override
            public Boolean multiSetIfAbsent(Map<? extends String, ? extends AuthenticationCode> map) {
                return null;
            }

            @Override
            public AuthenticationCode get(Object o) {
                AuthenticationCode rs = new AuthenticationCode();
                rs.setUuid("abc");
                String[] s = {AuthenticationCode.WAITING, AuthenticationCode.LOGGED};
                rs.setStatus(s[new Random().nextInt(1)]);
                return rs;
            }

            @Override
            public AuthenticationCode getAndSet(String s, AuthenticationCode authenticationCode) {
                return null;
            }

            @Override
            public List<AuthenticationCode> multiGet(Collection<String> collection) {
                return null;
            }

            @Override
            public Long increment(String s, long l) {
                return null;
            }

            @Override
            public Double increment(String s, double v) {
                return null;
            }

            @Override
            public Integer append(String s, String s2) {
                return null;
            }

            @Override
            public String get(String s, long l, long l1) {
                return null;
            }

            @Override
            public void set(String s, AuthenticationCode authenticationCode, long l) {

            }

            @Override
            public Long size(String s) {
                return null;
            }

            @Override
            public Boolean setBit(String s, long l, boolean b) {
                return null;
            }

            @Override
            public Boolean getBit(String s, long l) {
                return null;
            }

            @Override
            public RedisOperations<String, AuthenticationCode> getOperations() {
                return null;
            }
        };
        return rs;
    }

    @Test
    public void getAuthenticationUUID() {
        String uuid = "abc";
        AuthenticationCode code = authenticationService.getAuthentication(uuid);
        assertThat(code.getUuid()).isEqualToIgnoringCase(uuid);
    }

    @Test
    public void preLogin() {
        String uuid = "abc";
        authenticationService.preLogin(uuid);
        assertThat(authenticationService.preLogin(uuid)).isTrue();
    }

    @Test
    public void login() {
        String uuid = "abc";
        SecurityContextHolder.getContext().setAuthentication(helper.oAuth2Authentication("ArtemisWeb", "13323454321"));
        boolean token = authenticationService.login(uuid);
        assertThat(token).isTrue();
    }*/
}