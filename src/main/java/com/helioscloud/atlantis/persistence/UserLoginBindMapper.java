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
import org.apache.ibatis.annotations.Select;

public interface UserLoginBindMapper extends BaseMapper<UserLoginBind> {

    @Select("select * from art_user_login_bind where id=#{0}")
    UserLoginBind testMapper(String id);
}
