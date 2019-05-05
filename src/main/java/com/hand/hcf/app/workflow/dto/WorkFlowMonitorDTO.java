package com.hand.hcf.app.workflow.dto;


import com.hand.hcf.app.workflow.brms.dto.ReturnNode;
import com.hand.hcf.app.workflow.brms.dto.RuleApprovalChainDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 审批流监控
 */
@Data
public class WorkFlowMonitorDTO{
    @ApiModelProperty(value = "单据Oid")
    private UUID entityOid;

    @ApiModelProperty(value = "单据Oid")
    private UUID documentOid;

    @ApiModelProperty(value = "单据大类")
    private Integer entityType;

    @ApiModelProperty(value = "单据大类")
    private Integer documentCategory;

    @ApiModelProperty(value = "单据大类名称")
    private String documentCategoryName;

    @ApiModelProperty(value = "单据ID")
    private Long documentId;

    @ApiModelProperty(value = "单据编号")
    private String documentNumber;

    @ApiModelProperty(value = "单据名称")
    private String documentName;

    @ApiModelProperty(value = "单据状态")
    private Integer status;

    @ApiModelProperty(value = "申请日期")
    private ZonedDateTime applicantDate;

    @ApiModelProperty(value = "审批节点")
    private String  approvalNodeOid;

    @ApiModelProperty(value = "表单Oid")
    private UUID formOid;

    @ApiModelProperty(value = "审批流名称")
    private String formName;

    @ApiModelProperty(value = "归属类型")
    private String formType;

    @ApiModelProperty(value = "最后审批时间")
    private String lastUpdatedDate;


    @ApiModelProperty(value = "用户OID")
    private UUID userOid;

    @ApiModelProperty(value = "当前审批人")
    private String approverName;

    @ApiModelProperty(value = "最后审批人Oid")
    private UUID lastApproverOid;

    @ApiModelProperty(value = "最后审批人")
    private String lastAppover;

    @ApiModelProperty(value = "创建人")
    private Long createdBy;

    @ApiModelProperty(value = "创建人名称")
    private String createdByName;


}
