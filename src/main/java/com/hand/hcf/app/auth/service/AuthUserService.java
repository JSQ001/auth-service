package com.hand.hcf.app.auth.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.auth.constant.CacheConstants;
import com.hand.hcf.app.auth.constant.Constants;
import com.hand.hcf.app.auth.domain.UserLock;
import com.hand.hcf.app.auth.dto.PasswordPolicyDTO;
import com.hand.hcf.app.auth.dto.UserDTO;
import com.hand.hcf.app.auth.dto.UserQO;
import com.hand.hcf.app.auth.enums.UserLockedEnum;
import com.hand.hcf.app.auth.persistence.AuthUserMapper;
import com.hand.hcf.app.auth.persistence.UserLockMapper;
import com.hand.hcf.app.common.enums.UserStatusEnum;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.UserNotActivatedException;
import com.hand.hcf.app.core.security.domain.PrincipalLite;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.service.MessageService;
import com.hand.hcf.app.core.util.RedisHelper;
import com.hand.hcf.app.core.web.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AuthUserService extends BaseService<AuthUserMapper, UserDTO> {

    //默认最大尝试登陆次数
    private static final int DEFAULT_MAX_LOGIN_ATTEMPT = 5;
    //自动解锁等待时间分钟数
    private static final int DEFAULT_UNLOCK_MINUTES = 30;

    //默认自动锁定持续时间,单位:second
    private static final int DEFAULT_AUTO_UNLOCK_DURATION = 3600;

    @Autowired
    private RedisHelper redisHelper;

    @Autowired
    private UserLockMapper userLockMapper;

    @Autowired
    private MessageService messageService;

    private UserDTO getDtoByQO(UserQO userQO) {
        List<UserDTO> users = baseMapper.listDtoByQO(userQO);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }


    private UserDTO getDtoById(Long userId) {
        return getDtoByQO(UserQO.builder().id(userId).build());
    }

    private UserDTO getDtoByUserOid(UUID userOid) {
        return getDtoByQO(UserQO.builder().userOid(userOid).build());
    }

    private UserDTO getDtoByLogin(String login) {
        return getDtoByQO(UserQO.builder().login(login).build());
    }

    private List<UserDTO> listDtoByLoginBind(String login) {
        return baseMapper.listDtoByQO(UserQO.builder().loginBind(login)
                .build());
    }


    private UserDTO getDtoByEmail(String email) {
        return getDtoByQO(UserQO.builder().email(email).build());
    }

    private UserDTO getDtoByMobile(String mobile) {
        return getDtoByQO(UserQO.builder().mobile(mobile).build());
    }


    private ZonedDateTime getLastPasswordDate(UUID userOid) {
        List<ZonedDateTime> days = baseMapper.listLastPasswordDate(userOid);
        if (!days.isEmpty()) {
            return days.get(0);
        }
        return null;
    }

    private PasswordPolicyDTO getPasswordPolicy(Long tenantId) {
        PasswordPolicyDTO passwordPolicyDTO = new PasswordPolicyDTO();
        passwordPolicyDTO.setPasswordExpireDays(0);
        passwordPolicyDTO.setPasswordAttemptTimes(DEFAULT_MAX_LOGIN_ATTEMPT);
        passwordPolicyDTO.setAutoUnlockDuration(DEFAULT_UNLOCK_MINUTES);
        return passwordPolicyDTO;
    }

    private Integer countLoginBind(UUID userOid) {
        return baseMapper.countLoginBind(userOid);
    }

    public Integer updateUserLockStatus(UserDTO userDTO) {
        return baseMapper.updateUserLockStatus(userDTO);
    }


    /**
     * 用户登录，只能是手机号或者邮箱，用户主键login为公司默认账户，不能登录
     *
     * @param login
     * @return
     */
    @Transactional
    public PrincipalLite loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        UserDTO user = null;
        List<UserDTO> users = listDtoByLoginBind(login);
        if (users != null && !users.isEmpty()) {
            if (users.size() > 1) {
                throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_LOGIN_MULTI_USER));

            } else {
                user = users.get(0);
            }
        }

        //==============如果绑定表没有数据，但是用户查询出来已经被激活==========================
        if (user == null) {
            boolean isEmailLogin = false;
            user = getDtoByLogin(login);
            if (user == null) {
                user = getDtoByMobile(login);
            }
            if (user == null) {
                user = getDtoByEmail(login);

                isEmailLogin = true;
            }

            //绑定登录的判断（用户被激活，但是有绑定记录，证明该为新登录）
            if (user != null && user.getActivated()) {
                Integer userLoginBinds = countLoginBind(user.getUserOid());
                if (userLoginBinds != null && userLoginBinds > 0) {
                    if (isEmailLogin) {
                        throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_EMAIL_NOT_BIND));
                    }
                    throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_MOBILE_NOT_BIND));
                }


            } else {
                //新用户登录，手机号没有激活（邮箱激活的场景提示）
                if (user == null) {

                    UserDTO userQueryByPhone = getDtoByMobile(login);
                    if (userQueryByPhone != null && userQueryByPhone.getActivated()) {
                        Integer userLoginBinds = countLoginBind(userQueryByPhone.getUserOid());
                        if (userLoginBinds != null && userLoginBinds > 0) {
                            throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_MOBILE_NOT_BIND));
                        }
                    } else if (userQueryByPhone != null && !userQueryByPhone.getActivated()) {
                        throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_NOT_FOUND));
                    }
                }
            }

        }
        //用户状态检查
        if (user == null) {
            throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_NOT_ACTIVATED));
        }
        loginCommonCheck(user);
        return getPrincipal(user);
    }


    public PrincipalLite loadUserByUserOid(UUID userOid) {
        UserDTO u = getDtoByUserOid(userOid);
        if (u == null) {
            throw new UsernameNotFoundException(messageService.getMessageFromSource(Constants.USER_NOT_FOUND));
        }
        //1.用户是否被激活
        if (!u.getActivated()) {
            throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_NOT_ACTIVATED));
        }
        //公共检查2.用户离职 3，用户锁定 4.密码过期
        loginCommonCheck(u);
        return getPrincipal(u);
    }

    public PrincipalLite loadUserByEmail(String email) {
        // 判断手机号码是否存在
        UserDTO u = getDtoByEmail(email);

        if (u == null) {
            throw new UsernameNotFoundException(messageService.getMessageFromSource(Constants.USER_NOT_FOUND));
        }
        //1.用户是否被激活
        if (!u.getActivated()) {
            throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_NOT_ACTIVATED));
        }
        //公共检查2.用户离职 3，用户锁定 4.密码过期
        loginCommonCheck(u);
        return getPrincipal(u);
    }

    private void loginCommonCheck(UserDTO user) {
        //1.用户是否被激活
        if (user == null || !user.getActivated()) {
            throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_NOT_FOUND));
        }
        //公共检查2.用户离职 3，用户锁定 4.密码过期
        //员工离职不允许登录
        if (user.getStatus().intValue() == UserStatusEnum.INVALID.getId()) {
            throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_WAS_INVALID));
        }

        //check用户是否被锁定(锁定状态和锁定时间两个条件)
        if (user.getLockStatus().intValue() == UserLockedEnum.LOCKED.getId() && ZonedDateTime.now().isBefore(user.getLockDateDeadline())) {
            throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_WAS_LOCKED));
        }

        //check用户的密码是否过期
        if (!this.isPasswordExpire(user, user.getTenantId())) {
            throw new UserNotActivatedException(messageService.getMessageFromSource(Constants.USER_PASSWORD_EXPIRED));
        }

        //查询公司当前支持的语言
        if (StringUtils.isEmpty(user.getLanguage())) {
            user.setLanguage(LanguageEnum.ZH_CN.getKey());
        }
    }


    private boolean isPasswordExpire(UserDTO user, Long tenantId) {

        PasswordPolicyDTO passwordPolicyDTO = getPasswordPolicy(tenantId);

        //密码过期时间为-1，标识密码永不过期。
        if (passwordPolicyDTO.getPasswordExpireDays() == 0) {
            return true;
        }
        ZonedDateTime expireDate = getLastPasswordDate(user.getUserOid());
        if (expireDate != null) {
            expireDate = expireDate.plusDays(passwordPolicyDTO.getPasswordExpireDays());
            return (expireDate.isAfter(ZonedDateTime.now()) || expireDate.isEqual(ZonedDateTime.now()));
        } else {
            ZonedDateTime passwordExpireDate = user.getCreatedDate().plusDays(passwordPolicyDTO.getPasswordExpireDays());
            return (passwordExpireDate.isAfter(ZonedDateTime.now()) || passwordExpireDate.isEqual(ZonedDateTime.now()));
        }
    }


    public void loginFailed(String login, HttpServletRequest request) {
        //1.根据username获取user
        UserDTO user = getDtoByMobile(login);
        if (user == null) {
            user = getDtoByEmail(login);
            if (user == null) {
                log.debug("Can't find user name/email equals {}", login);
                return;
            }
        }

        //2.根据companyId拿到SecurityPolicy
        PasswordPolicyDTO passwordPolicyDTO = getPasswordPolicy(user.getTenantId());

        //3.拿到LoginAttempt
        Integer maxAttemptTimes = passwordPolicyDTO.getPasswordAttemptTimes() == 0 ? DEFAULT_MAX_LOGIN_ATTEMPT : passwordPolicyDTO.getPasswordAttemptTimes();

        //4.增加失败次数:1,最大到maxAttemptTimes,如果小于则直接返回,等于则lockUser
        if (maxAttemptTimes.longValue() > redisHelper.increment(CacheConstants.LOGIN_ATTEMPT_PREFIX + user.getId(), 1)) {
            return;
        }

        //5.construct userlock
        UserLock userLock = UserLock.builder()
                .userId(user.getId())
                .clientIp(request.getHeader("User-Agent"))
                .userAgent(HttpRequestUtil.getRealRemoteAddr(request))
                .build();

        //6.lock user
        lockUser(userLock, passwordPolicyDTO.getAutoUnlockDuration() == 0 ? DEFAULT_UNLOCK_MINUTES : passwordPolicyDTO.getAutoUnlockDuration());
    }


    @Transactional
    public UserLock lockUser(UserLock userLock, int unlockMinutes) {
        //初始化UserLock
        userLock.setDeleted(false);
        userLock.setLockedDate(ZonedDateTime.now());
        userLockMapper.insert(userLock);

        UserDTO unlockedUser = getDtoById(userLock.getUserId());
        if (unlockedUser == null || (unlockedUser.getLockStatus().intValue() == UserLockedEnum.LOCKED.getId() && ZonedDateTime.now().isBefore(unlockedUser.getLockDateDeadline()))) {
            throw new BizException("user.not.found.or.locked");
        }

        updateUserLockStatus(
                UserDTO.builder()
                        .id(userLock.getUserId())
                        .lockStatus(UserLockedEnum.LOCKED.getId())
                        .passwordAttampt(0)
                        .lockDateDeadline(ZonedDateTime.now().plusMinutes(unlockMinutes))
                        .build());
        //锁定用户时,清除登陆失败次数
        redisHelper.deleteByKey(CacheConstants.LOGIN_ATTEMPT_PREFIX + userLock.getUserId());
        return userLock;
    }

    @Transactional
    public void unlockUser(Long userId) {
        updateUserLockStatus(
                UserDTO.builder()
                        .id(userId)
                        .lockStatus(UserLockedEnum.UNLOCKED.getId())
                        .passwordAttampt(0)
                        .lockDateDeadline(null)
                        .build());
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
            if (lockedDate.getTime() + autoLockDuration * 60 * 1000 < System.currentTimeMillis()) {
                Long userId = (Long) l.get("user_id");
                unlockUser(userId);
            }
        });
    }

    //预先设置api,方法暂未用到
    public UserLock getAvailableUserLock(Long userId) {
        return userLockMapper.selectList(new EntityWrapper<UserLock>().eq("user_id", userId)).get(0);
    }

    public PrincipalLite getPrincipal(UserDTO user) {
        PrincipalLite principalLite = new PrincipalLite();
        principalLite.setId(user.getId());
        principalLite.setUserOid(user.getUserOid());
        principalLite.setLogin(user.getLogin());
        principalLite.setPassword(user.getPassword());
        principalLite.setActivated(user.getActivated());
        principalLite.setStatus(user.getStatus());
        principalLite.setLanguage(user.getLanguage());
        principalLite.setTenantId(user.getTenantId());
        principalLite.setUserName(user.getUserName());
        principalLite.setEmail(user.getEmail());
        principalLite.setMobile(user.getMobile());
        return principalLite;
    }

    /**
     * 根据oauth客户端获取用户信息
     *
     * @param clientId 客户端id
     * @return PrincipalLite
     */
    public PrincipalLite getUserByOauthClientId(String clientId) {
        List<UserDTO> userDTOS = baseMapper.getUserByOauthClientId(clientId);
        if (CollectionUtils.isEmpty(userDTOS)) {
            return null;
        }
        return getPrincipal(userDTOS.get(0));
    }
}
