package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @Author: bin.xie
 * @Description: 付款申请单关联通用待付(报账单行)数据DTO
 * @Date: Created in 14:54 2018/4/25
 * @Modified by
 */

@Data
public class CashDataPublicReportLineDTO {
    private String reportNumber;//报账单编号

    @JsonSerialize(using = ToStringSerializer.class)
    private Long scheduleLineId;//报账单计划付款行ID

    private BigDecimal amount;//金额

    private BigDecimal associatedAmount;//已关联金额

    private BigDecimal availableAmount;//可用金额

    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionId; // 来源待付ID
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
     * 本币金额
     */
    private BigDecimal functionalAmount;
    /**
     * 已核销金额
     */
    private BigDecimal writeOffAmount;
    /**
     * 计划付款日期
     */
    private ZonedDateTime schedulePaymentDate;
    /**
     * 付款方式大类
     */
    private String paymentMethod;
    /**
     * 付款方式名称
     */
    private String paymentMethodName;
    /**
     * 现金事务分类id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionClassId;
    /**
     * 现金事务分类名称
     */
    private String cshTransactionClassName;
    /**
     * 现金流量项id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashFlowItemId;
    /**
     * 现金流量项名称
     */
    private String cashFlowItemName;
    /**
     * 收款对象类别code
     */
    private String payeeCategory;
    /**
     * 收款对象类别名称
     */
    private String payeeCategoryName;
    /**
     * 收款对象id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long payeeId;
    /**
     * 收款方代码
     */
    private String payeeCode;
    /**
     * 收款方名称
     */
    private String payeeName;
    /**
     * 银行账号
     */
    private String accountNumber;
    /**
     * 银行户名
     */
    private String accountName;
    /**
     * 银行代码
     */
    private String bankCode;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 分行代码
     */
    private String bankLocationCode;
    /**
     * 分行名称
     */
    private String bankLocationName;
    /**
     * 分行所在省
     */
    private String provinceCode;
    /**
     * 省名称
     */
    private String provinceName;
    /**
     * 分行所在市
     */
    private String cityCode;
    /**
     * 市名称
     */
    private String cityName;
    /**
     * 合同头id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractHeaderId;
    /**
     * 合同资金计划行id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractLineId;
    private String contractNumber;//合同编号
    private Integer contractLineNumber;//行号
    private String contractDueDate;//签订日期

    private String formName; // 表单名称

    private String cshTransactionTypeCode; // 现金事务类型代码
}