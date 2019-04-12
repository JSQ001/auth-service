package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class FormApprovalModeDTO implements Serializable {

    private String formOid;

    private Integer approvalMode;
}
