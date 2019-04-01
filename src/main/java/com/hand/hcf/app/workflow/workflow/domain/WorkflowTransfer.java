package com.hand.hcf.app.workflow.workflow.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/18 17:25
 * @version: 1.0.0
 */
@TableName("sys_workflow_transfer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTransfer extends Domain{
    //租户Id
    private Long tenantId;

    //账套Id
    private Long setOfBooksId;

    //授权人Id
    private Long authorizerId;

    //单据大类
    @TableField(value = "document_category",strategy = FieldStrategy.IGNORED)
    private String documentCategory;

    //审批流
    @TableField(value = "workflow_id",strategy = FieldStrategy.IGNORED)
    private Long workflowId;

    //代理人ID
    private Long agentId;

    //有效日期从
    private ZonedDateTime startDate;

    //有效日期至
    private ZonedDateTime endDate;

    //备注
    private String authorizationNotes;

}
