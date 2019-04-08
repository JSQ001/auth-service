package com.hand.hcf.app.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@TableName( "sys_countersign_detail")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountersignDetail {

    @TableField("entity_oid")
    private UUID entityOid;

    private Integer entityType;

    @TableField("sequence_number")
    private Integer sequence;

    private Integer countersignType;

    @TableField( "countersign_approver_oids")
    private String countersignApprovalOids;

    @TableField( "rule_approval_node_oid")
    private UUID ruleApprovalNodeOid;

    private Integer operationType;
}
