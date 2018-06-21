/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.service;

import com.helioscloud.atlantis.domain.CompanySecurity;
import com.helioscloud.atlantis.domain.PasswordHistory;
import com.helioscloud.atlantis.domain.UserLoginBind;
import com.helioscloud.atlantis.domain.enumeration.EmployeeStatusEnum;
import com.helioscloud.atlantis.domain.enumeration.UserLockedEnum;
import com.helioscloud.atlantis.dto.UserDTO;
import com.helioscloud.atlantis.exception.UserNotActivatedException;
import com.helioscloud.atlantis.persistence.PasswordHistoryMapper;
import com.helioscloud.atlantis.persistence.UserLoginBindMapper;
import com.helioscloud.atlantis.persistence.UserMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Project Name:auth-service
 * Package Name:com.helioscloud.atlantis.service
 * Date:2018/5/16
 * Create By:zongyun.zhou@hand-china.com
 */
@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserLoginBindMapper userLoginBindMapper;
    @Autowired
    PasswordHistoryMapper passwordHistoryMapper;
    @Autowired
    CompanyService companySecurityService;

    public UserDTO findOneByContactEmail(String email) {
        return userMapper.findOneByContactEmail(email);
    }

    public void loginCommonCheck(UserDTO user) {
        //1.用户是否被激活
        if (user == null || !user.isActivated()) {
            throw new UserNotActivatedException("user.not.activated");
        }
        //公共检查2.用户离职 3，用户锁定 4.密码过期
        //员工离职不允许登录
        if (user.getStatus().intValue() == EmployeeStatusEnum.LEAVED.getID()) {
            throw new UserNotActivatedException("user.was.leaved");
        }

        //check用户是否被锁定(锁定状态和锁定时间两个条件)
        if (user.getLockStatus().intValue() == UserLockedEnum.LOCKED.getID() && DateTime.now().isBefore(user.getLockDateDeadline())) {
            throw new UserNotActivatedException("user.is.locked");
        }
//        if (user.getLockStatus().intValue() == UserLockedEnum.LOCKED.getID() && DateTime.now().isBefore(user.getLockDateDeadline())) {
//            //锁定后添加剩余解锁时间说明
//            CompanySecurity tenantCompanySecurity = companySecurityService.getTenantCompanySecurity(user.getTenantId());
//            //标准解锁时间
//            int autoUnlockDuration = tenantCompanySecurity.getAutoUnlockDuration();
//            if (autoUnlockDuration == 0) {
//                autoUnlockDuration = 30;
//            }
//            long remainUnLockTime = 0L;
//            if (user.getLockDateDeadline() != null) {
//                remainUnLockTime = user.getLockDateDeadline().getMillis() - DateTime.now().getMillis();
//            }
//            Integer remainUnLockMinutes = 0;
//            if (remainUnLockTime > 0) {
//                remainUnLockMinutes = Math.toIntExact(remainUnLockTime / 1000 / 60);
//            }
//            throw new UserNotActivatedException("user.is.locked" + "#" + autoUnlockDuration + "#" + remainUnLockMinutes);
//        }

        //check用户的密码是否过期
        if (!this.isPasswordExpire(user, user.getTenantId())) {
            throw new UserNotActivatedException("user.password.expired");
        }

        if (user.isDeleted()) {
            throw new UserNotActivatedException("user.was.deleted");
        }
        //查询公司当前支持的语言
        if (StringUtils.isEmpty(user.getLanguage())) {
            user.setLanguage("zh_CN");
        }
    }

    private boolean isPasswordExpire(UserDTO user, Long tenantId) {
        List<PasswordHistory> historyList = passwordHistoryMapper.getPasswordHistoryOrderByCreateDate(user.getUserOID().toString());
        List<CompanySecurity> companySecuritys = companySecurityService.getTenantCompanySecurity(tenantId);
        if (!companySecuritys.isEmpty()) {
            CompanySecurity companySecurity = companySecuritys.get(0);
            //密码过期时间为-1，标识密码永不过期。
            if (companySecurity.getPasswordExpireDays() == 0) {
                return true;
            }
            if (historyList != null && historyList.size() > 0) {
                PasswordHistory history = historyList.get(0);
                DateTime expireDate = history.getCreatedDate().plusDays(companySecurity.getPasswordExpireDays());
                if (expireDate.isAfterNow() || expireDate.isEqualNow()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                DateTime passwordExpireDate = user.getCreatedDate().plusDays(companySecurity.getPasswordExpireDays());
                if (passwordExpireDate.isAfterNow() || passwordExpireDate.isEqualNow()) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
//            if (historyList != null && historyList.size() > 0) {
//                PasswordHistory history = historyList.get(0);
//                DateTime expireDate = history.getCreatedDate().plusDays(AccountRefactorConstants.DEFAULT_PASSWORD_EXPIRE_DAYS);
//                if (expireDate.isAfterNow() || expireDate.isEqualNow()) {
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//            else
//            {
//                DateTime passwordExpireDate = user.getCreatedDate().plusDays(AccountRefactorConstants.DEFAULT_PASSWORD_EXPIRE_DAYS);
//                if (passwordExpireDate.isAfterNow() || passwordExpireDate.isEqualNow()) {
//                    return true;
//                }else{
//                    return false;
//                }
//            }
        }
        return true;
    }

    public List<UserDTO> findUserByUserBind(String login) {
        return userMapper.findUserByUserBind(login);
    }

    public UserDTO findOneByLogin(String login) {
        return userMapper.findOneByLogin(login);
    }

    public UserDTO findOneByMobile(String mobile) {
        return userMapper.findOneByMobile(mobile);
    }

    public List<UserLoginBind> getUserLoginBindInfo(UUID userOid) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("user_oid", userOid);
        paramMap.put("is_active", true);
        paramMap.put("is_enabled", true);
        paramMap.put("is_deleted", false);
        userLoginBindMapper.selectById(1);
        List<UserLoginBind> list = userLoginBindMapper.selectByMap(paramMap);
        return list;
    }

    public UserDTO findByUserId(Long userId) {
        return userMapper.findOneByID(userId);
    }

    public void save(UserDTO unlockedUser) {
        userMapper.updateById(unlockedUser);
    }

    public UserDTO findOneByUserOID(UUID userOID) {
        return userMapper.findOneByUserOID(userOID);
    }

    public void updateUserLock(UserDTO unlockedUser) {
        userMapper.updateUserLock(unlockedUser);
    }
}
