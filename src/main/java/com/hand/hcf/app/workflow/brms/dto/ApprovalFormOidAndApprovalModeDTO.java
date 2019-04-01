package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApprovalFormOidAndApprovalModeDTO implements Serializable {

    private String formOid;

    private Integer approvalMode;
}
