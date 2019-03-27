package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class MoveApprovalNodeDTO implements Serializable{
    private UUID ruleApprovalNodeOid;
    private UUID nextRuleApprovalNodeOid;
}
