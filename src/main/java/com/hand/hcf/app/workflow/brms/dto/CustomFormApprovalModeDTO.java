package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CustomFormApprovalModeDTO implements Serializable{
    private List<ApprovalFormOidAndApprovalModeDTO> approvalFormOidAndApprovalModeDTOList;
}
