/**
* Created by Transy on 2017/5/18.
*/
package com.hand.hcf.app.base.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.domain.PasswordHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PasswordHistoryMapper extends BaseMapper<PasswordHistory> {

    List<PasswordHistory> getPasswordHistoryOrderByCreateDate(@Param(value = "userOID") String userOID);
}
