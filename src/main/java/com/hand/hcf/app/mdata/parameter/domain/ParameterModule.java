package com.hand.hcf.app.mdata.parameter.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18n;
import lombok.Data;

/**
 * 系统模块表
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 18:51
 */
@Data
@TableName("sys_parameter_module")
public class ParameterModule extends DomainI18n {
    /**
     * 模块代码
     */
    @TableField("module_code")
    private String moduleCode;
    /**
     * 模块名称
     */
    @TableField("module_name")
    @I18nField
    private String moduleName;

}
