package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class RuleApprovalChainCopyDTO implements Serializable{
    private UUID sourceFormOid;
    private UUID targetFormOid;
}
