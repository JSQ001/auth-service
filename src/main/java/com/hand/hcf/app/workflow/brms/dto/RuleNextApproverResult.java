package com.hand.hcf.app.workflow.brms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class RuleNextApproverResult implements Serializable {

    @JsonProperty("code")
    private String returnCode;
    @JsonProperty("msg")
    private String returnMsg;
    @JsonProperty("time")
    private long timestamp;

    private UUID ruleApprovalChainOid;

    private Integer approvalMode;

    private Integer level;

    private RuleApprovalNodeDTO droolsApprovalNode;
}
