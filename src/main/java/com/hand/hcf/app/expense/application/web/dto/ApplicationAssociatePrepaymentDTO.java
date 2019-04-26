package com.hand.hcf.app.expense.application.web.dto;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;


@Data
public class ApplicationAssociatePrepaymentDTO {
    @NonNull
    private Long applicationId; //申请单ID

    private String applicationNumber; //申请单编号

    private String applicationType; //申请单类型

    private ZonedDateTime requisitionDate; //提交日期

    private String currencyCode; //币种

    private BigDecimal amount; //总金额

    private BigDecimal associatedAmount; //已关联金额

    private BigDecimal associableAmount; //可关联金额

    private String remarks; // 备注

    /**
     * 部门
     * */
    private Long departmentId;

    private String departmentName;


    private Long employeeId;

    private String name;
    /**
     * 头像 暂无
     * */
    private String  iconUrl;

    public ApplicationAssociatePrepaymentDTO() {}

}
