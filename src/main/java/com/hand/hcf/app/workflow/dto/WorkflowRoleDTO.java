package com.hand.hcf.app.workflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;


/**
 * A DTO for the WorkflowRole entity.
 */
@Data
public class WorkflowRoleDTO implements Serializable {

    private UUID workflowRoleOid;//uuid

    private UUID workflowRulesSettingOid;//对应配置uuid

    private UUID userOid;//type:0时 用户uuid

    private Integer sequenceNumber;//审批流序列

    private Integer amountSequence;//金额序列

    private Double upperBound;//上界 不分规则配置时的金额

    private Double lowerBound;//下界 不分规则配置时的金额

    private ZonedDateTime createDate;

    private ZonedDateTime updateDate;

    @JsonIgnore
    private Long createdBy;
    @JsonIgnore
    private Long lastUpdatedBy;

    private String ruleType;//类型 0用户 1成本中心主管 2部门主管 3外部接口 5选人审批

    private String url;//type:3 时对应地址

    private Integer departmentManagerId;

    private Integer costCenterItemManagerId;

    private Integer number;//选人审批或部门主管级数

    /**
     * 前台显示字段
     */
    private String userName;//用户名

  


    @Override
    public String toString() {
        return "WorkflowRoleDTO{" +
            "costCenterItemManagerId=" + costCenterItemManagerId +
            ", workflowRoleOid='" + workflowRoleOid + '\'' +
            ", workflowRulesSettingOid='" + workflowRulesSettingOid + '\'' +
            ", userOid='" + userOid + '\'' +
            ", sequence_number=" + sequenceNumber +
            ", amountSequence=" + amountSequence +
            ", upperBound=" + upperBound +
            ", lowerBound=" + lowerBound +
            ", createDate=" + createDate +
            ", updateDate=" + updateDate +
            ", createBy='" + createdBy + '\'' +
            ", updateBy='" + lastUpdatedBy + '\'' +
            ", type='" + ruleType + '\'' +
            ", url='" + url + '\'' +
            ", departmentManagerId=" + departmentManagerId +
            ", userName='" + userName + '\'' +
            '}';
    }

}
