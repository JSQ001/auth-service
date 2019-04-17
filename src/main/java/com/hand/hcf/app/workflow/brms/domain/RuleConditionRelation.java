package com.hand.hcf.app.workflow.brms.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * 场景对象关系
 */
@TableName("sys_rule_condition_relation")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleConditionRelation extends Domain implements Serializable {

    @NotNull
    private UUID ruleConditionOid;
    private Integer status;
    private Integer entityType;
    private UUID entityOid;

}
