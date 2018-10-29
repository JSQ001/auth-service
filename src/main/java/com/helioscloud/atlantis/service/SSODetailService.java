package com.helioscloud.atlantis.service;

import com.cloudhelios.atlantis.security.domain.PrincipalLite;
import com.helioscloud.atlantis.dto.UserDTO;
import com.helioscloud.atlantis.exception.UserNotActivatedException;
import com.helioscloud.atlantis.util.PrincipalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class SSODetailService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(SSODetailService.class);
    @Autowired
    private UserService userService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Authenticating email：{} cas start ", email);
        if (StringUtils.isEmpty(email)) {
            throw new UserNotActivatedException("email.is.empty");
        }
        UserDTO user = userService.findOneByContactEmail(email);
        if(user == null) {
            throw new UserNotActivatedException("user.not.found");
        }
        //用户状态检查
        userService.loginCommonCheck(user);
        return PrincipalBuilder.builder(user);
    }
}
