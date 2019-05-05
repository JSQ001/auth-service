

package com.hand.hcf.app.base.user.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.SqlHelper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.attachment.AttachmentService;
import com.hand.hcf.app.base.system.constant.CacheConstants;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.base.system.constant.SyncLockPrefix;
import com.hand.hcf.app.base.system.domain.PasswordHistory;
import com.hand.hcf.app.base.system.enums.DeviceVerificationStatus;
import com.hand.hcf.app.base.system.service.PasswordHistoryService;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.user.constant.AccountConstants;
import com.hand.hcf.app.base.user.domain.SMSToken;
import com.hand.hcf.app.base.user.domain.User;
import com.hand.hcf.app.base.user.domain.UserLoginBind;
import com.hand.hcf.app.base.user.dto.*;
import com.hand.hcf.app.base.user.enums.SMSTokenType;
import com.hand.hcf.app.base.user.enums.UserLockedEnum;
import com.hand.hcf.app.base.user.persistence.UserLoginBindMapper;
import com.hand.hcf.app.base.user.persistence.UserMapper;
import com.hand.hcf.app.base.userRole.domain.Role;
import com.hand.hcf.app.base.userRole.service.UserRoleService;
import com.hand.hcf.app.base.util.RespCode;
import com.hand.hcf.app.common.co.UserCO;
import com.hand.hcf.app.common.enums.UserStatusEnum;
import com.hand.hcf.app.core.domain.enumeration.LanguageEnum;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.exception.core.ObjectNotFoundException;
import com.hand.hcf.app.core.security.domain.Authority;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.core.util.RandomUtil;
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

@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = {CacheConstants.USER})
public class UserService extends BaseService<UserMapper, User> {

    @Autowired
    private MapperFacade mapper;

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserLoginBindMapper userLoginBindMapper;

    @Autowired
    private PasswordHistoryService passwordHistoryService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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


    public List<UserLoginBind> getUserLoginBindInfo(UUID userOid) {

        return userLoginBindMapper.selectList(new EntityWrapper<UserLoginBind>()
                .eq("user_oid", userOid)
                .eq("is_active", true));
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
        return pageDTOByQO(page, UserQO.builder()
                .keyword(keyword)
                .tenantId(tenantId)
                .isInactiveSearch(isInactiveSearch)
                .build());
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
            //取用户分配的角色集合
            listr = list.stream().map(user -> userDtoToUserRoleList(user)).collect(Collectors.toList());
        }
        return listr;
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
        return true;

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
            return this.selectOne(this.getWrapper().eq("user_oid", userOid.toString()));
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
            return userToUserDto(this.getByUserOid(userOid));
        }
    }

    public Set<Authority> findUserAuthorities(UUID userOid) {
        return baseMapper.listAuthorities(null, userOid);
    }

    public User saveUser(User user) {
        super.insertOrUpdate(user);
        return user;
    }

    public UserDTO saveUserDto(UserDTO userDTO, Long tenantId) {
        return userToUserDto(createOrUpdate(userDtoToUser(userDTO), tenantId));
    }

    public UserCO saveUserCO(UserCO user) {
        return userToUserCO(createOrUpdate(userCOToUser(user)
                , LoginInformationUtil.getCurrentTenantId()));
    }

    public List<UserCO> saveUserCOList(List<UserCO> users) {
        return users.stream().map(u -> saveUserCO(u)).collect(Collectors.toList());
    }


    public User getById(Long userId) {
        if (userId == null) {
            return null;
        }
        return this.selectById(userId);
    }


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


    public UserDTO getAccountInfo(String appVersion, String client, String clientVersion, boolean nativeApp) {
        User user = this.getByUserOid(LoginInformationUtil.getCurrentUserOid());
        UserDTO userDTO = userToUserDto(user);
        if (userDTO != null) {
            // 默认情况下，如果是原生app则开启，旧版app不开启
            userDTO.setDeviceValidate(DeviceVerificationStatus.DEFAULT.name().equals(user.getDeviceVerificationStatus())
                    ? nativeApp : DeviceVerificationStatus.OPENED.name().equals(user.getDeviceVerificationStatus()));
        }
        return userDTO;
    }


    public User userDtoToUser(UserDTO userDTO) {
        return mapper.map(userDTO, User.class);
    }

    public UserDTO userToUserDto(User user) {
        return mapper.map(user, UserDTO.class);
    }

    public UserRoleListDTO userDtoToUserRoleList(UserDTO userDTO) {
        UserRoleListDTO userRoleListDTO = new UserRoleListDTO();
        //为了取用户的全量角色，正常不会有一个用户超过1000角色
        Page pp = PageUtil.getPage(0, 1000);
        pp.setSearchCount(false);
        List<Role> listRole = userRoleService.getRolesByCond(userDTO.getId(),
                null, null, "ASSIGNED", pp);
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
    //@SyncLock(lockPrefix = SyncLockPrefix.USER_NEW, waiting = true, timeOut = 3000)
    public User createOrUpdate(User user, Long tenantId) {
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
            } else if (SqlHelper.retBool(baseMapper.checkLogin(user.getLogin(), null))) {
                throw new BizException(RespCode.USER_LOGIN_EXISTS);
            }

            if (StringUtils.isEmpty(user.getUserName())) {
                throw new BizException(RespCode.USER_NAME_NOT_NULL);
            }

            if (StringUtils.isEmpty(email)) {
                throw new BizException(RespCode.EMAIL_IS_NULL);
            } else if (SqlHelper.retBool(baseMapper.checkLogin(user.getEmail(), null))) {
                throw new BizException(RespCode.USER_EMAIL_EXISTS);
            }

            if (!StringUtils.isEmpty(mobile)) {
                if (SqlHelper.retBool(baseMapper.checkLogin(user.getMobile(), null))) {
                    throw new BizException(RespCode.USER_MOBILE_EXISTS);
                }
            }
            String password = user.getPassword();
            if (StringUtils.isEmpty(password)) {
                password = AccountConstants.DEFAULT_PASSWORD;
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
                if (SqlHelper.retBool(baseMapper.checkLogin(user.getEmail(), null))) {
                    throw new BizException(RespCode.USER_EMAIL_EXISTS);
                }
            }
            //修改手机号
            if (!StringUtils.isEmpty(mobile) && !mobile.equalsIgnoreCase(oldUser.getMobile())) {
                if (SqlHelper.retBool(baseMapper.checkLogin(user.getMobile(), null))) {
                    throw new BizException(RespCode.USER_MOBILE_EXISTS);
                }
            }
            //修改手机号码或者邮箱需要激活
            this.changeLogin(user, email, mobile, true);
            user.setVersionNumber(oldUser.getVersionNumber());
        }

        //保存至User
        user = this.saveUser(user);

        return user;
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

    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(PasswordUpdateDTO dto) {
        User user = this.selectById(LoginInformationUtil.getCurrentUserId());
        if (user == null) {
            throw new BizException(RespCode.USER_NOT_EXIST);
        }
        // 校验密码是否正确
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new BizException(RespCode.USER_OLD_PASS_WRONG);
        }
        if (!dto.getConfirmPassword().equals(dto.getNewPassword())) {
            throw new BizException(RespCode.USER_PASS_FORMAT_WRONG);
        }
        String passwordEncode = passwordEncoder.encode(dto.getNewPassword());
        user.setPasswordHash(passwordEncode);
        user.setPasswordHashLastUpdatedDate(ZonedDateTime.now());
        user.setResetPassword(false);
        //修改密码成功记录修改密码历史
        PasswordHistory passwordHistory = new PasswordHistory();
        passwordHistory.setUserOid(user.getUserOid());
        passwordHistory.setPasswordHash(passwordEncode);
        passwordHistoryService.insert(passwordHistory);
        return this.updateById(user);
    }
}