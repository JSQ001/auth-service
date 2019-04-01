package com.hand.hcf.app.workflow.workflow.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ApprovalResultDTO {
    private UUID entityOid;
    private Integer entityType;
    private UUID approverOid;
    private UUID operatorOid;
    private UUID formOid;
    private Integer invoiceAllowUpdateType;
}
