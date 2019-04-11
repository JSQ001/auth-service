package com.hand.hcf.app.workflow.brms.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hand.hcf.app.workflow.brms.domain.DroolsRuleDetailResult;
import com.hand.hcf.app.workflow.brms.enums.RuleApprovalEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
public class RuleApprovalNodeDTO {

    private UUID ruleApprovalNodeOid;

    private Integer status;

    private UUID ruleApprovalChainOid;

    private String code;

    private String name;

    private String remark;

    @JsonProperty("type")
    private Integer typeNumber;

    //@JsonIgnore
    private Integer nullableRule;

    /**
     * 会签规则
     */
    //@JsonIgnore
    private Integer countersignRule = RuleApprovalEnum.RULE_CONUTERSIGN_ALL.getId();
    //@JsonIgnore
    private Integer repeatRule;
    //@JsonIgnore
    private Integer selfApprovalRule;

    private Integer sequenceNumber;

    //extend
    private List<RuleApproverDTO> ruleApprovers = new ArrayList<>();

    //节点审批动作 多选择用,分割
    private String approvalActions;

    //后一个节点Oid
    /**
     * 在谁之前
     * 为空标示最后个节点
     */
    private UUID nextRuleApprovalNodeOid;

    private List<UUID> droolsRuleDetailResultOidList = new ArrayList<>();

    //审批人
    private Map<String, Set<UUID>> ruleApproverMap;

    //是否包含自审批跳过
    private Boolean containsSelfApprovalSkip;

    //节点是否审批通过，默认不通过
    private Boolean approvalIsPass = false;
    //当为结束节点时 标记是否可以打印PDF
    private Boolean printFlag;

    //知会配置信息
    private NotifyInfo notifyInfo;

    private ConcurrentHashMap<Long, DroolsRuleDetailResult> droolsBatchCodeRuleResultMap = new ConcurrentHashMap<>();

    private UUID entityOid;

    /**
     * 机器人节点  审批意见
     */
    private String comments;
    /**
     *	能否修改核定金额(0:不允许，1允许)
     *
     */
    private Integer invoiceAllowUpdateType;

}
