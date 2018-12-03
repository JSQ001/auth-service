package com.hand.hcf.app.base.service;

import com.hand.hcf.app.base.util.PrincipalBuilder;
import com.hand.hcf.app.base.dto.UserDTO;
import com.hand.hcf.app.base.exception.UserNotActivatedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class AppDetailService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(AppDetailService.class);
    @Autowired
    private AuthUserService userService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String userOID) throws UsernameNotFoundException {
        log.info("Authenticating userOID：{} cas start ", userOID);
        if (StringUtils.isEmpty(userOID)) {
            throw new UserNotActivatedException("userOID.is.empty");
        }
        UserDTO user = userService.findOneByUserOID(UUID.fromString(userOID));
        if(user == null) {
            throw new UserNotActivatedException("user.not.found");
        }
        //用户状态检查
        userService.loginCommonCheck(user);
        return PrincipalBuilder.builder(user);
    }
}
