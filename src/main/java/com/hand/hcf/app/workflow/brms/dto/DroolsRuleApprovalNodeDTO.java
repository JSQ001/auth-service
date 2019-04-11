package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hand.hcf.app.workflow.dto.FormValueDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class DroolsRuleApprovalNodeDTO {

    //节点Oid
    private UUID ruleApprovalNodeOid;

    //自定义表单values
    private List<FormValueDTO> formValues = new ArrayList<>();

    private boolean customMessageDisplayFlg = Boolean.TRUE;

    //申请人Oid
    private UUID applicantOid;

    //表单Oid
    private UUID formOid;

    //审批者
    List<RuleApproverDTO> ruleApproverDTOs;

    //实体类型  (标记实体类型和Oid查询对应的分摊审批人)
    private Integer entityType;

    //实体Oid
    private UUID entityOid;

    //单据信息
    private Map<String, Object> entityData;

    private RuleApprovalNodeDTO ruleApprovalNodeDTO;

    private List<String> drlContentList;
}
