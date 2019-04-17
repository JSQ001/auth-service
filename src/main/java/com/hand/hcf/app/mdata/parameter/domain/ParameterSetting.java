package com.hand.hcf.app.mdata.parameter.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogic;
import com.hand.hcf.app.mdata.parameter.enums.ParameterLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: chenzhipeng
 * @Date: 2018/12/26 18:50
 */
@Data
@TableName("sys_parameter_setting")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParameterSetting extends DomainLogic {
    /**
     * 参数级别
     */
    @TableField("parameter_level")
    private ParameterLevel parameterLevel;
    /**
     * 租户id
     */
    @TableField("tenant_id")
    private Long tenantId;
    /**
     * 帐套id
     */
    @TableField("set_of_books_id")
    private Long setOfBooksId;
    /**
     * 公司id
     */
    @TableField("company_id")
    private Long companyId;
    /**
     * 参数id
     */
    @TableField("parameter_id")
    private Long parameterId;
    /**
     * 参数值id
     */
    @TableField("parameter_value_id")
    private String parameterValueId;
}
