/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

/**
* Created by Transy on 2017/5/18.
*/
package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.UserLoginBind;
import com.helioscloud.atlantis.dto.UserDTO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

public interface UserLoginBindMapper extends BaseMapper<UserLoginBind> {

    @Select("select * from art_user_login_bind where id=#{0}")
    UserLoginBind testMapper(String id);
    List<UserLoginBind> findOneByUserOID(@Param("userOID") UUID userOid);
}
