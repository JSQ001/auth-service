package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.exception.UserNotActivatedException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HaimaServiceTest extends AuthServiceSelectTest {

    @Autowired
    private HaimaService haimaService;

    @Test
    public void loadHaimaUserByClientIdAndCode() {

        assertThatThrownBy(() -> haimaService.loadHaimaUserByClientIdAndCode("a", "b"))
                .isInstanceOf(UserNotActivatedException.class)
                .hasMessage("user.not.bind");
    }
}