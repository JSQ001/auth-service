package com.hand.hcf.app.workflow.workflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;


/**
 * A DTO for the WorkflowRulesSetting entity.
 */
public class WorkflowRulesSettingDTO implements Serializable {

    private UUID workflowRulesSettingOid;//uuid

    @JsonIgnore
    private UUID companyOid;//公司uuid

    @JsonIgnore
    private UUID departmentOid;//部门uuid

    @JsonIgnore
    private UUID costCenterOid;//成本中心uui

    @JsonIgnore
    private UUID costCenterItemOid;//成中心项目uuid

    @NotNull
    private String type;//类型(0 不分条件 1分条件)

    @JsonIgnore
    private List<Double> amount;//金额


    private List<WorkflowRoleDTO> roleList;

    private Integer entityType;//1001 申请 1002报销单

    /**
     * 前台显示字段
     */
    @JsonIgnore
    private String departmentName;//部门名称
    @JsonIgnore
    private String costCenterName;//成中心名称
    @JsonIgnore
    private String costCenterItemName;//成本中心项目名称

    /**
     * 前台显示字段
     */

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getCostCenterName() {
        return costCenterName;
    }

    public void setCostCenterName(String costCenterName) {
        this.costCenterName = costCenterName;
    }

    public String getCostCenterItemName() {
        return costCenterItemName;
    }

    public void setCostCenterItemName(String costCenterItemName) {
        this.costCenterItemName = costCenterItemName;
    }

    public UUID getWorkflowRulesSettingOid() {
        return workflowRulesSettingOid;
    }

    public void setWorkflowRulesSettingOid(UUID workflowRulesSettingOid) {
        this.workflowRulesSettingOid = workflowRulesSettingOid;
    }

    public UUID getCompanyOid() {
        return companyOid;
    }

    public void setCompanyOid(UUID companyOid) {
        this.companyOid = companyOid;
    }

    public UUID getDepartmentOid() {
        return departmentOid;
    }

    public void setDepartmentOid(UUID departmentOid) {
        this.departmentOid = departmentOid;
    }

    public UUID getCostCenterOid() {
        return costCenterOid;
    }

    public void setCostCenterOid(UUID costCenterOid) {
        this.costCenterOid = costCenterOid;
    }

    public UUID getCostCenterItemOid() {
        return costCenterItemOid;
    }

    public void setCostCenterItemOid(UUID costCenterItemOid) {
        this.costCenterItemOid = costCenterItemOid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public List<WorkflowRoleDTO> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<WorkflowRoleDTO> roleList) {
        this.roleList = roleList;
    }

    public List<Double> getAmount() {
        return amount;
    }

    public void setAmount(List<Double> amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "WorkflowRulesSettingDTO{" +
            "workflowRulesSettingOid='" + workflowRulesSettingOid + '\'' +
            ", companyOid='" + companyOid + '\'' +
            ", departmentOid='" + departmentOid + '\'' +
            ", costCenterOid='" + costCenterOid + '\'' +
            ", costCenterItemOid='" + costCenterItemOid + '\'' +
            ", type='" + type + '\'' +
            ", amount=" + amount +
            ", roleList=" + roleList +
            '}';
    }

    public Integer getEntityType() {
        return entityType;
    }

    public void setEntityType(Integer entityType) {
        this.entityType = entityType;
    }
}
