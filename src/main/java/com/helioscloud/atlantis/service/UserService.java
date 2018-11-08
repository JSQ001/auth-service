/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cloudhelios.atlantis.client.dto.UserSummaryInfoDTO;
import com.cloudhelios.atlantis.util.PageUtil;
import com.helioscloud.atlantis.constant.CacheConstants;
import com.helioscloud.atlantis.domain.CompanySecurity;
import com.helioscloud.atlantis.domain.PasswordHistory;
import com.helioscloud.atlantis.domain.Role;
import com.helioscloud.atlantis.domain.UserLoginBind;
import com.helioscloud.atlantis.domain.enumeration.EmployeeStatusEnum;
import com.helioscloud.atlantis.domain.enumeration.UserLockedEnum;
import com.helioscloud.atlantis.dto.UserDTO;
import com.helioscloud.atlantis.exception.UserNotActivatedException;
import com.helioscloud.atlantis.persistence.PasswordHistoryMapper;
import com.helioscloud.atlantis.persistence.UserLoginBindMapper;
import com.helioscloud.atlantis.persistence.UserMapper;
import org.apache.ibatis.annotations.Param;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Pageable;
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
    private UserRoleService userRoleService;
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
        //  userLoginBindMapper.selectById(1);
      /*  List<UserLoginBind> list=    userLoginBindMapper.selectList(new EntityWrapper<UserLoginBind>()
                .eq("user_oid",userOid)
                .eq("is_active",true)
                .eq("is_enabled",true)
                .eq("is_deleted",false)
        );*/
        // List<UserLoginBind> list=    userLoginBindMapper.findOneByUserOID(userOid);
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

    /**
     * 用户切换语言
     * @param user
     */
    @CacheEvict(key="#user.id.toString()")
    public void updateUserLanguage(UserDTO user){
        userMapper.updateUserLanguage(user.getId(),user.getLanguage());
    }

    /**
     * 获取用户列表
     *
     * @param tenantId     必填，取租户下的所有用户
     * @param setOfBooksId 如果填了，取帐套下的用户
     * @param companyId    如果填了，则取公司下的用户
     * @return 按full_name排序
     * 20180829修改：hec-18 【角色权限重构】用户列表接口增加当前用户对应的角色的集合
     */
    public List<UserDTO> getUserListByCond(Long tenantId,
                                           Long setOfBooksId,
                                           Long companyId,
                                           String login,
                                           String fullName,
                                           String mobile,
                                           String email,
                                           Page page) {
      List<UserDTO> list = userMapper.getUserListByCond(tenantId, setOfBooksId, companyId, login, fullName, mobile, email, page);
        if (list != null && list.size() > 0) {
            Page pp = PageUtil.getPage(0,1000);//为了取用户的全量角色，正常不会有一个用户超过1000角色
            //取用户分配的角色集合
            list.stream().forEach(user -> {
                List<Role> listRole = userRoleService.getRolesByCond(user.getId(),null,null,"ASSIGNED",pp);
                user.setRoleList(listRole);
            });
        }
        return list;
    }


    public List<UserDTO> findByCondition(@Param("keyword") String keyword,
                                         @Param("tenantId") Long tenantId,
                                         @Param("departmentOIDs") List<UUID> departmentOIDs,
                                         @Param("status") String status,
                                         @Param("companyOIDs") List<UUID> companyOIDs,
                                         Page page){
//        Pagination page=new Pagination(pageable.getPageNumber(),pageable.getPageSize());
        if(!StringUtils.isEmpty(status) && "all".equals(status)){
            status = null;
        }
        List<UserDTO> list = userMapper.findByCondition(keyword,tenantId,departmentOIDs,status,companyOIDs,page);
        if (list != null && list.size() > 0) {
            Page pp = PageUtil.getPage(0,1000);//为了取用户的全量角色，正常不会有一个用户超过1000角色
            //取用户分配的角色集合
            list.stream().forEach(user -> {
                List<Role> listRole = userRoleService.getRolesByCond(user.getId(),null,null,"ASSIGNED",pp);
                user.setRoleList(listRole);
            });
        }
        return list;
    }



}
