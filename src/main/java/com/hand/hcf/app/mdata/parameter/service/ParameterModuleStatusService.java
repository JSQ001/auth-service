package com.hand.hcf.app.mdata.parameter.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.parameter.domain.ParameterModuleStatus;
import com.hand.hcf.app.mdata.parameter.persistence.ParameterModuleStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/1/8 16:26
 */
@Service
public class ParameterModuleStatusService extends BaseService<ParameterModuleStatusMapper,ParameterModuleStatus> {

    @Autowired
    private ParameterModuleStatusMapper parameterModuleStatusMapper;

    //查询系统模块，租户id，0代表系统，为了初始化给租户。
    public List<ParameterModuleStatus> listSysParameterModuleStatus(){
        return parameterModuleStatusMapper
                .selectList(new EntityWrapper<ParameterModuleStatus>()
                .eq("tenant_id",0));
    }
}
