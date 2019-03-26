package com.hand.hcf.app.mdata.parameter.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.mdata.parameter.domain.Parameter;
import com.hand.hcf.app.mdata.parameter.domain.ParameterValues;
import com.hand.hcf.app.mdata.parameter.enums.ParameterLevel;
import com.hand.hcf.app.mdata.parameter.persistence.ParameterValuesMapper;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.service.FeignReflectService;
import com.hand.hcf.core.util.LoginInformationUtil;
import com.hand.hcf.core.util.TypeConversionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/27 14:25
 */
@Service
public class ParameterValuesService extends BaseService<ParameterValuesMapper, ParameterValues> {

    @Autowired
    private ParameterValuesMapper parameterValuesMapper;

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private FeignReflectService feignReflectService;

    private final String CompanyServiceURL = "hcf.application.base.name";

    public List<BasicCO> listParameterValuesByPVType(String parameterCode){
        List<BasicCO> result = parameterValuesMapper.listParameterValuesByPVType(parameterCode);
        return result;
    }

    public List<BasicCO> pageParameterValuesByCond(String parameterCode,
                                                             Object selectId,
                                                             ParameterLevel parameterLevel,
                                                             Long setOfBooksId,
                                                             Long companyId,
                                                             String code,
                                                             String name,
                                                             Page page) {
        //若为API则为对应的API的返回值中code，描述为返回值名称。
        Parameter parameter = parameterService.getParameterByParameterCode(parameterCode);
        Map<String,Object> parameterMap = new HashMap<>();
        parameterMap.put("securityType",parameterLevel.name());
        if(parameterLevel.equals(ParameterLevel.TENANT)){
            parameterMap.put("filterId", LoginInformationUtil.getCurrentTenantId());
        }else if(parameterLevel.equals(ParameterLevel.SOB)){
            parameterMap.put("filterId",setOfBooksId);
        }else if(parameterLevel.equals(ParameterLevel.COMPANY)){
            parameterMap.put("filterId",companyId);
        }
        if (TypeConversionUtils.isNotEmpty(selectId)) {
            parameterMap.put("selectId",selectId);
        }
        if (TypeConversionUtils.isNotEmpty(code)) {
            parameterMap.put("code",code);
        }
        if (TypeConversionUtils.isNotEmpty(name)) {
            parameterMap.put("name",name);
        }
        parameterMap.put("page",page.getCurrent() - 1);
        parameterMap.put("size",page.getSize());

        List<BasicCO> result = this.getParameterValue(parameter.getApiSourceModule(),parameter.getApi(),parameterMap,page);
        return result;
    }

    public List<BasicCO> getParameterValue(String sourceModule,
                                                     String api,
                                                     Map<String,Object> parameterMap,
                                                     Page page){
        String applicationName = "";
        if("COMPANY".equals(sourceModule)){
            applicationName = CompanyServiceURL;
        }else{

        }
        Page<BasicCO> values = new Page<>();
        try {
            values = feignReflectService.doRestForParameterMap(values.getClass(),applicationName,api, RequestMethod.GET,"",parameterMap);
            if(values != null){
                page.setTotal(values.getTotal());
                return values.getRecords().stream().map(dto -> {
                    BasicCO result = new BasicCO();
                    result.setId(dto.getId().toString());
                    result.setCode(dto.getCode());
                    result.setName(dto.getName());
                    return result;
                }).collect(toList());
            }
        } catch (NoSuchMethodException e) {
            return null;
        }
        return null;
    }
}
