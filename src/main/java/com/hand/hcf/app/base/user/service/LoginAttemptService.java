package com.hand.hcf.app.base.user.service;

import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.core.util.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by caixiang on 2017/7/20.
 */
@Service
@Transactional
public class LoginAttemptService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisHelper redisHelper;

    public void loginSucceeded(Long userId) {
        //若登录成功,清空当前的user_login_faied_times
        redisHelper.deleteByKey(Constants.LOGIN_ATTEMPT_PREFIX + userId);
        userService.unlockUser(userId);
    }

}
