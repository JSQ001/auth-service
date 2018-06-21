/**
* Created by Transy on 2017/5/18.
*/
package com.helioscloud.atlantis.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.helioscloud.atlantis.domain.PasswordHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PasswordHistoryMapper extends BaseMapper<PasswordHistory> {

    List<PasswordHistory> getPasswordHistoryOrderByCreateDate(@Param(value = "userOID") String userOID);
}
