package com.hand.hcf.app.common.co;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @description:
 * @version: 1.0
 * @author: qianjun.gong@hand-china.com
 * @date: 2019/4/10
 */
@Data
public class ExpensePaymentScheduleCO {

    private Long id;
    /**
     * 对公报账头ID
     */
    private Long expReportHeaderId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 账套ID
     */
    private Long setOfBooksId;

    /**
     * 公司ID
     */
    private Long companyId;

    /**
     * 部门ID
     */
    private Long departmentId;

    /**
     * 申请人ID
     */
    private Long applicantId;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 币种
     */
    private String currencyCode;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 本币金额
     */
    private BigDecimal functionAmount;

    /**
     * 备注
     */
    private String description;

    /**
     * 付款方式类型（线上：ONLINE_PAYMENT；线下：OFFLINE_PAYMENT；落地文件：EBANK_PAYMENT)
     */
    private String paymentMethod;

    /**
     * 付款用途ID
     */
    private Long cshTransactionClassId;

    /**
     * 现金流ID
     */
    private Long cashFlowItemId;

    /**
     * 计划付款日期
     */
    private ZonedDateTime paymentScheduleDate;

    /**
     * 关联合同资金计划ID
     */
    private Long conPaymentScheduleLineId;

    /**
     * 收款方类型（员工：EMPLOYEE；供应商：VENDER）
     */
    private String payeeCategory;

    /**
     * 收款方id
     */
    private Long payeeId;

    /**
     * 收款方账户
     */
    private String accountNumber;

    /**
     * 收款方户名
     */
    private String accountName;

    /**
     * 冻结状态（Y/N）
     */
    private String frozenFlag;

    /**
     * 核销金额
     */
    private BigDecimal writeOffAmount;

    /**
     * 审核状态
     */
    private String auditFlag;

    /**
     * 审核日期
     */
    private ZonedDateTime auditDate;

    private ZonedDateTime requisitionDate;

    private String reportTypeName;

}
