package com.hand.hcf.app.mdata.parameter.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18n;
import lombok.Data;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 18:50
 */
@Data
@TableName("sys_parameter_values")
public class ParameterValues extends DomainI18n {
    /**
     * 参数值code
     */
    @TableField("parameter_value_code")
    private String parameterValueCode;
    /**
     * 参数值名称
     */
    @TableField("parameter_value_name")
    @I18nField
    private String parameterValueName;
    /**
     * 参数代码
     */
    @TableField("parameter_code")
    private String parameterCode;
    /**
     * 默认参数值
     */
    @TableField("paramete_default_value")
    private String parameteDefaultValue;
}
