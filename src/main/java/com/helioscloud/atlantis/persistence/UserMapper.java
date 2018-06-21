/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.dto.UserDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;


/**
 * Created by markfredchen on 2017/3/16.
 */
public interface UserMapper extends BaseMapper<UserDTO> {

    /**
     * 根据邮箱查询用户
     *
     * @param email：邮箱
     * @return 用户
     */
    UserDTO findOneByContactEmail(@Param("email") String email);

    List<UserDTO> findUserByUserBind(@Param("login") String login);

    UserDTO findOneByLogin(@Param("login") String login);

    UserDTO findOneByMobile(@Param("mobile") String mobile);

    UserDTO findOneByUserOID(@Param("userOID") UUID userOID);

    UserDTO findOneByID(@Param("id") Long id);

    void updateUserLock(UserDTO userDTO);
}
