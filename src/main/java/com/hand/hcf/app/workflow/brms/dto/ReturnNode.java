package com.hand.hcf.app.workflow.brms.dto;

import lombok.Data;

import java.util.UUID;

/**
 * @author mh.z
 * @date 2019/04/16
 */
@Data
public class ReturnNode {
    /** 节点oid */
    private UUID ruleApprovalNodeOid;

    /** 节点名称 */
    private String remark;
}
