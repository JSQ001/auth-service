package com.hand.hcf.app.workflow.brms.domain;


import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * A Application.
 * 节点
 */
@TableName("sys_rule_approval_node")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleApprovalNode extends Domain implements Serializable {

    @NotNull
    private UUID ruleApprovalNodeOid;
    private Integer status;
    @NotNull
    private UUID ruleApprovalChainOid;
    private String code;
    private String name;
    private String remark;
    private Integer typeNumber;

    private Integer nullableRule;
    private Integer countersignRule;
    private Integer repeatRule;
    private Integer selfApprovalRule;
    private Boolean printFlag;
    private Integer sequenceNumber;
    /**
     *节点审批动作
     *
     */
    private String approvalActions;
    private String drl;
	/**
     *	知会配置内容
     */
    private String notifyInfo;
	/**
     *	机器人节点审批意见
     */
    private String comments;
	/**
     *	能否修改核定金额(0:不允许，1允许)
     *
     */
    private Integer invoiceAllowUpdateType;
}
