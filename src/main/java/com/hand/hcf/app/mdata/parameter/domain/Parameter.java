package com.hand.hcf.app.mdata.parameter.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18n;
import com.hand.hcf.app.mdata.parameter.enums.ParameterValueTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 18:50
 */
@Data
@TableName("sys_parameter")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Parameter extends DomainI18n {
    /**
     * 参数代码
     */
    @TableField("parameter_code")
    private String parameterCode;
    /**
     * 参数名称
     */
    @TableField("parameter_name")
    @I18nField
    private String parameterName;
    /**
     * 模块代码
     */
    @TableField("module_code")
    private String moduleCode;
    /**
     * 账套级参数
     */
    @TableField("sob_parameter")
    private Boolean sobParameter;
    /**
     * 公司级参数
     */
    @TableField("company_parameter")
    private Boolean companyParameter;
    /**
     * 参数值类型
     */
    @TableField("parameter_value_type")
    private ParameterValueTypeEnum parameterValueType;
    /**
     * API来源模块
     */
    @TableField("api_source_module")
    private String apiSourceModule;
    /**
     * API
     */
    @TableField("api")
    private String api;
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
