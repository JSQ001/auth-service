package com.hand.hcf.app.workflow.dto.monitor;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.util.UUID;

@Data
public class WorkflowJumpDTO {
    @ApiParam(value = "跳转的节点")
    private UUID ruleApprovalNodeOid;

    @ApiParam(value = "单据OID")
    private  UUID entityOid;

    @ApiParam(value = "单据大类")
    private  Integer entityType;
}
