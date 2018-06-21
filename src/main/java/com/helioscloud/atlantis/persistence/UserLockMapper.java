package com.helioscloud.atlantis.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.UserLock;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by caixiang on 2017/7/19.
 */
public interface UserLockMapper extends BaseMapper<UserLock> {

    List<Map<String, Object>> selectUserLockMap();

    void updateUserLockStatus(@Param("userId") Long userId,
                              @Param("lockStatus") Integer lockStatus);
}
