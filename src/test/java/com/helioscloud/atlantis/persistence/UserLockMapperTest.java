package com.helioscloud.atlantis.persistence;

import com.helioscloud.atlantis.AuthServiceSelectTest;
import com.helioscloud.atlantis.domain.enumeration.UserLockedEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UserLockMapperTest extends AuthServiceSelectTest {

    @Autowired
    private UserLockMapper userLockMapper;

    @Test
    public void selectUserLockMap() {
        List<Map<String, Object>> userLockMapList = userLockMapper.selectUserLockMap();
        assertThat(userLockMapList.isEmpty()).isFalse();
    }

    @Test
    public void updateUserLockStatus() {
        userLockMapper.updateUserLockStatus(975916080787894271L, UserLockedEnum.UNLOCKED.getID());
    }
}