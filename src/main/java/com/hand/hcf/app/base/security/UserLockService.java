package com.hand.hcf.app.base.security;

import com.hand.hcf.core.exception.core.ValidationError;
import com.hand.hcf.core.exception.core.ValidationException;
import com.hand.hcf.app.base.domain.UserLock;
import com.hand.hcf.app.base.domain.enumeration.UserLockedEnum;
import com.hand.hcf.app.base.dto.UserDTO;
import com.hand.hcf.app.base.persistence.UserLockMapper;
import com.hand.hcf.app.base.service.AuthUserService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by caixiang on 2017/7/19.
 */
@Service
public class UserLockService {

    //默认自动锁定持续时间,单位:second
    private static final int DEFAULT_AUTO_UNLOCK_DURATION = 3600;
    private static final String LOGIN_ATTEMPT_PREFIX = "LOGIN_ATTEMPT_PREFIX_";

    @Autowired
    private UserLockMapper userLockMapper;
    @Autowired
    private AuthUserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Transactional
    public UserLock lockUser(UserLock userLock, int unlockMinutes) {
        //初始化UserLock
        userLock.setIsDeleted(false);
        userLock.setLockedDate(DateTime.now());
        userLockMapper.insert(userLock);

        UserDTO unlockedUser = userService.findByUserId(userLock.getUserId());
        if(unlockedUser == null || (unlockedUser.getLockStatus().intValue() == UserLockedEnum.LOCKED.getID() && DateTime.now().isBefore(unlockedUser.getLockDateDeadline()))){
            throw new ValidationException(new ValidationError("User","user.not.found.or.locked"));
        }
        unlockedUser.setLockStatus(UserLockedEnum.LOCKED.getID());
        unlockedUser.setLockDateDeadline(DateTime.now().plusMinutes(unlockMinutes));
        unlockedUser.setPasswordAttempt(0);
        userService.updateUserLock(unlockedUser);
        //锁定用户时,清除登陆失败次数
        stringRedisTemplate.delete(LOGIN_ATTEMPT_PREFIX + userLock.getUserId());
        return userLock;
    }

    @Transactional
    public void unlockUser(Long userId) {
        userLockMapper.updateUserLockStatus(userId,UserLockedEnum.UNLOCKED.getID());
    }

//    @Scheduled(cron = "0 0/3 * * * ?")//使用登陆时间来判定用户锁定时间
    public void autoUnLockUser() {

        //1.look up all undeleted userlock info records
       List<Map<String, Object>> userLockMapList = userLockMapper.selectUserLockMap();

        //2.meet the requirement to invoke unlockUser method
        userLockMapList.forEach(l -> {
            //锁定分钟数
            int autoLockDuration = l.get("auto_unlock_duration") == null ? DEFAULT_AUTO_UNLOCK_DURATION : (Integer) l.get("auto_unlock_duration");
            Date lockedDate = (Date) l.get("locked_date");

            //锁定时刻时间+锁定持续时间 vs 当前时间
            if (lockedDate.getTime() + autoLockDuration * 60*1000 < System.currentTimeMillis()) {
                Long userId = (Long) l.get("user_id");
                unlockUser(userId);
            }
        });
    }

    //预先设置api,方法暂未用到
    public UserLock getAvailableUserLock(Long userId) {
        return userLockMapper.selectOne(UserLock.builder().userId(userId).isDeleted(false).build());
    }
}
