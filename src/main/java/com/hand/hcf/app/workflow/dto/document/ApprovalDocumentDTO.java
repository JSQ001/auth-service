package com.hand.hcf.app.workflow.dto.document;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author mh.z
 * @date 2019/03/07
 * @description 未审批已审批的单据
 */
@Getter
@Setter
@ApiModel(value="未审批已审批的单据记录")
public class ApprovalDocumentDTO {
    @ApiModelProperty(value="单据oid")
    private UUID entityOid;

    @ApiModelProperty(value="单据大类")
    private Integer entityType;

    @ApiModelProperty(value="单据id")
    private Long documentId;

    @ApiModelProperty(value="单据编号")
    private String documentNumber;

    @ApiModelProperty(value="单据名称")
    private String documentName;

    @ApiModelProperty(value="单据类型id")
    private Long documentTypeId;

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

    @ApiModelProperty(value="单据人名称")
    private String applicantName;

    @ApiModelProperty(value="提交日期")
    private ZonedDateTime submittedDate;

    @ApiModelProperty(value="申请日期")
    private ZonedDateTime applicantDate;

    @ApiModelProperty(value="单据状态")
    private Integer status;

    @ApiModelProperty(value="备注")
    private String remark;
}
