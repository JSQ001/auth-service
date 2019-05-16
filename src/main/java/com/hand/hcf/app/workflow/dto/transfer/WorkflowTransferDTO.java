package com.hand.hcf.app.workflow.dto.transfer;

import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * @author shaofeng.zheng@hand-china.com
 * @description
 * @date 2019/1/21 13:55
 * @version: 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTransferDTO extends Domain {
    //租户Id
    private Long tenantId;

    //账套Id
    private Long setOfBooksId;

    //授权人Id
    private Long authorizerId;

    //授权人工号
    private String authorizerCode;

    //授权人名称
    private String authorizerName;

    //单据大类
    private String documentCategory;

    private String documentCategoryName;

    //审批流
    private Long workflowId;

    //审批流名称
    private String workflowName;

    //代理人ID
    private Long agentId;

    //代理人工号
    private String agentCode;

    //代理人名称
    private String agentName;

    //有效日期从
    private ZonedDateTime startDate;

    //有效日期至
    private ZonedDateTime endDate;

    //备注
    private String authorizationNotes;
}
