package com.hand.hcf.app.workflow.workflow.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ApprovalPassChainResp implements Serializable{
    private Long id;
    private UUID approverOid;
    private String userName;
    private UUID ruleApprovalNodeOid;
}
