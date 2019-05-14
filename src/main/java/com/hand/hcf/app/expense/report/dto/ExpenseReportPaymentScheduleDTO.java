package com.hand.hcf.app.expense.report.dto;

import com.hand.hcf.app.common.co.CashWriteOffCO;
import com.hand.hcf.app.common.co.ContractHeaderLineCO;
import com.hand.hcf.app.common.co.PublicReportLineAmountCO;
import com.hand.hcf.app.expense.report.domain.ExpenseReportPaymentSchedule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kai.zhang05@hand-china.com
 * @create 2019/3/20 22:07
 * @remark
 */
@ApiModel(description = "费用报账支付计划")
@Data
public class ExpenseReportPaymentScheduleDTO extends ExpenseReportPaymentSchedule {

    /**
     * 收款对象code
     */
    @ApiModelProperty(value = "收款对象code")
    private String payeeCode;
    /**
     * 收款对象名称
     */
    @ApiModelProperty(value = "收款对象名称")
    private String payeeName;
    /**
     * 收款对象类别名称
     */
    @ApiModelProperty(value = "收款对象类别名称")
    private String payeeCategoryName;
    /**
     * 合同资金计划行可关联金额
     */
    @ApiModelProperty(value = "合同资金计划行可关联金额")
    private BigDecimal contractLineAmount;
    /**
     * 付款方式名称
     */
    @ApiModelProperty(value = "付款方式类型名称")
    private String paymentMethodName;
    /**
     * 付款方式名称
     */
    @ApiModelProperty(value = "付款方式名称")
    private String paymentTypeName;
    /**
     * 现金事务分类名称
     */
    @ApiModelProperty(value = "现金事务分类名称")
    private String cshTransactionClassName;
    /**
     * 付款信息
     */
    @ApiModelProperty(value = "付款信息")
    private PublicReportLineAmountCO paidInfo ;
    /**
     * 核销信息
     */
    @ApiModelProperty(value = "核销信息")
    private List<CashWriteOffCO> cashWriteOffMessage;

    /**
     * 合同头行详细信息
     */
    @ApiModelProperty(value = "合同头行详细信息")
    private ContractHeaderLineCO contractHeaderLineMessage;

    /**
     * 序号
     */
    @ApiModelProperty(value = "序号")
    private Integer index;
}
