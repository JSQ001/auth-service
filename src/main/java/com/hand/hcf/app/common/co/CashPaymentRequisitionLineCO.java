package com.hand.hcf.app.common.co;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Created by ffqiang on 2018/07/12
 */
@Data
public class CashPaymentRequisitionLineCO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 预付单头id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentRequisitionHeaderId;
    /**
     * 关联申请头oid
     */
    private String refDocumentOid;

    //申请单头id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long refDocumentId;

    //申请单code
    private String refDocumentCode;

    //申请单备注
    private String refDocumentRemark;

    //申请单总金额
    private BigDecimal refDocumentTotalAmount;

    //已付款总金额
    private BigDecimal payAmount;

    //已付款币种
    private String payCurrency;

    //退款金额
    private BigDecimal returnAmount;

    //退款币种
    private String returnCurrency;

    /**
     * 租户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;
    /**
     * 公司id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;
    //公司名称
    private String companyName;
    /**
     * 收款方类型
     */
    @NotNull
    private String partnerCategory;
    /**
     * 收款方id
     */
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId;
    /**
     * 收款方代码
     */

    @NotNull
    private String partnerCode;
    /**
     * 银行户名
     */
    @NotNull
    private String accountName;
    /**
     * 银行账号
     */
    @NotNull
    private String accountNumber;
    /**
     * 收款方分行代码
     */

    private String bankBranchCode;
    /**
     * 收款方分行名称
     */
    private String bankBranchName;
    /**
     * 计划付款日期
     */
    private ZonedDateTime requisitionPaymentDate;
    /**
     * 付款方式类型
     */
    private String paymentMethodCategory;
    //付款方式类型中文
    private String paymentMethodName;
    /**
     * 现金事务分类id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionClassId;
    /**
     * 现金流量项id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashFlowId;
    /**
     * 现金流量项代码
     */
    private String cashFlowCode;
    /**
     * 金额
     */
    private BigDecimal amount;
    /**
     * 币种
     */
    private String currency;
    /**
     * 汇率
     */
    private Double exchangeRate;
    /**
     * 本位币金额
     */
    private BigDecimal functionAmount;
    /**
     * 描述
     */
    private String description;

    /*
     * 合同id
     * */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractId;
    /**
     *  合同编号
     */
    private String contractNumber;

    //合同名称
    private String contractName;

    /**
     *  资金计划行号
     */
    private String contractLineNumber;


    private Integer versionNumber;

//    private String refDocumentName;

    //收款方名称
    private String partnerName;

    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    //合同行id
    private Long contractLineId;

    //合同日期
    private String dueDate;

    //预付款类型id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentReqTypeId;

    //预付款类型名称
    private String typeName;

    //现金事务分类名称
    private String cshTransactionClassName;

    private Boolean isEnabled;

    private Boolean isDeleted;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long createdBy;

    private ZonedDateTime createdDate;


    //被核销报账单编号
    private String writeReportNumber;


    //核销信息
    private List<PublicReportWriteOffCO> reportWriteOffDTOS;

    //申请单信息


   private CashPaymentRequisitionHeaderCO prepaymentHead;
}
