package com.hand.hcf.app.workflow.brms.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

/**
 * 审批流通知
 * @author mh.z
 * @date 2019/04/14
 */
@Data
@TableName("sys_rule_notice")
public class RuleNotice extends Domain {

    @ApiModelProperty(value = "审批流通知oid")
    @TableField("rule_notice_oid")
    private UUID ruleNoticeOid;

    @ApiModelProperty(value = "节点id")
    @TableField("rule_approval_node_id")
    private Long ruleApprovalNodeId;
}
