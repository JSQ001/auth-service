package com.hand.hcf.app.base.codingrule.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.DomainLogicEnable;
import lombok.Data;

/**
 * @author dong.liu on 2017-08-24
 */
@Data
@TableName("sys_coding_rule_value")
public class CodingRuleValue extends DomainLogicEnable {

    @TableField("coding_rule_id")
    private Long codingRuleId; //编码规则id
    @TableField("document_type_code")
    private String documentTypeCode; //单据类型代码
    @TableField("company_code")
    private String companyCode; //公司代码
    @TableField("month_current_value")
    private Integer monthCurrentValue; //频率为每月的当前值
    @TableField("year_current_value")
    private Integer yearCurrentValue; //频率为每年的当前值
    @TableField("never_current_value")
    private Integer neverCurrentValue; //频率为从不的当前值
    @TableField("period_name")
    private String periodName; //操作日期
    @TableField(value = "tenant_id")
    private Long tenantId;  //租户id
}
