package com.hand.hcf.app.workflow.brms.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 审批流通知动作
 * @author mh.z
 * @date 2019/04/14
 */
@Data
@TableName("sys_rule_notice_action")
public class RuleNoticeAction extends Domain {
    @ApiModelProperty(value = "节点id")
    @TableField("rule_approval_node_id")
    private Long ruleApprovalNodeId;

    @ApiModelProperty(value = "审批流通知id")
    @TableField("rule_notice_id")
    private Long ruleNoticeId;

    @ApiModelProperty(value = "动作类型")
    @TableField("action_type")
    private Integer actionType;
}
