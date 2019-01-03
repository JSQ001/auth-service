package com.hand.hcf.app.base.persistence;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.domain.UserLock;

import java.util.List;
import java.util.Map;

/**
 * Created by caixiang on 2017/7/19.
 */
public interface UserLockMapper extends BaseMapper<UserLock> {

    List<Map<String, Object>> selectUserLockMap();

}
