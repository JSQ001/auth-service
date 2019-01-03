package com.hand.hcf.app.base.service;

import com.hand.hcf.core.exception.core.UserNotActivatedException;
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
    private UserService  userService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String userOid) throws UsernameNotFoundException {
        log.info("Authenticating userOID：{} cas start ", userOid);
        if (StringUtils.isEmpty(userOid)) {
            throw new UserNotActivatedException("userOID.is.empty");
        }

        return  userService.loadUserByUserOid(UUID.fromString(userOid));
    }
}
