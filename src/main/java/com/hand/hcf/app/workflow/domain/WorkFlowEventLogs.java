package com.hand.hcf.app.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

import java.util.UUID;

/**
 * Created by houyin.zhang@hand-china.com on 2018/12/12.
 * 单据工作流 关联审批人表
 */
@Data
@TableName("sys_wfl_ref_event_log")
public class WorkFlowEventLogs extends Domain {
    @TableField("event_id")
    private String eventId;//事件消息Oid
    @TableField("event_confirm_status")
    private Boolean eventConfirmStatus;
    @TableField("document_oid")
    private UUID documentOid;//单据Oid
    @TableField("document_category")
    private Integer documentCategory;//单据大类 801001 对公报账单，801002 预算日记账 801003 预付款单 801004 合同  801005 付款申请单 801006 费用调整单 801007 费用反冲 801008 核算工单  801009 费用申请单
    //表示服务注册到Eureka中的名称(如：prepayment:预付款，contract:合同, budget:预算)，这样能保证每次只对具体的服务发布消息
    @TableField("destination_service")
    private String destinationService;
}
