package com.hand.hcf.app.workflow.brms.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by Vance on 2017/1/22.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_drools_rule_detail")
public class DroolsRuleDetail extends Domain {
    @NotNull
    private UUID droolsRuleDetailOid;
    private UUID ruleConditionOid;
    private UUID ruleConditionApproverOid;
    private String droolsRuleDetailValue;
    private Long ruleConditionId;
    private String expectedResultMessage;
    @TableField(exist = false)
    private RuleCondition ruleCondition;

}
