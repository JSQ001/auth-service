package com.hand.hcf.app.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/12.
 * 单据工作流 关联审批人表
 */
@Data
@TableName("sys_wfl_ref_approvers")
public class WorkFlowApprovers extends Domain {
    @TableField("approver_oid")
    private UUID approverOid;//审批人Oid

    @TableField("workflow_document_ref_id")
    private Long workFlowDocumentRefId;// 单据工作流关联表ID

    @TableField("approve_node_oid")
    private String  approveNodeOid;//  审批节点Oid
}
