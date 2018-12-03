

package com.hand.hcf.app.base.service;


import com.hand.hcf.app.base.domain.UserLoginBind;
import com.hand.hcf.app.base.util.PrincipalBuilder;
import com.hand.hcf.app.base.dto.UserDTO;
import com.hand.hcf.app.base.exception.UserNotActivatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);
    @Autowired
    private AuthUserService userService;

    /**
     * 用户登录，只能是手机号或者邮箱，用户主键login为公司默认账户，不能登录
     *
     * @param login
     * @return
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        UserDTO userDTO = null;
        List<UserDTO> users = userService.findUserByUserBind(login);
        if (users != null && users.size() >= 1) {
            if (users.size() == 1) {
                userDTO = users.get(0);
            } else {
                throw new UserNotActivatedException("related.multi.user");
            }
        }

        //========================兼容原始登录接口========================================
        //==============如果绑定表没有数据，但是用户查询出来已经被激活==========================
        if (userDTO == null) {
            boolean isEmailLogin = false;
            userDTO = userService.findOneByLogin(login);
            if (userDTO == null) {
                userDTO = userService.findOneByMobile(login);
            }
            if (userDTO == null) {
                userDTO = userService.findOneByContactEmail(login);
                if (userDTO == null) {
                    throw new UsernameNotFoundException("user.not.found");
                }
                isEmailLogin = true;
            }

            //绑定登录的判断（用户被激活，但是有绑定记录，证明该为新登录）
            if (userDTO != null && userDTO.isActivated()) {
                List<UserLoginBind> userLoginBinds = userService.getUserLoginBindInfo(userDTO.getUserOID());
                if (userLoginBinds != null && userLoginBinds.size() > 0) {
                    if (isEmailLogin) {
                        throw new UserNotActivatedException("email.not.bind");
                    }
                    throw new UserNotActivatedException("mobile.not.bind");
                }

            } else {
                //新用户登录，手机号没有激活（邮箱激活的场景提示）
                if (userDTO == null) {
//                    Optional<User> userQueryByPhone = userRepository.findOneByContactPhonesNumberAndContactPhonesIsPrimary(login,true);
                    UserDTO userQueryByPhone = userService.findOneByLogin(login);
                    if (userQueryByPhone != null && userQueryByPhone.isActivated()) {
                        List<UserLoginBind> userLoginBinds = userService.getUserLoginBindInfo(userQueryByPhone.getUserOID());
                        if (userLoginBinds != null && userLoginBinds.size() > 0) {
                            throw new UserNotActivatedException("mobile.not.bind");
                        }
                    } else if (userQueryByPhone != null && !userQueryByPhone.isActivated()) {
                        throw new UserNotActivatedException("user.not.activated");
                    }
                }
            }
        }
        //用户状态检查
        if (userDTO == null) {
            throw new UserNotActivatedException("user.not.activated");
        }
        userService.loginCommonCheck(userDTO);
        return PrincipalBuilder.builder(userDTO);
    }
}
