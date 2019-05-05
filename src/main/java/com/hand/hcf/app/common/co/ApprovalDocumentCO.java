package com.hand.hcf.app.common.co;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author mh.z
 * @date 2019/03/22
 * @description 提交审批的单据
 */
@Data
public class ApprovalDocumentCO {
    // 单据id
    private Long documentId;

    // 单据oid
    private UUID documentOid;

    // 单据编号
    private String documentNumber;

    // 单据名称
    private String documentName;

    // 单据类别
    private Integer documentCategory;

    // 单据类型id
    private Long documentTypeId;

    // 单据类型代码
    private String documentTypeCode;

    // 单据类型名称
    private String documentTypeName;

    // 币种
    private String currencyCode;

    // 原币金额
    private BigDecimal amount;

    // 本币金额
    private BigDecimal functionAmount;

    // 公司id
    private Long companyId;

    // 部门oid
    private UUID unitOid;

    // 申请人oid
    private UUID applicantOid;

    // 申请日期
    private ZonedDateTime applicantDate;

    // 备注
    private String remark;

    // 提交人
    private UUID submittedBy;

    // 提交日期
    private ZonedDateTime submitDate;

    // 表单oid
    private UUID formOid;

    // 服务注册到Eureka中的名称
    private String destinationService;
}
