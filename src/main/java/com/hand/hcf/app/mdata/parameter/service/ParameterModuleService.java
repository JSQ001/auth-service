package com.hand.hcf.app.mdata.parameter.service;

import com.hand.hcf.app.core.service.BaseService;
import com.hand.hcf.app.mdata.parameter.domain.ParameterModule;
import com.hand.hcf.app.mdata.parameter.dto.ParameterModuleDTO;
import com.hand.hcf.app.mdata.parameter.persistence.ParameterModuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 19:55
 */
@Service
public class ParameterModuleService extends BaseService<ParameterModuleMapper,ParameterModule> {

    @Autowired
    private ParameterModuleMapper parameterModuleMapper;

    public List<ParameterModuleDTO> listParameterModuleByTenantId(Long tenantId){
        return parameterModuleMapper.listModuleByTenantId(tenantId);
    }

}
