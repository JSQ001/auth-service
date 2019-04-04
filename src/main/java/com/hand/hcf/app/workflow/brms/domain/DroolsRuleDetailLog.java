package com.hand.hcf.app.workflow.brms.domain;

import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DroolsRuleDetailLog extends Domain {
    private Long id;

    private UUID droolsRuleDetailOid;

    private UUID ruleConditionOid;

    private UUID ruleConditionApproverOid;

    private String droolsRuleDetailValue;

    private RuleCondition ruleCondition;

    private String expectedResultMessage;

}
