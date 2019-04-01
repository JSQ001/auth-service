package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
public class RuleApproverDTO implements Serializable{

    private UUID ruleApproverOid;

    private Integer status;
    private String code;

    private String name;

    private String remark;

    private Integer approverType;

    private UUID approverEntityOid;

    private Integer levelNumber;

    private UUID ruleApprovalNodeOid;

    private Integer ruleApprovalNodeSequence;
    private List<RuleApproverDTO> ruleApprovers = new ArrayList<>();

    private Map<Long, List<RuleConditionDTO>> ruleConditions;

    @JsonIgnore
    private List<RuleConditionDTO> ruleConditionList;

    //包含分摊成本中心主管
    private Boolean containsApportionmentCostCenterManager;

    //包含分摊组织架构主管
    private Boolean containsApportionmentDepartmentManager;

    //包含分摊成本中心主要部门的部门经理
    private Boolean containsApportionmentCostCenterPrimaryDepartmentManager;

    //部门来源(1为申请人 2为单据上的部门)
    private Integer departmentType;

}
