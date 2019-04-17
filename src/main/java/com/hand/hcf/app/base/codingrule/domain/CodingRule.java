package com.hand.hcf.app.base.codingrule.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author dong.liu on 2017-08-23
 */
@Data
@TableName("sys_coding_rule")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodingRule extends DomainI18nEnable {


	@TableField("coding_rule_object_id")
	private Long codingRuleObjectId; //编码规则定义id
    @NotNull
    @TableField("coding_rule_code")
    private String codingRuleCode; //编码规则代码
    @Length(max = 50)
    @TableField("coding_rule_name")
    @I18nField
    private String codingRuleName; //编码规则名称
    @Length(max = 50)
    @TableField("remark")
    private String remark; //描述
	@TableField("reset_frequence")
	private String resetFrequence; //重置频率
    @TableField(exist = false)
    private String resetFrequenceName; //重置频率名称
    @TableField(value = "tenant_id")

    private Long tenantId;  //租户id
    @TableField("detail_synthesis")
    private String detailSynthesis;
}
