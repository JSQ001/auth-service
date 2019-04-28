

package com.hand.hcf.app.base.user.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.base.tenant.domain.Tenant;
import com.hand.hcf.app.base.user.domain.User;
import com.hand.hcf.app.base.user.dto.UserQO;
import com.hand.hcf.app.core.security.domain.Authority;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public interface UserMapper extends BaseMapper<User> {

    List<User> listByQO(UserQO userQO);

    List<User> listByQO(Page page, UserQO userQO);



    Set<Authority> listAuthorities(@Param("userId") Long userId, @Param("userOid") UUID userOid);

    Tenant getCurrentTenantByUserOid(@Param("userOid") UUID userOid);




    void updateUserLockStatus(@Param("userId") Long userId,
                              @Param("lockStatus") Integer lockStatus);

    Integer checkLogin(@Param("keyWord") String keyWord,
                       @Param("id") Long id);
}
