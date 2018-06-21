package com.helioscloud.atlantis.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.UserDevice;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

/**
 * @author qingsheng.chen 2018/6/8 Friday 15:19
 */
public interface UserDeviceMapper extends BaseMapper<UserDevice> {
    /**
     * 查询用户所有设备
     *
     * @param userOid 用户OID
     * @return 用户所有设备
     */
    List<UserDevice> selectUserDevice(@Param("userOid") UUID userOid);
}
