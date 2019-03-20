package com.hand.hcf.app.auth.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.auth.domain.UserLock;

import java.util.List;
import java.util.Map;

/**
 * Created by caixiang on 2017/7/19.
 */
public interface UserLockMapper extends BaseMapper<UserLock> {

    List<Map<String, Object>> selectUserLockMap();

}
