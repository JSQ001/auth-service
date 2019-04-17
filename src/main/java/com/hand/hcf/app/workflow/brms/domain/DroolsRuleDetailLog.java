package com.hand.hcf.app.workflow.brms.domain;

import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author polus
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DroolsRuleDetailLog extends Domain {

    private UUID droolsRuleDetailOid;

    private UUID ruleConditionOid;

    private UUID ruleConditionApproverOid;

    private String droolsRuleDetailValue;

    private RuleCondition ruleCondition;

    private String expectedResultMessage;

}
