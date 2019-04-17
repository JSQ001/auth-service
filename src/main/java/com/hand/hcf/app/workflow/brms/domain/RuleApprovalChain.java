package com.hand.hcf.app.workflow.brms.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 *
 * 审批链
 */
@TableName("sys_rule_approval_chain")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleApprovalChain extends Domain {

    @NotNull
    private UUID ruleApprovalChainOid;
    private Integer status;
    @NotNull
    private String code;
    private String name;
    private String remark;
    @NotNull
    private UUID ruleSceneOid;
    private Integer approvalMode;
    private Integer levelNumber;

}
