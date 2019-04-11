package com.hand.hcf.app.workflow.dto;

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
public class WorkFlowDocumentRefDTO {
    // 单据oid
    private UUID entityOid;

    // 单据大类
    private Integer documentCategory;

    // 单据大类名称
    private String documentCategoryName;

    // 单据id
    private Long documentId;

    // 单据编号
    private String documentNumber;

    // 单据名称
    private String documentName;

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

    // 单据状态
    private Integer status;

    // 备注
    private String remark;

    //对于被退回单据 rejecterName指驳回人  对于未完成单据 rejecterName指当前审批人
    private String rejecterName;

    //对于被退回单据 nodeName指驳节点  对于未完成单据 nodeName指当前审批节点
    private String nodeName;

    //驳回时间
    private ZonedDateTime rejectTime;

    //提交时间
    private ZonedDateTime applicantDate;//申请日期

    //当前审批人名称
    private String approverName;

}
