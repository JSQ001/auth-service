package com.hand.hcf.app.mdata.implement.web;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.core.exception.BizException;
import com.hand.hcf.app.core.util.PageUtil;
import com.hand.hcf.app.mdata.parameter.domain.Parameter;
import com.hand.hcf.app.mdata.parameter.domain.ParameterSetting;
import com.hand.hcf.app.mdata.parameter.enums.ParameterLevel;
import com.hand.hcf.app.mdata.parameter.enums.ParameterValueTypeEnum;
import com.hand.hcf.app.mdata.parameter.service.ParameterService;
import com.hand.hcf.app.mdata.parameter.service.ParameterSettingService;
import com.hand.hcf.app.mdata.parameter.service.ParameterValuesService;
import com.hand.hcf.app.mdata.utils.RespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: chenzhipeng
 * @Date: 2019/1/16 09:12
 */
@RestController
public class ParameterControllerImpl {

    @Autowired
    private ParameterService parameterService;

    @Autowired
    private ParameterValuesService parameterValuesService;

    @Autowired
    private ParameterSettingService parameterSettingService;

    public String getParameterValueByParameterCode(@RequestParam("parameterCode")String parameterCode,
                                                   @RequestParam("tenantId")Long tenantId,
                                                   @RequestParam(value = "sobId",required = false)Long sobId,
                                                   @RequestParam(value = "companyId",required = false)Long companyId) {
        Parameter parameter = parameterService.selectOne(new EntityWrapper<Parameter>()
                .eq("parameter_code", parameterCode));
        if (parameter == null){
            //参数不存在
            throw new BizException(RespCode.PARAMETER_NOT_EXIST);
        }
        ParameterSetting parameterSetting = null;

//        Wrapper<ParameterSetting> wrapper = new EntityWrapper<ParameterSetting>()
//                .eq("parameter_id", parameter.getId());
        if(parameter.getCompanyParameter() && companyId != null){
            parameterSetting = parameterSettingService.selectOne(new EntityWrapper<ParameterSetting>()
                    .eq("parameter_id", parameter.getId())
                    .eq("parameter_level", ParameterLevel.COMPANY)
                    .eq("company_id", companyId));
        }
        if(parameter.getSobParameter() && sobId != null && parameterSetting == null){
            parameterSetting = parameterSettingService.selectOne(new EntityWrapper<ParameterSetting>()
                    .eq("parameter_id", parameter.getId())
                    .eq("parameter_level", ParameterLevel.SOB)
                    .eq("set_of_books_id", sobId));
        }
        if(parameterSetting == null){
            parameterSetting = parameterSettingService.selectOne(new EntityWrapper<ParameterSetting>()
                    .eq("parameter_id", parameter.getId())
                    .eq("parameter_level", ParameterLevel.TENANT)
                    .eq("tenant_id", tenantId));
        }
        if (parameterSetting == null) {
            return null;
        }
        String result = null;
        if(ParameterValueTypeEnum.VALUE_LIST.equals(parameter.getParameterValueType())){
            result = parameterValuesService.selectById(parameterSetting.getParameterValueId()).getParameterValueCode();
        }else if(ParameterValueTypeEnum.API.equals(parameter.getParameterValueType())){
            Page basicPage = PageUtil.getPage(0, 10);
            basicPage.setSearchCount(false);
            List<BasicCO> values = parameterValuesService.pageParameterValuesByCond(parameterCode,parameterSetting.getParameterValueId(),parameterSetting.getParameterLevel(),parameterSetting.getSetOfBooksId(),parameterSetting.getCompanyId(),null,null,basicPage);
            if(values != null && values.size() > 0){
                result = values.get(0).getCode();
            }
        }else{
            result = parameterSetting.getParameterValueId();
        }
        return result;
    }
}
