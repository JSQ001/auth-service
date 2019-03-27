package com.hand.hcf.app.workflow.brms.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Created by Nick on 16/12/19.
 * 场景
 */
@TableName( "sys_rule_scene")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleScene extends Domain {

    @NotNull
    private UUID ruleSceneOid;

    private Integer status;

    private String code;

    private String name;

    private String remark;

    @NotNull
    private UUID companyOid;

    private int sequenceNumber;
}
