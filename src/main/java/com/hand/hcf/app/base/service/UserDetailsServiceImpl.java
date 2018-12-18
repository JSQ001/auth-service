

package com.hand.hcf.app.base.service;


import com.hand.hcf.app.client.system.AuthClient;
import com.hand.hcf.core.util.FeignUtil;
import com.hand.hcf.core.web.exception.ExceptionDetail;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Slf4j
@Component("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AuthClient authClient;

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
        try {
            return authClient.loadUserByUsername(login);
        } catch (HystrixBadRequestException e) {
            ExceptionDetail detail= FeignUtil.getExceptionDetail(e);
            throw new UsernameNotFoundException(detail.getBizErrorCode());
        }
    }
}
