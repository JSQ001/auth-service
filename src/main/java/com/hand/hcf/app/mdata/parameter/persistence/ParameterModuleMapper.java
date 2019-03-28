package com.hand.hcf.app.mdata.parameter.persistence;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hand.hcf.app.mdata.parameter.domain.ParameterModule;
import com.hand.hcf.app.mdata.parameter.dto.ParameterModuleDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 19:55
 */
public interface ParameterModuleMapper extends BaseMapper<ParameterModule> {

    //根据租户id查询租户下启用的模块
    List<ParameterModuleDTO> listModuleByTenantId(@Param("tenantId") Long tenantId);
}
