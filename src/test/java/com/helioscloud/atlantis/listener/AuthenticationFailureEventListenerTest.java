/*
package com.helioscloud.atlantis.listener;

import com.helioscloud.atlantis.AuthServerConfig;
import com.helioscloud.atlantis.security.LoginAttemptService;
import com.helioscloud.atlantis.security.UserLockService;
import com.helioscloud.atlantis.service.CompanyService;
import com.helioscloud.atlantis.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {AuthServerConfig.class, AuthenticationFailureEventListener.class,
        LoginAttemptService.class, UserService.class, CompanyService.class, UserLockService.class})
@ActiveProfiles("control")
public class AuthenticationFailureEventListenerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StringRedisTemplate template;

    @Test
    public void onApplicationEvent() throws Exception {
        given(template.boundValueOps(Mockito.anyString())).willReturn(getRs());

        mockMvc.perform(post("https://localhost/login")
                .param("username", "18587520392")
                .param("password", "wrong")
                .with(csrf()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/login?error"))
                .andDo(print());
    }

    private BoundValueOperations<String, String> getRs() {
        BoundValueOperations<String, String> rs = new BoundValueOperations<String, String>() {
            @Override
            public void set(String s) {

            }

            @Override
            public void set(String s, long l, TimeUnit timeUnit) {

            }

            @Override
            public Boolean setIfAbsent(String s) {
                return null;
            }

            @Override
            public String get() {
                return null;
            }

            @Override
            public String getAndSet(String s) {
                return null;
            }

            @Override
            public Long increment(long l) {
                return 10L;
            }

            @Override
            public Double increment(double v) {
                return Double.valueOf(10);
            }

            @Override
            public Integer append(String s) {
                return null;
            }

            @Override
            public String get(long l, long l1) {
                return null;
            }

            @Override
            public void set(String s, long l) {

            }

            @Override
            public Long size() {
                return null;
            }

            @Override
            public RedisOperations<String, String> getOperations() {
                return null;
            }

            @Override
            public String getKey() {
                return null;
            }

            @Override
            public DataType getType() {
                return null;
            }

            @Override
            public Long getExpire() {
                return null;
            }

            @Override
            public Boolean expire(long l, TimeUnit timeUnit) {
                return null;
            }

            @Override
            public Boolean expireAt(Date date) {
                return null;
            }

            @Override
            public Boolean persist() {
                return null;
            }

            @Override
            public void rename(String s) {

            }
        };
        return rs;
    }
}*/
