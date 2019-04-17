package com.hand.hcf.app.common.co;

import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/12.
 * 单据工作流 关联审批人表
 */
@Data
public class WorkflowApproversCO extends Domain {
    private UUID approverOid;//审批人OID

    private Long workFlowDocumentRefId;// 单据工作流关联表ID

    private UUID approveNodeOid;//  审批节点OID
}
