

package com.hand.hcf.app.base.user.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.base.system.constant.CacheConstants;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.base.system.constant.SyncLockPrefix;
import com.hand.hcf.app.base.system.domain.PasswordHistory;
import com.hand.hcf.app.base.system.enums.DeviceVerificationStatus;
import com.hand.hcf.app.base.system.service.MailService;
import com.hand.hcf.app.base.system.service.MessageTranslationService;
import com.hand.hcf.app.base.system.service.PasswordHistoryService;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.user.constant.AccountConstants;
import com.hand.hcf.app.base.user.domain.SMSToken;
import com.hand.hcf.app.base.user.domain.User;
import com.hand.hcf.app.base.user.domain.UserLoginBind;
import com.hand.hcf.app.base.user.dto.PasswordRuleDTO;
import com.hand.hcf.app.base.user.dto.UserDTO;
import com.hand.hcf.app.base.user.dto.UserQO;
import com.hand.hcf.app.base.user.dto.UserRoleListDTO;
import com.hand.hcf.app.base.user.enums.SMSTokenType;
import com.hand.hcf.app.base.user.enums.UserLockedEnum;
import com.hand.hcf.app.base.user.persistence.UserLoginBindMapper;
import com.hand.hcf.app.base.user.persistence.UserMapper;
import com.hand.hcf.app.base.userRole.domain.Role;
import com.hand.hcf.app.base.userRole.service.UserRoleService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.base.user.UserCO;
import com.hand.hcf.app.base.user.enums.UserStatusEnum;
import com.hand.hcf.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.core.redisLock.annotations.SyncLock;
import com.hand.hcf.core.security.domain.Authority;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.hand.hcf.core.util.PageUtil;
import com.hand.hcf.core.util.RandomUtil;
import com.hand.hcf.core.util.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.hand.hcf.app.base.user.constant.AccountConstants.*;

@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = {CacheConstants.USER})
public class UserService extends BaseService<UserMapper, User> {

    @Autowired
    MapperFacade mapper;

    @Autowired
    private UserCacheableService userCacheableService;

    @Autowired
    UserAuthorityService userAuthorityService;

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    UserLoginBindMapper userLoginBindMapper;

    @Autowired
    PasswordHistoryService passwordHistoryService;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    SMSTokenService smsTokenService;

    @Autowired
    MessageTranslationService messageTranslationService;

    @Autowired
    MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    RedisHelper redisHelper;

    private final static String[] ruleRegex = new String[4];

    static {
        ruleRegex[0] = regexLowerCase;
        ruleRegex[1] = regexUpperCase;
        ruleRegex[2] = regenNum;
        ruleRegex[3] = regexSpecialChar;
    }

    /**
     * listByQO tenant by userOid
     *
     * @param currentUserOid
     * @return
     */
    public Tenant findCurrentTenantByUSerOid(UUID currentUserOid) {
        Tenant tenant = baseMapper.getCurrentTenantByUserOid(currentUserOid);

        return tenant;
    }


    public User getByQO(UserQO userQO) {
        List<User> users = baseMapper.listByQO(userQO);
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }


    public List<User> pageByQO(Page page, UserQO userQO) {
        return baseMapper.listByQO(page, userQO);
    }


    public List<UserDTO> pageDTOByQO(Page page, UserQO userQO) {
        return mapper.mapAsList(baseMapper.listByQO(page, userQO), UserDTO.class);
    }


    public List<UserDTO> listDTOByQO(UserQO userQO) {
        return mapper.mapAsList(baseMapper.listByQO(userQO), UserDTO.class);
    }

    public List<UserDTO> listDTOByOids(List<UUID> userOids) {
        return listDTOByQO(UserQO.builder().userOids(userOids).build());
    }

    public UserDTO getDTOByQO(UserQO userQO) {
        List<UserDTO> users = listDTOByQO(userQO);
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    public UserDTO getDTOByLogin(String login) {
        return getDTOByQO(UserQO.builder().login(login).build());
    }

    public void save(UserDTO user) {
        updateById(userDtoToUser(user));
    }

    public List<UserLoginBind> getUserLoginBindInfo(UUID userOid) {

        return userLoginBindMapper.selectList(new EntityWrapper<UserLoginBind>()
                .eq("user_oid", userOid)
                .eq("is_active", true));
    }

    public Optional<User> getByMobile(String mobile) {
        return Optional.ofNullable(getByQO(UserQO.builder().mobile(mobile).build()));
    }


    /**
     * 用户切换语言
     *
     * @param user
     */
    @CacheEvict(key = "#user.id.toString()")
    public void updateUserLanguage(UserDTO user) {
        save(user);
    }


    /**
     * 返回用户的语言
     *
     * @param userOid
     * @return
     */
    public String getUserLanguage(UUID userOid) {
        try {
            User user = getByUserOid(userOid);
            return StringUtils.isEmpty(user.getLanguage()) ? Constants.DEFAULT_LANGUAGE : user.getLanguage();
        } catch (Exception e) {
            return Constants.DEFAULT_LANGUAGE;
        }
    }

    /**
     * 获取用户列表
     *
     * @param tenantId 必填，取租户下的所有用户
     * @return 按full_name排序
     * 20180829修改：hec-18 【角色权限重构】用户列表接口增加当前用户对应的角色的集合
     */
    public List<UserRoleListDTO> getUserListByCond(Long tenantId,
                                                   String login,
                                                   String userName,
                                                   String mobile,
                                                   String email,
                                                   Page page) {
        List<UserDTO> list = pageDTOByQO(page, UserQO.builder()
                .tenantId(tenantId)
                .login(login)
                .userName(userName)
                .mobile(mobile)
                .email(email).build());
        List<UserRoleListDTO> listr = null;
        if (list != null && list.size() > 0) {

            //取用户分配的角色集合
            listr = list.stream().map(user ->
                    userDtoToUserRoleList(user)
            ).collect(Collectors.toList());
        }
        return listr;
    }


    /**
     * 获取用户列表 分页 (员工管理)
     *
     * @param tenantId 必填，取租户下的所有用户
     * @param keyword  如果填了，根据条件取帐套下的用户
     * @return 按employee_id, created_date排序
     */
    public List<UserDTO> listByCondition(String keyword,
                                         Long tenantId,
                                         Boolean isInactiveSearch,
                                         Page page) {

        List<UserDTO> list = pageDTOByQO(page, UserQO.builder()
                .keyword(keyword)
                .tenantId(tenantId)
                .isInactiveSearch(isInactiveSearch)
                .build());
        return list;
    }


    /**
     * 获取用户列表 分页 (员工管理)
     *
     * @param tenantId 必填，取租户下的所有用户
     * @param keyword  如果填了，根据条件取帐套下的用户
     * @param status   如果填了，则根据状态取部门下的用户
     * @return 按employee_id, created_date排序
     */
    public List<UserRoleListDTO> listWithRoleByCondition(String keyword,
                                                         Long tenantId,
                                                         String status,
                                                         Boolean isInactiveSearch,
                                                         Page page) {

        List<UserDTO> list = listByCondition(keyword, tenantId, isInactiveSearch, page);
        List<UserRoleListDTO> listr = null;
        if (list != null && list.size() > 0) {
            Page pp = PageUtil.getPage(0, 1000);//为了取用户的全量角色，正常不会有一个用户超过1000角色
            //取用户分配的角色集合
            listr = list.stream().map(user -> userDtoToUserRoleList(user)).collect(Collectors.toList());
        }
        return listr;
    }


    public SMSToken getActivationToken(String mobile) {
        return this.getByMobile(mobile)
                .map(user -> {
                    if (user.getActivated()) {
                        throw new BizException(RespCode.USER_ALREADY_ACTIVATED);
                    }
                    List<SMSToken> tokens = smsTokenService.findByToUserAndTypeID(mobile, SMSTokenType.ACTIVATE_USER.getId());
                    SMSToken token = null;
                    if (tokens != null) {
                        for (SMSToken t : tokens) {
                            if (t.getExpireTime().isAfter(ZonedDateTime.now())) {
                                token = t;
                                break;
                            }
                        }
                    }
                    if (token == null) {
                        token = new SMSToken(user.getUserOid(), SMSTokenType.ACTIVATE_USER.getId(), RandomUtil.generateActivationKey(), ZonedDateTime.now().plusMinutes(10), mobile);
                    } else if (token.getExpireTime().minusMinutes(9).isBefore(ZonedDateTime.now())) {
                        token.setExpireTime(ZonedDateTime.now().plusMinutes(10));
                    } else {
                        throw new BizException(RespCode.USER_SEND_SMS_TOO_MANY);
                    }
                    smsTokenService.insertOrUpdate(token);
                    return token;
                }).orElseThrow(() -> new BizException(RespCode.USER_NOT_EXIST));
    }

    public SMSToken getActivationTokenByEmail(String email) {
        return this.getByEmail(email)
                .map(user -> {
                    if (user.getActivated()) {
                        throw new BizException(RespCode.USER_ALREADY_ACTIVATED);
                    }
                    List<SMSToken> tokens = smsTokenService.findByToUserAndTypeID(email, SMSTokenType.ACTIVATE_USER.getId());
                    SMSToken token = null;

                    if (tokens != null) {
                        for (SMSToken t : tokens) {
                            if (t.getExpireTime().isAfter(ZonedDateTime.now())) {
                                token = t;
                                break;
                            }
                        }
                    }
                    if (token == null) {
                        token = new SMSToken(user.getUserOid(), SMSTokenType.ACTIVATE_USER.getId(), RandomUtil.generateActivationKey(), ZonedDateTime.now().plusMinutes(30), email);
                    } else if (token.getExpireTime().minusMinutes(29).isBefore(ZonedDateTime.now())) {
                        token.setTokenValue(RandomUtil.generateActivationKey());
                        token.setExpireTime(ZonedDateTime.now().plusMinutes(30));
                    } else {
                        throw new BizException(RespCode.USER_SEND_SMS_TOO_MANY);
                    }
                    smsTokenService.insertOrUpdate(token);
                    return token;
                }).orElseThrow(() -> new BizException(RespCode.USER_EMAIL_NOT_EXISTS));
    }

    public SMSToken getRestPasswordTokenByEmail(String email) {
        return this.getByEmail(email)
                .map(user -> {
                    List<SMSToken> tokens = smsTokenService.findByToUserAndTypeID(email, SMSTokenType.RESET_PASSWORD.getId());
                    SMSToken token = null;

                    if (tokens != null) {
                        for (SMSToken t : tokens) {
                            if (t.getExpireTime().isAfter(ZonedDateTime.now())) {
                                token = t;
                                break;
                            }
                        }
                    }

                    if (token == null) {
                        token = new SMSToken(user.getUserOid(), SMSTokenType.RESET_PASSWORD.getId(), RandomUtil.generateActivationKey(), ZonedDateTime.now().plusMinutes(30), email);
                    } else if (token.getExpireTime().minusMinutes(29).isBefore(ZonedDateTime.now())) {
                        token.setTokenValue(RandomUtil.generateActivationKey());
                        token.setExpireTime(ZonedDateTime.now().plusMinutes(30));
                    } else {
                        throw new BizException(RespCode.USER_SEND_SMS_TOO_MANY);
                    }
                    smsTokenService.insertOrUpdate(token);
                    return token;
                }).orElseThrow(() -> new BizException(RespCode.USER_EMAIL_NOT_EXISTS));
    }

    public SMSToken getRegisterToken(String mobile) {
        List<SMSToken> tokens = smsTokenService.findByToUserAndTypeID(mobile, SMSTokenType.REGISTER_COMPANY.getId());
        SMSToken token = null;

        if (tokens != null) {
            for (SMSToken t : tokens) {
                if (t.getExpireTime().isAfter(ZonedDateTime.now())) {
                    token = t;
                    break;
                }
            }
        }

        if (token == null) {
            token = new SMSToken(null, SMSTokenType.REGISTER_COMPANY.getId(), RandomUtil.generateSMSToken(), ZonedDateTime.now().plusMinutes(10), mobile);
        } else if (token.getExpireTime().minusMinutes(9).isBefore(ZonedDateTime.now())) {
            token.setExpireTime(ZonedDateTime.now().plusMinutes(10));
        } else {
            throw new BizException(RespCode.USER_SEND_SMS_TOO_MANY);
        }
        smsTokenService.insertOrUpdate(token);
        return token;
    }

    public void activateRegistration(String mobile, String tokenValue, String newPassword) {
        SMSToken token = smsTokenService.findByTokenValueAndToUserAndTypeID(tokenValue, mobile, SMSTokenType.ACTIVATE_USER.getId());
        if (token == null) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_NOT_EXISTS);
        } else if (token.getExpireTime().isBefore(ZonedDateTime.now())) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_EXPIRED);
        }

        User user = this.getByUserOid(token.getUserOid());
        if (user == null) {
            throw new ObjectNotFoundException(User.class, token.getUserOid());
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setActivated(true);
        user.setActivatedDate(ZonedDateTime.now());
        this.saveUser(user);
        smsTokenService.deleteById(token);

    }

    public Boolean updateStatus(Long userId, UserStatusEnum status) {
        User user = getById(userId);
        if (user == null) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        if (!user.getTenantId().equals(LoginInformationUtil.getCurrentTenantId())) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        user.setStatus(status);
        return updateById(user);
    }

    /**
     * 更新用户语言
     *
     * @param language
     */
    public boolean updateUserLanguage(String language, String login) {
        LanguageEnum languageEnum = LanguageEnum.parse(language);
        if (languageEnum == null) {
            throw new BizException("Unable to match the language：" + language);
        }
        this.getByLogin(login).ifPresent(u -> {
            u.setLanguage(languageEnum.getKey());
            saveUser(u);
            log.debug("Changed Information for User: {}", u);
        });
        // baseTokenService.updateOauthAccessTokenById(language);

        return true;

    }

    public void changePassword(String oldPassword, String newPassword) {
        User user = this.getByUserOid(LoginInformationUtil.getCurrentUserOid());
        if (user != null) {
            if (!user.getActivated()) {
                throw new BizException(RespCode.USER_NOT_ACTIVATE);
            }
            if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                throw new BizException(RespCode.USER_OLD_PASS_WRONG);
            }
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            user.setPasswordHashLastUpdatedDate(ZonedDateTime.now());
            //重置密码后，设置强制重置为false
            user.setResetPassword(false);
            saveUser(user);
            log.debug("Changed password for User: {}", user);
            this.evictUserCache(user);//失效缓存
        }

    }

    public void changePasswordNew(User currentUser, String oldPassword, String newPassword) {
        this.changePassword(currentUser, oldPassword, newPassword);
    }

    /**
     * @param currentUser
     * @param oldPassword
     * @param newPassword
     */
    public void changePassword(User currentUser, String oldPassword, String newPassword) {
        //可选不输入原密码
        if (!StringUtils.isEmpty(oldPassword)) {
            if (!passwordEncoder.matches(oldPassword, currentUser.getPasswordHash())) {
                throw new BizException(RespCode.USER_OLD_PASS_WRONG);
            }
        }
        String passwordEncode = passwordEncoder.encode(newPassword);
        currentUser.setPasswordHash(passwordEncode);
        currentUser.setPasswordHashLastUpdatedDate(ZonedDateTime.now());
        currentUser.setResetPassword(false);
        saveUser(currentUser);
        reloadUserCache(currentUser);
        //修改密码成功记录修改密码历史
        PasswordHistory passwordHistory = new PasswordHistory();
        passwordHistory.setUserOid(currentUser.getUserOid());
        passwordHistory.setPasswordHash(passwordEncode);
        passwordHistoryService.insert(passwordHistory);
        log.debug("Changed password for User: {}", currentUser);

    }

    public boolean checkPasswordFormat(String newPassword, PasswordRuleDTO passwordRuleDTO) {
        log.info("check new password format by param passwordRule = {}", passwordRuleDTO);
        String passwordRule = AccountConstants.DEFAULT_PASSWORD_RULE;
        if (passwordRuleDTO != null) {
            passwordRule = passwordRuleDTO.getPasswordRule();
        }
        String[] checkRule = passwordRule.split("");
        boolean checkOK = true;
        for (int i = 0; i < checkRule.length; i++) {
            if (AccountConstants.INCLUDE_FLAG_CHAR.equals(checkRule[i])) {
                if (!newPassword.matches(ruleRegex[i])) {
                    checkOK = false;
                    break;
                }
            }
        }
        return checkOK;
    }

    public boolean checkPasswordLength(String newPassword, PasswordRuleDTO passwordRuleDTO) {
        log.info("start to check password length,and this rule is CompanySecurity={}", passwordRuleDTO);
        int lengthMix = AccountConstants.DEFAULT_PASSWORD_LENGTH_MIN;
        int lengthMax = AccountConstants.DEFAULT_PASSWORD_LENGTH_MAX;
        if (passwordRuleDTO != null) {
            lengthMix = passwordRuleDTO.getPasswordLengthMin();
            lengthMax = passwordRuleDTO.getPasswordLengthMax();
        }
        if (!StringUtils.isEmpty(newPassword) && lengthMix <= newPassword.length() && lengthMax >= newPassword.length()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkNewPasswordRepeatTimes(String newPassword, PasswordRuleDTO passwordRuleDTO, UUID userOId) {
        if (passwordRuleDTO.getPasswordRepeatTimes() <= 0) {
            return true;
        }
        //由于原始密码被encode，每次加密的密文不一致，所以不能以加密的密文查询
        List<PasswordHistory> passwordHistorys = passwordHistoryService.listPasswordHistory(userOId);
        if (passwordHistorys == null || passwordHistorys.size() <= 0) {
            return true;
        } else {
            boolean isOK = true;
            if (passwordHistorys.size() >= passwordRuleDTO.getPasswordRepeatTimes()) {
                for (int i = 0; i < passwordRuleDTO.getPasswordRepeatTimes(); i++) {
                    if (passwordEncoder.matches(newPassword, passwordHistorys.get(i).getPasswordHash())) {
                        isOK = false;
                        break;
                    }
                }

            } else {
                for (int i = 0; i < passwordHistorys.size(); i++) {
                    if (passwordEncoder.matches(newPassword, passwordHistorys.get(i).getPasswordHash())) {
                        isOK = false;
                        break;
                    }
                }
            }
            return isOK;
        }
    }

    public void changePasswordCheckNew(Long tenantId, UUID userOid, String newPassword) {

        this.changePasswordCheck(tenantId, userOid, newPassword);
    }

    public PasswordRuleDTO getPasswordRule(Long tenantId) {
        return new PasswordRuleDTO();
    }

    public void changePasswordCheck(Long tenantId, UUID userOid, String newPassword) {
        PasswordRuleDTO passwordRule = getPasswordRule(tenantId);

        //验证密码长度
        if (!this.checkPasswordLength(newPassword, passwordRule)) {
            throw new BizException(RespCode.USER_PASS_LENGTH_WRONG);
        }
        //验证密码格式
        if (!this.checkPasswordFormat(newPassword, passwordRule)) {
            throw new BizException(RespCode.USER_PASS_FORMAT_WRONG);
        }
        //验证密码和之前的密码相同的次数，不能超过设置的密码的重复次数
        if (!this.checkNewPasswordRepeatTimes(newPassword, passwordRule, userOid)) {
            throw new BizException(RespCode.USER_PASS_REPAT_WRONG);
        }
    }

    /**
     * 公共调用修改密码(已加密)
     *
     * @param companyOid
     * @param login
     * @param newPassword
     */
    public void implementChangePassword(UUID companyOid, UUID userOid, String login, String newPassword) {

        Optional<User> user = Optional.empty();
        String msg = "";
        if (userOid != null) {
            user = Optional.of(this.getByUserOid(userOid));
            msg = userOid.toString();
        } else if (!StringUtils.isEmpty(login)) {
            user = this.getByLogin(login);
            msg = login;
        }
        if (!user.isPresent()) {
            throw new ObjectNotFoundException(User.class, msg);
        }
        //重置密码时，将用户锁定状态重置
        user.ifPresent(u -> {
            u.setPasswordHash(newPassword);
            u.setPasswordHashLastUpdatedDate(ZonedDateTime.now());
            u.setPasswordAttempt(0);
            u.setLockStatus(UserLockedEnum.UNLOCKED.getId());
            saveUser(u);
        });
    }


    public SMSToken getResetPasswordToken(String mobile) {
        return this.getByMobile(mobile)
                .map(user -> {
                    if (!user.getActivated()) {
                        throw new BizException(RespCode.USER_NOT_ACTIVATE);
                    }
                    PasswordRuleDTO passwordRule = getPasswordRule(LoginInformationUtil.getCurrentTenantId());
                    boolean allowReset = Constants.FALSE;
                    if (passwordRule != null) {

                        //allowReset为true时表示不允许重置密码
                        allowReset = passwordRule.getAllowReset();

                    }
                    if (allowReset) {
                        throw new BizException(RespCode.USER_PASS_NOT_ALLOW_RESET);
                    }
                    List<SMSToken> tokens = smsTokenService.findByToUserAndTypeID(mobile, SMSTokenType.RESET_PASSWORD.getId());
                    SMSToken token = null;

                    if (tokens != null) {
                        for (SMSToken t : tokens) {
                            if (t.getExpireTime().isAfter(ZonedDateTime.now())) {
                                token = t;
                                break;
                            }
                        }
                    }

                    if (token == null) {
                        token = new SMSToken(user.getUserOid(), SMSTokenType.RESET_PASSWORD.getId(), RandomUtil.generateActivationKey(), ZonedDateTime.now().plusMinutes(10), mobile);
                    } else if (token.getExpireTime().minusMinutes(9).isBefore(ZonedDateTime.now())) {
                        token.setExpireTime(ZonedDateTime.now().plusMinutes(10));
                    } else {
                        throw new BizException(RespCode.USER_SEND_SMS_TOO_MANY);
                    }
                    smsTokenService.insertOrUpdate(token);
                    return token;
                }).orElseThrow(() -> new BizException(RespCode.USER_NOT_EXIST));
    }

    public void checkSmsResetPassword(String mobile, String tokenValue) {
        log.debug("checkSmsResetPassword token {}", tokenValue);
        SMSToken token = smsTokenService.findByTokenValueAndToUserAndTypeID(tokenValue, mobile, SMSTokenType.RESET_PASSWORD.getId());
        if (token == null) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_NOT_EXISTS);
        } else if (token.getExpireTime().isBefore(ZonedDateTime.now())) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_EXPIRED);
        }
    }

    public void checkSmsActivationToken(String mobile, String tokenValue) {
        log.debug("checkSmsActivationToken token{}", tokenValue);
        SMSToken token = smsTokenService.findByTokenValueAndToUserAndTypeID(tokenValue, mobile, SMSTokenType.ACTIVATE_USER.getId());
        if (token == null) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_NOT_EXISTS);
        } else if (token.getExpireTime().isBefore(ZonedDateTime.now())) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_EXPIRED);
        }
    }


    public void smsResetPassword(String mobile, String tokenValue, String newPassword) {
        log.debug("smsResetPassword user password for reset smsToken {}", tokenValue);
        SMSToken token = smsTokenService.findByTokenValueAndToUserAndTypeID(tokenValue, mobile, SMSTokenType.RESET_PASSWORD.getId());
        if (token == null) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_NOT_EXISTS);
        } else if (token.getExpireTime().isBefore(ZonedDateTime.now())) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_EXPIRED);
        }

        User user = this.getByUserOid(token.getUserOid());
        if (user == null) {
            throw new ObjectNotFoundException(User.class, token.getUserOid());
        }
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encryptedPassword);
        user.setLockStatus(UserLockedEnum.UNLOCKED.getId());
        user.setPasswordAttempt(0);
        saveUser(user);
        smsTokenService.deleteById(token);
    }

    public void emailResetPassword(String email, String tokenValue, String newPassword) {
        log.debug("smsResetPassword user password for reset smsToken {}", tokenValue);
        Optional<User> user = this.getByEmail(email);
        SMSToken token;
        if (user.isPresent()) {
            token = smsTokenService.findByTokenValueAndToUserAndTypeID(tokenValue, email, SMSTokenType.RESET_PASSWORD.getId());
        } else {
            throw new ObjectNotFoundException(User.class, "user.not.found");
        }

        if (token == null) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_NOT_EXISTS);
        } else if (token.getExpireTime().isBefore(ZonedDateTime.now())) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_EXPIRED);
        }
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.get().setPasswordHash(encryptedPassword);
        user.get().setLockStatus(UserLockedEnum.UNLOCKED.getId());
        user.get().setPasswordAttempt(0);
        saveUser(user.get());
    }

    public SMSToken getAddMobileTokenV2(String mobile) {
        return this.getAddMobileToken(mobile);
    }

    public SMSToken getAddMobileToken(String mobile) {
        Optional<User> optional = this.getByMobile(mobile);
        if (optional.isPresent()) {
            throw new BizException(RespCode.USER_MOBILE_EXISTS);
        }
        List<SMSToken> tokens = smsTokenService.findByToUserAndTypeID(mobile, SMSTokenType.ADD_MOBILE.getId());
        SMSToken token = null;

        if (tokens != null) {
            for (SMSToken t : tokens) {
                if (t.getExpireTime().isAfter(ZonedDateTime.now())) {
                    token = t;
                    break;
                }
            }
        }

        if (token == null) {
            token = new SMSToken(LoginInformationUtil.getCurrentUserOid(), SMSTokenType.ADD_MOBILE.getId(), RandomUtil.generateActivationKey(), ZonedDateTime.now().plusMinutes(10), mobile);
        } else if (token.getExpireTime().minusMinutes(9).isBefore(ZonedDateTime.now())) {
            token.setExpireTime(ZonedDateTime.now().plusMinutes(10));
        } else {
            throw new BizException(RespCode.USER_SEND_SMS_TOO_MANY);
        }
        smsTokenService.insertOrUpdate(token);
        return token;
    }

    public void checkSmsAddMobileTokenV2(String mobile, String tokenValue) {
        this.checkSmsActivationToken(mobile, tokenValue);
    }

    public void checkSmsAddMobileToken(String mobile, String tokenValue) {
        log.debug("checkSmsAddMobileToken token{}", tokenValue);
        SMSToken token = smsTokenService.findByTokenValueAndToUserAndTypeID(tokenValue, mobile, SMSTokenType.ADD_MOBILE.getId());
        if (token == null) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_NOT_EXISTS);
        } else if (token.getExpireTime().isBefore(ZonedDateTime.now())) {
            throw new BizException(RespCode.USER_ACTIVATION_TOKEN_EXPIRED);
        }
    }


    public void inviteUser(List<UUID> userOids, Long currentUserId) {
        List<User> userList = listByUserOidIn(userOids);
        noticeUsers(userList, currentUserId);
    }

    private int getNoticeType() {
        return AccountConstants.NOTICE_TYPE_EMAIL;
    }

    private void noticeUsers(List<User> userList, Long currentUserId) {
        Long tenantId = null;
        for (User user : userList) {
            int noticeType = getNoticeType();
            if (AccountConstants.NOTICE_TYPE_EMAIL == noticeType) {
                mailService.sendInvitationEmail(user.getEmail(), user.getUserName(), null, new Locale(user.getLanguage()));
            }
            if (AccountConstants.NOTICE_TYPE_MOBILE == noticeType) {
                //sendNoticeUserSms(user);
            }
            if (AccountConstants.NOTICE_TYPE_EMAIL_AND_MOBILE == noticeType) {
                mailService.sendInvitationEmail(user.getEmail(), user.getUserName(), null, new Locale(user.getLanguage()));
                //sendNoticeUserSms(user);
            }
            if (tenantId == null) {
                tenantId = user.getTenantId();
            }
        }

    }

    /**
     * 根据用户Oid查询用户
     *
     * @param userOid：用户Oid
     * @return
     */
    public User getByUserOid(UUID userOid) {
        if (userOid == null) {
            return null;
        } else {
            return userCacheableService.getByUserOid(userOid);
        }
    }

    /**
     * 根据用户Oid查询用户
     *
     * @param userOid：用户Oid
     * @return
     */
    public UserDTO getDtoByUserOid(UUID userOid) {
        if (userOid == null) {
            return null;
        } else {
            return userToUserDto(userCacheableService.getByUserOid(userOid));
        }
    }


    public List<User> listByUserOidIn(List<UUID> userOids) {
        return selectList(new EntityWrapper<User>().in("user_oid", userOids));


    }


    /**
     * 获取用户主语言
     *
     * @param user
     * @return
     */
    public String getLanguageByUser(User user) {
        String language = Constants.DEFAULT_LANGUAGE;
        if (user != null) {
            language = user.getLanguage();
        }
        return language;
    }


    public Set<Authority> findUserAuthorities(UUID userOid) {
        return baseMapper.listAuthorities(null, userOid);
    }

    public User saveUser(User user) {
        super.insertOrUpdate(user);
        this.evictUserCache(user);
        return user;
    }

    public UserDTO saveUserDto(UserDTO userDTO, Long currUserId, Long tenantId) {
        return userToUserDto(createOrUpdate(userDtoToUser(userDTO), currUserId, tenantId));
    }

    public UserCO saveUserCO(UserCO user) {
        return userToUserCO(createOrUpdate(userCOToUser(user)
                , LoginInformationUtil.getCurrentUserId()
                , LoginInformationUtil.getCurrentTenantId()));
    }


    public List<UserCO> saveUserCOList(List<UserCO> users) {
        return users.stream().map(u -> saveUserCO(u)).collect(Collectors.toList());
    }

    //    @Cacheable(key="#email")
    public Optional<User> getByEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new BizException(RespCode.EMAIL_IS_NULL);
        }
        return Optional.ofNullable(getByQO(UserQO.builder().email(email).build()));
    }


    public void evictUserCache(User user) {
        if (user.getId() != null) {
            userCacheableService.evictCacheUserByUserId(user);
        }
        if (user.getUserOid() != null) {
            userCacheableService.evictCacheUserByUserOid(user);
        }

        if (user.getEmail() != null) {
            userCacheableService.evictCacheUserByEmail(user.getEmail());
        }
    }

    @Transactional
    public User save(User user) {
        super.insertOrUpdate(user);
        user = getByUserOid(user.getUserOid());
        this.evictUserCache(user);
        return user;
    }

    public User getById(Long userId) {
        if (userId == null) {
            return null;
        }
        return userCacheableService.getById(userId);
    }

    public Boolean manualUnlockUser(Long userId) {
        unlockUser(userId);
        return Boolean.TRUE;
    }

    @Transactional
    public void unlockUser(Long userId) {
        baseMapper.updateUserLockStatus(userId, UserLockedEnum.UNLOCKED.getId());
    }


    /**
     * 根据账号查询用户
     *
     * @param login：账号
     * @return
     */
    public Optional<User> getByLogin(String login) {
        return Optional.ofNullable(getByQO(UserQO.builder().login(login).build()));

    }

    /**
     * 根据用户Oid查询未删除用户
     *
     * @param userOid：用户Oid
     * @return
     */
    public User getByUserOidAndIsDeletedFalse(UUID userOid) {
        return getByQO(UserQO.builder().userOid(userOid).build());

    }


    public Map<String, Object> getMobileStatusByUserOid(UUID userOid) {
        User user = getByUserOid(userOid);
        if (user == null) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        return getMobileStatus(user.getMobile());
    }

    public Map<String, Object> getMobileStatus(String mobile) {

        Map<String, Object> retMap = new HashMap<>();
        if (StringUtils.isEmpty(mobile)) {
            retMap.put("status", "not.set");
            return retMap;
        }

        List<UserLoginBind> userLoginBinds = userLoginBindMapper.selectList(
                new EntityWrapper<UserLoginBind>()
                        .eq("login", mobile)
                        .eq("is_active", true));
        if (userLoginBinds == null || userLoginBinds.size() <= 0) {
            retMap.put("status", "not.validate");
            retMap.put("mobile", mobile);
            return retMap;
        }
        retMap.put("status", "activated");
        retMap.put("mobile", mobile);
        return retMap;
    }

    public UserDTO getAccountInfo(String appVersion, String client, String clientVersion, boolean nativeApp) {
        User user = userCacheableService.getByUserOid(LoginInformationUtil.getCurrentUserOid());

        UserDTO userDTO = userToUserDto(user);

        if (userDTO != null) {
            // 默认情况下，如果是原生app则开启，旧版app不开启
            userDTO.setDeviceValidate(DeviceVerificationStatus.DEFAULT.name().equals(user.getDeviceVerificationStatus()) ? nativeApp : DeviceVerificationStatus.OPENED.name().equals(user.getDeviceVerificationStatus()));
        }
        return userDTO;
    }


    public void reloadUserCache(User user) {
        if (user.getId() != null) {
            userCacheableService.reloadCacheUserByUserId(user);
        }
        if (user.getUserOid() != null) {
            userCacheableService.reloadCacheUserByUserOid(user);
        }
    }


    public User userDtoToUser(UserDTO userDTO) {
        return mapper.map(userDTO, User.class);
    }

    public UserDTO userToUserDto(User user) {
        return mapper.map(user, UserDTO.class);
    }

    public UserRoleListDTO userDtoToUserRoleList(UserDTO userDTO) {

        UserRoleListDTO userRoleListDTO = new UserRoleListDTO();
        Page pp = PageUtil.getPage(0, 1000);//为了取用户的全量角色，正常不会有一个用户超过1000角色
        List<Role> listRole = userRoleService.getRolesByCond(userDTO.getId(), null, null, "ASSIGNED", pp);
        mapper.map(userDTO, userRoleListDTO);
        userRoleListDTO.setRoleList(listRole);

        return userRoleListDTO;
    }

    private UserLoginBind getUserLoginBindByLogin(String login, UUID userOID) {
        Map<String, Object> param = new HashMap<>();
        param.put("login", login);
        param.put("user_oid", userOID.toString());
        param.put("is_active", true);
        // Modified qingsheng.chen 2018/03/14
        param.put("enabled", true);
        param.put("deleted", false);
        // End modified
        List<UserLoginBind> userLoginBindList = userLoginBindMapper.selectByMap(param);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userLoginBindList)) {
            return userLoginBindList.get(0);
        } else {
            return null;
        }
    }

    private boolean checkUserInactiveStatus(UUID userOID) {
        List<UserLoginBind> userLoginBindList = getUserLoginBindInfo(userOID);
        //有部分项目是没有绑定记录的，那么这种是不能设置为未激活的
        List<UserLoginBind> userLoginBindListWithAllStatus = getUserLoginBindByUserOID(userOID, true);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(userLoginBindListWithAllStatus) && org.apache.commons.collections.CollectionUtils.isEmpty(userLoginBindList)) {
            return true;
        } else {
            return false;
        }
    }

    private List<UserLoginBind> getUserLoginBindByUserOID(UUID userOID, boolean withAllStatus) {
        Map<String, Object> param = new HashMap<>();
        param.put("user_oid", userOID);
        if (!withAllStatus) {
            param.put("is_active", true);
            param.put("enabled", true);
            param.put("deleted", false);
        }
        List<UserLoginBind> userLoginBindList = userLoginBindMapper.selectByMap(param);
        return userLoginBindList;
    }

    public User changeLogin(User user, String email, String mobile, boolean clearOldMobile) {

        String oldEmail = user.getEmail();
        if (!StringUtils.isEmpty(email)) {
            log.info("email:{},oldemail:{}", email, oldEmail);
            if (!email.equalsIgnoreCase(oldEmail)) {

                UserLoginBind userLoginBind = getUserLoginBindByLogin(oldEmail, user.getUserOid());
                if (userLoginBind != null) {
                    // Modified qingsheng.chen 2018/03/14
                    userLoginBind.setDeleted(true);
                    userLoginBind.setEnabled(false);
                    userLoginBind.setLastUpdatedDate(ZonedDateTime.now());
                    userLoginBindMapper.updateById(userLoginBind);
                    log.info("update useroid:{}", userLoginBind.getUserOid());
                    // End modified
                }

                if (this.checkUserInactiveStatus(user.getUserOid())) {
                    log.info("将用户修改成未激活，原email:{},新email{}", oldEmail, email);
                    user.setActivated(false);
                }
            }
        }
        return user;
    }


    /**
     * 新增或编辑员工
     *
     * @param user
     * @return userDTO
     */
    @Transactional
    @SyncLock(lockPrefix = SyncLockPrefix.USER_NEW, waiting = true, timeOut = 3000)
    public User createOrUpdate(User user, Long currentUserId, Long tenantId) {


        String email = user.getEmail();
        String mobile = user.getMobile();


        if (user.getId() == null) {
            //insert
            user.setUserOid(UUID.randomUUID());
            // 默认为激活
            user.setActivated(true);
            user.setLanguage(LoginInformationUtil.getCurrentLanguage());
            user.setTenantId(tenantId);

            if (StringUtils.isEmpty(user.getLogin())) {
                throw new BizException(RespCode.USER_LOGIN_NOT_NULL);
            } else if (verifyLoginExsits(user)) {
                throw new BizException(RespCode.USER_LOGIN_EXISTS);
            }

            if (StringUtils.isEmpty(user.getUserName())) {
                throw new BizException(RespCode.USER_NAME_NOT_NULL);
            }

            if (StringUtils.isEmpty(email)) {
                throw new BizException(RespCode.EMAIL_IS_NULL);
            } else if (verifyEmailExsits(user)) {
                throw new BizException(RespCode.USER_EMAIL_EXISTS);
            }

            if (!StringUtils.isEmpty(mobile)) {
                if (verifyMobileExsits(user)) {
                    throw new BizException(RespCode.USER_MOBILE_EXISTS);
                }
            }
            String password = user.getPassword();
            if (StringUtils.isEmpty(password)) {
                password = DEFAULT_PASSWORD;
            }
            user.setPasswordHash(passwordEncoder.encode(password));

        } else {
            //update
            User oldUser = getById(user.getId());
            if (oldUser == null) {
                throw new BizException(RespCode.USER_NOT_EXIST);
            }
            if (!oldUser.getTenantId().equals(tenantId)) {
                throw new BizException(RespCode.USER_NOT_EXIST);
            }

            //设置激活日期
            if (user.getActivated() && !oldUser.getActivated()) {
                user.setActivatedDate(ZonedDateTime.now());
            }

            //修改邮箱
            if (!StringUtils.isEmpty(email) && !email.equalsIgnoreCase(oldUser.getEmail())) {
                if (verifyEmailExsits(user)) {
                    throw new BizException(RespCode.USER_EMAIL_EXISTS);
                }
                userCacheableService.evictCacheUserByEmail(oldUser.getEmail());
            }
            //修改手机号
            if (!StringUtils.isEmpty(mobile) && !mobile.equalsIgnoreCase(oldUser.getMobile())) {
                if (verifyMobileExsits(user)) {
                    throw new BizException(RespCode.USER_MOBILE_EXISTS);
                }
            }
            //修改手机号码或者邮箱需要激活
            this.changeLogin(user, email, mobile, true);
        }

        //保存至User
        user = this.saveUser(user);

        return user;
    }


    public Boolean verifyLoginExsits(User user) {
        if (selectCount(new EntityWrapper<User>()
                .eq("login", user.getLogin())
                .ne("id", user.getId())
        ) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean verifyEmailExsits(User user) {
        if (selectCount(new EntityWrapper<User>()
                .eq("email", user.getEmail())
                .ne("id", user.getId())
        ) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean verifyMobileExsits(User user) {
        if (selectCount(new EntityWrapper<User>()
                .eq("mobile", user.getMobile())
                .ne("id", user.getId())
        ) > 0) {
            return true;
        } else {
            return false;
        }
    }

    public UserCO userToUserCO(User user) {
        UserCO userCO = new UserCO();
        mapper.map(user, userCO);
        userCO.setPhoneNumber(user.getMobile());
        return userCO;
    }


    public User userCOToUser(UserCO userCO) {
        User user = new User();
        mapper.map(userCO, user);

        user.setMobile(userCO.getPhoneNumber());
        return user;
    }


}