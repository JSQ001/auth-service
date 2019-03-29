package com.hand.hcf.app.workflow.workflow.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ApprovalDTO {
    private UUID entityOid;
    private Integer entityType;

    private UUID approverOid;

    private UUID operatorOid;

    private Integer invoiceAllowUpdateType;//能否修改核定金额


}
