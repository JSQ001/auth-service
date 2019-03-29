package com.hand.hcf.app.workflow.workflow.dto;


import com.hand.hcf.app.workflow.workflow.domain.ApprovalChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildApprovalChainResult {
    List<ApprovalChain> approvalChains;
    Boolean autoSelfApproval;
    Integer nodeTypeAndApprovalResult;
    //审批结束后是否打印
    Boolean endNodePrintEnable;
}
