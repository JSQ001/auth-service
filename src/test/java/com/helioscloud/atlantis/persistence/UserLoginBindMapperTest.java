package com.helioscloud.atlantis.persistence;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.domain.UserLoginBind;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class UserLoginBindMapperTest extends AuthServiceSelectTest {

    @Autowired
    private UserLoginBindMapper userLoginBindMapper;

    @Test
    public void test_query() {
        String s = "890768427022106624";
        UserLoginBind userLoginBind = userLoginBindMapper.testMapper(s);
        assertThat(userLoginBind.getId().toString()).isEqualTo(s);
    }
}