package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.exception.UserNotActivatedException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DingTalkServiceImplTest extends AuthServiceSelectTest {

    @Autowired
    private DingTalkServiceImpl DingTalkServiceImpl;

    @Test
    public void loadDingTalkUserByCodeAndCorpId() {
        String code = "sefc";
        String corpId = "ding9276faa506e4795c35c2f4657eb6378f";

        assertThatThrownBy(() -> DingTalkServiceImpl.loadDingTalkUserByCodeAndCorpId(code, corpId))
                .isInstanceOf(UserNotActivatedException.class)
                .hasMessage("user.not.bind");
    }
}