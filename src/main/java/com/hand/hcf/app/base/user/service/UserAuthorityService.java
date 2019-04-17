package com.hand.hcf.app.base.user.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.base.system.constant.Constants;
import com.hand.hcf.app.base.user.domain.UserAuthority;
import com.hand.hcf.app.base.user.persistence.AuthorityMapper;
import com.hand.hcf.app.base.user.persistence.UserAuthorityMapper;
import com.hand.hcf.app.core.security.AuthoritiesConstants;
import com.hand.hcf.app.core.security.domain.Authority;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.core.util.LoginInformationUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserAuthorityService extends BaseService<UserAuthorityMapper, UserAuthority> {
    @Autowired
    private UserAuthorityMapper userAuthorityMapper;

    @Autowired
    AuthorityMapper authorityMapper;

    @Autowired
    private UserService userService;

    /**
     * 根据用户id查询用户权限信息
     *
     * @param userId：用户id
     * @return
     */
    public Set<UserAuthority> findByUserId(Long userId) {
        return userAuthorityMapper.findByUserId(userId);
    }

    public Set<Authority> findByNames(List<String> names) {
        return new HashSet(authorityMapper.selectList(new EntityWrapper<Authority>().in("name", names)));
    }

    public boolean saveAuthorities(Long userId, Set<Authority> authorities) {
        return super.insertOrUpdateBatch(authorities.stream().map(a -> {
            UserAuthority userAuthority = new UserAuthority();
            userAuthority.setUserId(userId);
            userAuthority.setAuthorityName(a.getName());
            return userAuthority;
        }).collect(Collectors.toList()));
    }

    public boolean deleteAuthorities(Long userId, Set<Authority> authorities) {
       return super.delete(new EntityWrapper<UserAuthority>()
                .eq("user_id", userId)
                .in("authority_name", authorities.stream().map(a -> a.getName()).collect(Collectors.toList())))
        ;
    }

    public boolean isCurrentUserInRole(String authority) {
        if (userService.findUserAuthorities(LoginInformationUtil.getCurrentUserOid()).contains(new Authority(authority))) {
            return true;
        }
        return false;
    }

    public boolean hasTenantAuthority(String roleType) {
        Boolean isTenantAdmin = isCurrentUserInRole(AuthoritiesConstants.ROLE_TENANT_ADMIN);
        if (StringUtils.isNotEmpty(roleType) && Constants.ROLE_TENANT.equals(roleType) && isTenantAdmin) {
            return true;
        } else {
            return false;
        }
    }



}
