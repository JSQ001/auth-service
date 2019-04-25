package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2018/5/31 17:07
 * @remark 核销反冲 报账单信息
 */
@Data
public class CashWriteOffReserveExpReportDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 报账单头id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expReportHeaderId;
    /**
     * 行号
     */
    private Integer scheduleLineNumber;
    /**
     * 公司id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    /**
     * 描述
     */
    private String description;
    /**
     * 币种
     */
    private String currency;
    /**
     * 汇率
     */
    private Double exchangeRate;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 本币金额
     */
    private BigDecimal functionalAmount;
    /**
     * 计划付款日期
     */
    private ZonedDateTime schedulePaymentDate;
    /**
     * 付款方式大类
     */
    private String paymentMethod;
    /**
     * 现金事务分类id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionClassId;
    /**
     * 现金流量项id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashFlowItemId;
    /**
     * 收款对象类型code
     */
    private String payeeCategory;
    /**
     * 收款对象id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long payeeId;
    /**
     * 收款对象code
     */
    private String payeeCode;
    /**
     * 合同头id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractHeaderId;
    /**
     * 最后更新日期
     */
    private ZonedDateTime lastModifiedDate;
    /**
     * 最后更新用户id
     */
    private Long lastModifiedBy;
    /**
     * 创建日期
     */
    private ZonedDateTime createdDate;
    /**
     * 创建用户id
     */
    private Long createdBy;
    /**
     * 收款对象名称
     */
    private String payeeName;
    /**
     * 合同编号
     */
    private String contractHeaderNumber;
}
