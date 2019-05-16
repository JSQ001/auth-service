package com.hand.hcf.app.workflow.dto.dashboard;

import lombok.Data;

import java.util.List;

@Data
public class ApprovalDashboardDTO {
    private Integer totalCount;
    private List<ApprovalDashboardDetailDTO> approvalDashboardDetailDTOList;
}
