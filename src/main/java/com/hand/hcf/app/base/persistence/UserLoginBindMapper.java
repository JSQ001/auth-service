

/**
* Created by Transy on 2017/5/18.
*/
package com.hand.hcf.app.base.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.domain.UserLoginBind;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

public interface UserLoginBindMapper extends BaseMapper<UserLoginBind> {

    @Select("select * from art_user_login_bind where id=#{0}")
    UserLoginBind testMapper(String id);
    List<UserLoginBind> findOneByUserOID(@Param("userOID") UUID userOid);
}
