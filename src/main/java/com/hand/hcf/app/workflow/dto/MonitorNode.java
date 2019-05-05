package com.hand.hcf.app.workflow.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class MonitorNode {
    /** 节点oid */
    private UUID ruleApprovalNodeOid;

    /** 节点名称 */
    private String remark;

    /** 是否可以跳转 **/
    private Boolean jump;

    /** 是否当前节点**/
    private Boolean isApprovalNode;
}
