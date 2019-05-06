package com.hand.hcf.app.base.userRole.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.base.userRole.domain.FunctionList;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @description:
 * @version: 1.0
 * @author: xue.han@hand-china.com
 * @date: 2019/1/29
 */
public interface FunctionListMapper extends BaseMapper<FunctionList>{
    /**
     * 查询其他租户相同的功能
     * @param tenantId
     * @param sourceFunctionId
     * @return
     */
    List<FunctionList> listOtherTenantFunction(@Param("tenantId") Long tenantId,
                                               @Param("sourceFunctionId") Long sourceFunctionId);
}
