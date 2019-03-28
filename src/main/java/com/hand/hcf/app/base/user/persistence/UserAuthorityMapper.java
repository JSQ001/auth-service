package com.hand.hcf.app.base.user.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.user.domain.UserAuthority;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * 用户权限关联数据存储层
 * Created by Strive on 18/3/14.
 */
public interface UserAuthorityMapper extends BaseMapper<UserAuthority> {
    /**
     * 根据用户ID查询用户权限关联信息
     *
     * @param userId：用户ID
     * @return
     */
    Set<UserAuthority> findByUserId(@Param("userId") Long userId);
}
