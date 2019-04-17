package com.hand.hcf.app.workflow.domain;


import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * A WorkflowRole.
 */
@Data
@TableName("sys_workflow_role")
public class WorkflowRole extends Domain implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID workflowRoleOid;
    private UUID workflowRulesSettingOid;
    private Integer sequenceNumber;
    private Integer amountSequence;
    private Double upperBound;
    private Double lowerBound;
    private String ruleType;
    private String url;
    private Integer departmentManagerId;
    private Integer costCenterItemManagerId;
    private UUID userOid;
    private Integer numbers;//选人审批数或部门主管级数
}
