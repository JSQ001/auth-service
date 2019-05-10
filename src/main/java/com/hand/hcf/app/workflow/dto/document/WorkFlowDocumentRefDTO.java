package com.hand.hcf.app.workflow.dto.document;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @description: WorkFlowDocumentRefDTO
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/3/12
 */
@Data
@ApiModel(value="单据记录")
public class WorkFlowDocumentRefDTO {
    @ApiModelProperty(value="单据oid")
    private UUID entityOid;

    @ApiModelProperty(value="单据大类")
    private Integer documentCategory;

    @ApiModelProperty(value="单据名称")
    private String documentCategoryName;

    @ApiModelProperty(value="单据id")
    private Long documentId;

    @ApiModelProperty(value="单据编号")
    private String documentNumber;

    @ApiModelProperty(value="单据名称")
    private String documentName;

    @ApiModelProperty(value="单据类型代码")
    private String documentTypeCode;

    @ApiModelProperty(value="单据类型名称")
    private String documentTypeName;

    @ApiModelProperty(value="币种")
    private String currencyCode;

    @ApiModelProperty(value="金额")
    private BigDecimal amount;

    @ApiModelProperty(value="本币金额")
    private BigDecimal functionAmount;

    @ApiModelProperty(value="申请人oid")
    private UUID applicantOid;

    @ApiModelProperty(value="申请人名称")
    private String applicantName;

    @ApiModelProperty(value="提交日期")
    private ZonedDateTime submittedDate;

    @ApiModelProperty(value="单据状态")
    private Integer status;

    @ApiModelProperty(value="备注")
    private String remark;

    @ApiModelProperty(value="对于被退回单据 approverOid指驳回人Oid  对于未完成单据 approverOid指当前审批人Oid")
    private UUID approverOid;

    @ApiModelProperty(value="对于被退回单据 rejecterName指驳回人  对于未完成单据 rejecterName指当前审批人")
    private String rejecterName;

    @ApiModelProperty(value="对于被退回单据 nodeName指驳节点  对于未完成单据 nodeName指当前审批节点")
    private String nodeName;

    @ApiModelProperty(value="驳回时间")
    private ZonedDateTime rejectTime;

    @ApiModelProperty(value="提交时间")
    private ZonedDateTime applicantDate;

    @ApiModelProperty(value="当前审批人名称")
    private String approverName;

    @ApiModelProperty(value="表单oid")
    private UUID formOid;

    @ApiModelProperty(value="单据类型id")
    private Long documentTypeId;

    @ApiModelProperty(value = "申请人部门id")
    private Long departmentId;

    @ApiModelProperty(value = "申请人部门名称")
    private String departmentName;
}
