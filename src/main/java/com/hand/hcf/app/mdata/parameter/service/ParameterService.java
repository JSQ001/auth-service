package com.hand.hcf.app.mdata.parameter.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.hand.hcf.app.mdata.parameter.domain.Parameter;
import com.hand.hcf.app.mdata.parameter.enums.ParameterLevel;
import com.hand.hcf.app.mdata.parameter.persistence.ParameterMapper;
import com.hand.hcf.core.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 19:40
 */
@Service
public class ParameterService extends BaseService<ParameterMapper,Parameter> {

    @Autowired
    private ParameterMapper parameterMapper;

    public List<Parameter> listParameterByModuleCode(String moduleCode,ParameterLevel parameterLevel){
        Wrapper<Parameter> wrapper = new EntityWrapper<Parameter>()
                .eq(moduleCode != null, "module_code", moduleCode);
        if(ParameterLevel.SOB.equals(parameterLevel)){
            wrapper.eq("sob_parameter",true);
        }else if(ParameterLevel.COMPANY.equals(parameterLevel)){
            wrapper.eq("company_parameter",true);
        }
        List<Parameter> parameterList = parameterMapper.selectList(wrapper);
        return parameterList;
    }

    public Parameter getParameterByParameterCode(String parameter){
        return parameterMapper.selectOne(Parameter.builder().parameterCode(parameter).build());
    }
}
