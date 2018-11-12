package com.hand.hcf.app.base.security;

import com.hand.hcf.app.base.domain.CompanySecurity;
import com.hand.hcf.app.base.domain.UserLock;
import com.hand.hcf.app.base.dto.UserDTO;
import com.hand.hcf.app.base.service.CompanyService;
import com.hand.hcf.app.base.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by caixiang on 2017/7/20.
 */
@Service
@Transactional
public class LoginAttemptService {
    private static final Logger logger = LoggerFactory.getLogger(LoginAttemptService.class);

    //默认最大尝试登陆次数
    private static final int defaultMaxLoginAttempt = 5;
    private static final int defaultUnLockMinutes = 30;
    private static final String LOGIN_ATTEMPT_PREFIX = "LOGIN_ATTEMPT_PREFIX_";
    @Autowired
    private UserService userService;
    @Autowired
    private UserLockService userLockService;
    @Autowired
    CompanyService companyService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

//    public void loginSucceeded(Long userId) {
//        //若登录成功,清空当前的user_login_faied_times
//        redisHelper.deleteByKey(Constants.LOGIN_ATTEMPT_PREFIX + userId);
//        userLockService.unlockUser(userId);
//    }

    public void loginFailed(String username, HttpServletRequest httpRequest) {
        //1.根据username获取user
        UserDTO user = userService.findOneByMobile(username);
        if (user == null) {
            user = userService.findOneByContactEmail(username);
            if (user == null) {
                logger.debug("Can't find user name/email equals {}", username);
                return;
            }
        }

        //2.根据companyId拿到SecurityPolicy
        List<CompanySecurity> companySecuritys = companyService.getTenantCompanySecurity(user.getTenantId());
        if (companySecuritys.isEmpty()) {
            logger.debug("Can't find companySecurity tenantId equals {}", user.getTenantId());
            return;
        }

        //3.拿到LoginAttempt
        CompanySecurity companySecurity = companySecuritys.get(0);
        Integer maxAttemptTimes = companySecurity == null || companySecurity.getPasswordAttemptTimes() == 0 ? defaultMaxLoginAttempt : companySecurity.getPasswordAttemptTimes();

        //4.增加失败次数:1,最大到maxAttemptTimes,如果小于则直接返回,等于则lockUser
        user.setPasswordAttempt(user.getPasswordAttempt() + 1);
        if (maxAttemptTimes.longValue() > stringRedisTemplate.boundValueOps(LOGIN_ATTEMPT_PREFIX + user.getId()).increment(1)) {
            return;
        }
        //5.construct userlock
        UserLock userLock = UserLock.builder()
            .userId(user.getId())
            .clientIp(getClientIP(httpRequest))
            .userAgent(httpRequest.getHeader("User-Agent"))
            .build();

        //6.lock user
        userLockService.lockUser(userLock, companySecurity == null || companySecurity.getAutoUnlockDuration() == 0 ? defaultUnLockMinutes : companySecurity.getAutoUnlockDuration());
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
