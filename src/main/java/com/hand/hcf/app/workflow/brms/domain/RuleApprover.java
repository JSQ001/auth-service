package com.hand.hcf.app.workflow.brms.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * 审批者
 */
@TableName("sys_rule_approver")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleApprover extends Domain implements Serializable {

    @NotNull
    private UUID ruleApproverOid;
    private Integer status;
    private String code;
    private String name;
    private String remark;
    private Integer departmentType;
    private Integer approverType;
    private UUID approverEntityOid;
    private Integer levelNumber;
    @NotNull
    private UUID ruleApprovalNodeOid;
    //包含分摊成本中心主管
    @TableField("contains_appo_center_manger")
    private Boolean containsAppoCenterManger;
    //包含分摊组织架构主管
    @TableField("contains_appo_depart_manager")
    private Boolean containsAppoDepartManager;
    //包含分摊成本中心主要部门的部门经理
    @TableField("contains_appo_pri_dept_manager")
    private Boolean containsAppoPriDeptManager;

    @ApiModelProperty(value = "审批流通知id")
    @TableField("rule_notice_id")
    private Long ruleNoticeId;
}
