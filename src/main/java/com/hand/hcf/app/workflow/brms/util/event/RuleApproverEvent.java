package com.hand.hcf.app.workflow.brms.util.event;

import com.hand.hcf.app.workflow.brms.domain.RuleApprover;
import com.hand.hcf.app.workflow.brms.domain.RuleCondition;
import com.hand.hcf.app.workflow.brms.dto.RuleConditionDTO;
import lombok.Data;

import java.util.List;

@Data
public class RuleApproverEvent implements Event {

    @Override
    public String getKey() {
        if (ruleApprover == null) {
            return null;
        }

        return ruleApprover.getRemark();
    }

    public RuleApprover getRuleApprover() {
        return ruleApprover;
    }

    public List<RuleCondition> ruleConditionList;

    public RuleConditionDTO ruleConditionDTO;

    public void setRuleApprover(RuleApprover ruleApprover) {
        this.ruleApprover = ruleApprover;
    }

    private RuleApprover ruleApprover;

    public RuleApproverEvent(RuleApprover ruleApprover) {
        this.ruleApprover = ruleApprover;
    }
}
