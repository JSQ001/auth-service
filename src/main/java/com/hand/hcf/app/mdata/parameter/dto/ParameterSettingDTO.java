package com.hand.hcf.app.mdata.parameter.dto;

import com.hand.hcf.app.mdata.parameter.enums.ParameterLevel;
import com.hand.hcf.app.mdata.parameter.enums.ParameterValueTypeEnum;
import lombok.Data;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/27 11:11
 */
@Data
public class ParameterSettingDTO {

    private Long id;

    private Long parameterId;

    private String moduleCode;

    private String moduleName;

    private String parameterCode;

    private String parameterName;

    private ParameterValueTypeEnum parameterValueType;

    private ParameterLevel parameterLevel;

    private Long setOfBooksId;

    private String setOfBooksName;

    private Boolean sobParameter;

    private Long companyId;

    private String companyName;

    private Boolean companyParameter;

    private String parameterHierarchy;
    /**
     * 参数值，如果parameterValueType
     * 是VALUE_LIST则是sys_parameter_value表的id
     * 如果是API则是成本中心id
     * 如果是TEXT，则是本身
     * 如果是INT，也是本身
     * 如果是DATE，也是本身
     */
    private String parameterValueId;

    private String parameterValue;

    private String parameterValueDesc;

    private String api;

    private String apiSourceModule;

    private Integer versionNumber;

}
