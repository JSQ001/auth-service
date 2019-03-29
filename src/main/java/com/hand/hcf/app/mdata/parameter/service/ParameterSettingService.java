package com.hand.hcf.app.mdata.parameter.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hand.hcf.app.common.co.BasicCO;
import com.hand.hcf.app.mdata.parameter.domain.ParameterModuleStatus;
import com.hand.hcf.app.mdata.parameter.domain.ParameterSetting;
import com.hand.hcf.app.mdata.parameter.domain.ParameterValues;
import com.hand.hcf.app.mdata.parameter.dto.ParameterSettingDTO;
import com.hand.hcf.app.mdata.parameter.enums.ParameterLevel;
import com.hand.hcf.app.mdata.parameter.enums.ParameterValueTypeEnum;
import com.hand.hcf.app.mdata.parameter.persistence.ParameterSettingMapper;
import com.hand.hcf.app.mdata.utils.RespCode;
import com.hand.hcf.app.mdata.utils.StringUtil;
import com.hand.hcf.core.exception.BizException;
import com.hand.hcf.core.service.BaseService;
import com.hand.hcf.core.util.LoginInformationUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/27 14:25
 */
@Service
public class ParameterSettingService extends BaseService<ParameterSettingMapper, ParameterSetting> {

    @Autowired
    private ParameterSettingMapper parameterSettingMapper;

    @Autowired
    private ParameterValuesService parameterValuesService;

    @Autowired
    private ParameterModuleStatusService parameterModuleStatusService;

    public List<ParameterSettingDTO> pageParameterSettingByLevelAndCond(ParameterLevel parameterLevel,
                                                                        Long tenantId,
                                                                        Long setOfBooksId,
                                                                        Long companyId,
                                                                        String moduleCode,
                                                                        String parameterCode,
                                                                        String parameterName,
                                                                        Page page){
        List<ParameterSettingDTO> parameterSettingDTOS = parameterSettingMapper.pageParameterSettingByLevelAndCond(parameterLevel.getValue(), LoginInformationUtil.getCurrentTenantId(),setOfBooksId,companyId,moduleCode,parameterCode,parameterName,page);
            if(parameterSettingDTOS != null && parameterSettingDTOS.size() > 0){
            //根据值类型拿值
            parameterSettingDTOS.forEach(parameterSettingDTO -> {
                if(ParameterValueTypeEnum.VALUE_LIST.equals(parameterSettingDTO.getParameterValueType())){
                    if(!StringUtil.isNullOrEmpty(parameterSettingDTO.getParameterValueId())){
                        ParameterValues parameterValues = parameterValuesService.selectById(parameterSettingDTO.getParameterValueId());
                        if(parameterValues == null){
                            new BizException(RespCode.PARAMETER_VALUES_NOT_EXIST);
                        }
                        parameterSettingDTO.setParameterValue(parameterValues.getParameterValueCode());
                        parameterSettingDTO.setParameterValueDesc(parameterValues.getParameterValueName());
                    }
                }else if(ParameterValueTypeEnum.API.equals(parameterSettingDTO.getParameterValueType())){
                    Map<String,Object> parameterMap = new HashMap<>();
                    if(!StringUtil.isNullOrEmpty(parameterSettingDTO.getParameterValueId())){
                        parameterMap.put("selectId",parameterSettingDTO.getParameterValueId());
                    }
                    List<BasicCO> result = parameterValuesService.pageParameterValuesByCond(parameterSettingDTO.getParameterCode(),parameterSettingDTO.getParameterValueId(),parameterSettingDTO.getParameterLevel(),parameterSettingDTO.getSetOfBooksId(),parameterSettingDTO.getCompanyId(),null,null,page);
                    if(result != null && result.size() > 0){
                        parameterSettingDTO.setParameterValue(result.get(0).getCode());
                        parameterSettingDTO.setParameterValueDesc(result.get(0).getName());
                    }
                }else{
                    parameterSettingDTO.setParameterValue(parameterSettingDTO.getParameterValueId());
                }
                parameterSettingDTO.setParameterHierarchy("租户级");
                if(parameterSettingDTO.getSobParameter()){
                    parameterSettingDTO.setParameterHierarchy(parameterSettingDTO.getParameterHierarchy()+"|账套级");
                }
                if(parameterSettingDTO.getCompanyParameter()){
                    parameterSettingDTO.setParameterHierarchy(parameterSettingDTO.getParameterHierarchy()+"|公司级");
                }
            });
        }else{
            if(ParameterLevel.TENANT.equals(parameterLevel)){
                List<ParameterModuleStatus> parameterModuleStatuses = parameterModuleStatusService.listSysParameterModuleStatus();
                List<ParameterSetting> parameterSettings = parameterSettingMapper.selectList(new EntityWrapper<ParameterSetting>()
                        .eq("tenant_id",0));
                if(CollectionUtils.isEmpty(parameterModuleStatuses) && CollectionUtils.isEmpty(parameterSettings)){
                    return new ArrayList<>();
                }
                initTenantParameterData(parameterModuleStatuses,parameterSettings);
                return this.pageParameterSettingByLevelAndCond(parameterLevel, LoginInformationUtil.getCurrentTenantId(),setOfBooksId,companyId,moduleCode,parameterCode,parameterName,page);
            }
        }

        return parameterSettingDTOS;
    }

    @Transactional(rollbackFor = {Exception.class})
    public ParameterSetting insertParameterSetting(ParameterSetting parameterSetting){
        parameterSettingMapper.insert(parameterSetting);
        return parameterSetting;
    }

    @Transactional(rollbackFor = {Exception.class})
    public ParameterSetting updateParameterSetting(ParameterSetting parameterSetting){
        parameterSettingMapper.updateById(parameterSetting);
        return parameterSetting;
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteParameterSettingById(Long parameterSettingId){
        ParameterSetting parameterSetting = this.selectById(parameterSettingId);
        if(parameterSetting == null){
            new BizException(RespCode.PARAMETER_SETTING_NOT_EXIST);
        }
        parameterSetting.setDeleted(true);
        parameterSetting.setLastUpdatedBy(LoginInformationUtil.getCurrentUserId());
        parameterSetting.setLastUpdatedDate(ZonedDateTime.now());
        //逻辑删除，liquibase增加deleted字段,并且添加逻辑
        this.updateById(parameterSetting);
    }

    /**
     * 初始化租户参数数据
     * @param parameterModuleStatuses 系统模块状态表数据
     * @param parameterSettings 系统参数明细数据
     */
    @Transactional(rollbackFor = {Exception.class})
    void initTenantParameterData(List<ParameterModuleStatus> parameterModuleStatuses, List<ParameterSetting> parameterSettings){
        List<ParameterModuleStatus> moduleStatusList = parameterModuleStatuses.stream().map(parameterModuleStatus -> {
            ParameterModuleStatus value = ParameterModuleStatus.builder()
                    .tenantId(LoginInformationUtil.getCurrentTenantId())
                    .moduleCode(parameterModuleStatus.getModuleCode())
                    .build();
            value.setEnabled(parameterModuleStatus.getEnabled());
            return value;
        }).collect(Collectors.toList());

        List<ParameterSetting> parameterSettingList = parameterSettings.stream().map(parameterSetting -> {
                ParameterSetting value = ParameterSetting.builder()
                        .parameterValueId(parameterSetting.getParameterValueId())
                        .companyId(parameterSetting.getCompanyId())
                        .parameterId(parameterSetting.getParameterId())
                        .parameterLevel(parameterSetting.getParameterLevel())
                        .setOfBooksId(parameterSetting.getSetOfBooksId())
                        .tenantId(LoginInformationUtil.getCurrentTenantId())
                        .build();
                return value;
            }).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(moduleStatusList)){
            parameterModuleStatusService.insertBatch(moduleStatusList);
        }
        if(CollectionUtils.isNotEmpty(parameterSettingList)){
            this.insertBatch(parameterSettingList);
        }
    }
}
