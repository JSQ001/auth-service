package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrepaymentApprovalDTO {
    //单据OID
    private String applicantOid;
    //单据CODE
    private String PrepaymentCode;
    //申请人
    private String applicantName;
    //申请人编号
    private String applicantCode;
    //创建时间
    private ZonedDateTime createdDate;
    //提交时间
    private ZonedDateTime submittedDate;
    //表单OID
    private String formOid;
    //表单名称
    private String formName;
    //表单类型
    private String formType;
    //单据oid
    private UUID applicationOid;
    //状态
    private Integer status;
    //驳回类型
    private Integer rejectType;
    //报销单号
    private String businessCode;
    //币种
    private String currencyCode;
    //总金额
    private Double totalAmount;

    /**
     * 部门OID
     */
    private UUID departmentOid;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 是否是个人原因
     */
    private Boolean isPersonalReasom;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;


    /**
     * 预付款单类型id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentReqTypeId;

    private String typeName;
    /**
     * 说明
     */
    private String description;

    //字符串提交日期
    private String StringSubmitDate;
}
