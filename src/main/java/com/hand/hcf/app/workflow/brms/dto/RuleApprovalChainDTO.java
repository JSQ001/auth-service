package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
public class RuleApprovalChainDTO {
    List<RuleApprovalNodeDTO> ruleApprovalNodes;
    RuleSceneDTO ruleScene;
    private UUID ruleApprovalChainOid;
    private UUID formOid;
    private String code;
    private String name;

    private String remark;
    private Integer status;

    private UUID ruleSceneOid;

    private Integer approvalMode;

    private Integer level;

    private Boolean checkData;
}
