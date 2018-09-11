/*
 * Copyright (c) 2018. Shanghai Zhenhui Information Technology Co,. ltd.
 * All rights are reserved.
 */

package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
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

    /**
     * 用户切换语言
     * @param userId
     * @param language
     */
    void updateUserLanguage(@Param("userId")Long userId, @Param("language")String language);

    /**
     * 获取用户列表 分页
     * @param tenantId    必填，取租户下的所有用户
     * @param setOfBooksId 如果填了，取帐套下的用户
     * @param companyId    如果填了，则取公司下的用户
     * @return 按full_name排序
     */
    List<UserDTO> getUserListByCond(@Param("tenantId") Long tenantId,
                                                @Param("setOfBooksId") Long setOfBooksId,
                                                @Param("companyId") Long companyId,
                                                @Param("login") String login,
                                                @Param("fullName") String fullName,
                                                @Param("mobile") String mobile,
                                                @Param("email") String email,
                                                Page page);
}
