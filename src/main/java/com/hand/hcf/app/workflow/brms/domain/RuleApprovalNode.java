package com.hand.hcf.app.workflow.brms.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * A Application.
 * 节点
 */
@TableName("sys_rule_approval_node")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleApprovalNode extends Domain implements Serializable {

    @NotNull
    private UUID ruleApprovalNodeOid;
    private Integer status;
    @NotNull
    private UUID ruleApprovalChainOid;
    private String code;
    private String name;
    private String remark;
    private Integer typeNumber;

    private Integer nullableRule;
    private Integer countersignRule;
    private Integer repeatRule;
    private Integer selfApprovalRule;
    private Boolean printFlag;
    private Integer sequenceNumber;
    /**
     *节点审批动作
     *
     */
    private String approvalActions;
    private String drl;
	/**
     *	知会配置内容
     */
    private String notifyInfo;
	/**
     *	机器人节点审批意见
     */
    private String comments;
	/**
     *	能否修改核定金额(0:不允许，1允许)
     *
     */
    private Integer invoiceAllowUpdateType;

    @ApiModelProperty(value = "节点页面id")
    @TableField("page_id")
    private Long pageId;

    @ApiModelProperty(value = "是否允许加签")
    @TableField("addsign_flag")
    private Boolean addsignFlag;

    @ApiModelProperty(value = "是否允许转交")
    @TableField("transfer_flag")
    private Boolean transferFlag;

    @ApiModelProperty(value = "驳回后再次提交处理")
    @TableField("reject_rule")
    private Integer rejectRule;

    @ApiModelProperty(value = "允许退回指定节点")
    @TableField("return_flag")
    private Boolean returnFlag;

    @ApiModelProperty(value = "可退回节点")
    @TableField("return_type")
    private Integer returnType;

    @ApiModelProperty(value = "自选节点")
    @TableField("return_nodes")
    private String returnNodes;

    @ApiModelProperty(value = "退回审批通过后处理")
    @TableField("return_rule")
    private Integer returnRule;

    @ApiModelProperty(value = "开启审批流通知")
    @TableField("notify_flag")
    private Boolean notifyFlag;

    @ApiModelProperty(value = "PC消息通知方式")
    @TableField("notify_method_pc")
    private Boolean pcNotifyMethod;

    @ApiModelProperty(value = "APP消息通知方式")
    @TableField("notify_method_app")
    private Boolean appNotifyMethod;

    @ApiModelProperty(value = "邮件通知方式")
    @TableField("notify_method_email")
    private Boolean emailNotifyMethod;
}
