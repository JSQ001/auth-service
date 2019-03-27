package com.hand.hcf.app.workflow.workflow.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * A WorkflowRulesSetting.
 */
@Data
@TableName("sys_workflow_rules_setting")
public class WorkflowRulesSetting extends Domain implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID workflowRulesSettingOid;
    private UUID companyOid;
    private UUID departmentOid;
    private UUID costCenterOid;
    private UUID costCenterItemOid;
    private String ruleType;
    private String amount;
    private Integer entityType;

    @TableField(exist = false)
    private List<WorkflowRole> roleList;

}
