package com.hand.hcf.app.workflow.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class ApprovalPassNodeResp implements Serializable{

    private UUID ruleApprovalNodeOid;

    private String name;

    private String remark;

    private Integer status;
}
