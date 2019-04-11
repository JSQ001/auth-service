package com.hand.hcf.app.workflow.dto;


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
public class ApprovalDocumentDTO {
    // 单据oid
    private UUID entityOid;

    // 单据大类
    private Integer entityType;

    // 单据id
    private Long documentId;

    // 单据编号
    private String documentNumber;

    // 单据名称
    private String documentName;

    // 单据类型id
    private Long documentTypeId;

    // 单据类型代码
    private String documentTypeCode;

    // 单据类型名称
    private String documentTypeName;

    // 币种
    private String currencyCode;

    // 金额
    private BigDecimal amount;

    // 本币金额
    private BigDecimal functionAmount;

    // 申请人oid
    private UUID applicantOid;

    // 申请人名称
    private String applicantName;

    // 提交日期
    private ZonedDateTime submittedDate;

    // 申请日期
    private ZonedDateTime applicantDate;

    // 单据状态
    private Integer status;

    // 备注
    private String remark;
}
