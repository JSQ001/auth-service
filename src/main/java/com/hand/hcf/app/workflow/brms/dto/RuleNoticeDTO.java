package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author mh.z
 * @date 2019/04/16
 */
@Data
public class RuleNoticeDTO {
    /** 审批流通知oid */
    private UUID ruleNoticeOid;

    /** 节点id */
    private Long ruleApprovalNodeId;

    /** 节点oid */
    private UUID ruleApprovalNodeOid;

    /** 审批流通知动作 */
    private List<Integer> actions;

    /** 审批人*/
    private List<RuleApproverDTO> users;

    /** 条件 */
    private Map<Long, List<RuleConditionDTO>> conditions;
}
