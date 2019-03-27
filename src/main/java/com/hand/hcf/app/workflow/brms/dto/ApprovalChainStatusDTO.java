package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ApprovalChainStatusDTO implements Serializable{
    private UUID ruleApprovalChainOid;
    private boolean enabled;
}
