package com.hand.hcf.app.prepayment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.co.CashPaymentRequisitionHeaderCO;
import com.hand.hcf.app.core.domain.Domain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * <p>
 * 预付款单行表
 * </p>
 *
 * @author baochao.chen@hand-china.com
 * @since 2017-10-26
 */
@ApiModel(description = "预付款单行表实体类")
@Data
@TableName("csh_payment_requisition_line")
public class CashPaymentRequisitionLine extends Domain {
	/**
	 * 预付单头id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableField("payment_requisition_header_id")
	private Long paymentRequisitionHeaderId;
	/**
	 * 关联申请id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableField(value = "ref_document_id")
	private Long refDocumentId;


	@TableField(value = "ref_document_code")
	private String refDocumentCode;
	/**
	 * 租户id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableField("tenant_id")
	private Long tenantId;
	/**
	 * 公司id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableField("company_id")
	private Long companyId;
	/**
	 * 收款方类型
	 */
	@TableField("partner_category")
	private String partnerCategory;
	/**
	 * 收款方id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableField("partner_id")
	private Long partnerId;
	/**
	 * 收款方代码
	 */
	@TableField("partner_code")
	private String partnerCode;
	/**
	 * 银行户名
	 */
	@TableField("account_name")
	private String accountName;
	/**
	 * 银行账号
	 */
	@TableField("account_number")
	private String accountNumber;
	/**
	 * 收款方分行代码
	 */
	@TableField(value = "bank_branch_code")
	private String bankBranchCode;
	/**
	 * 收款方分行名称
	 */
	@TableField(value = "bank_branch_name")
	private String bankBranchName;
	/**
	 * 计划付款日期
	 */
	@TableField("requisition_payment_date")
	private ZonedDateTime requisitionPaymentDate;
	/**
	 * 付款方式类型
	 */
	@TableField("payment_method_category")
	private String paymentMethodCategory;

	/**
	 * 付款方式 (付款方式值列表：ZJ_PAYMENT_TYPE)
	 */
	@ApiModelProperty(value = "付款方式")
	@TableField("payment_type")
	@NotNull
	private String paymentType;

	/**
	 * 账户属性 (“对私”（PRIVATE）和“对公”（BUSINESS）)
	 */
	@ApiModelProperty(value = "账户属性")
	@TableField("prop_flag")
	@NotNull
	private String propFlag;

	/**
	 * 现金事务分类id
	 */
	@TableField("csh_transaction_class_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long cshTransactionClassId;
	/**
	 * 现金流量项id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	@TableField("cash_flow_id")
	private Long cashFlowId;
	/**
	 * 现金流量项代码
	 */
	@TableField("cash_flow_code")
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
	@TableField("exchange_rate")
	private Double exchangeRate;
	/**
	 * 本位币金额
	 */
	@TableField("function_amount")
	private BigDecimal functionAmount;
	/**
	 * 描述
	 */
	private String description;

	/**
	 *  合同编号
	 */
	@TableField(value = "contract_number")
	private String contractNumber;

	/**
	 *  资金计划行号
	 */
	@TableField(value = "contract_line_number")
	private String contractLineNumber;

	/*合同头id*/
	@TableField(value = "contract_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long contractId;

	@TableField(value = "ref_document_name")
	private String refDocumentName;

	@TableField("partner_name")
	private String partnerName;

	@JsonSerialize(using = ToStringSerializer.class)
	@TableField(value = "contract_line_id")
	private Long contractLineId;

	@TableField(value = "due_date")
	private ZonedDateTime dueDate;

	@TableField(exist = false)
	private CashPaymentRequisitionHeaderCO prepaymentHead;

	public Double AmountToDouble(){
		return amount != null ? amount.doubleValue() : 0D;
	}
}
