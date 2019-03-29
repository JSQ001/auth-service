package com.hand.hcf.app.workflow.workflow.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class ApprovalDetailResp implements Serializable{
    /**
     * 表单oid
     */
    private UUID formOid;
    /**
     * 表单名字
     */
    private String formName;
    /**
     * 公司oid
     */
    private UUID companyOid;
    /**
     * 审批通过列表
     */
    private List<ApprovalPassChainResp> passChainResp;
    /**
     * 审批通过对应节点列表
     */
    private List<ApprovalPassNodeResp> passNodeResp;
}
