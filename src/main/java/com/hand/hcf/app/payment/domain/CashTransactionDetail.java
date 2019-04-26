package com.hand.hcf.app.payment.domain;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.common.co.AttachmentCO;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * <p>
 * 支付明细表
 * </p>
 *
 * @author baochao.chen@hand-china.com
 * @since 2017-09-29
 */
@Data
@TableName("csh_transaction_detail")
public class CashTransactionDetail extends Domain {

	@TableId
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    /**
     * 租户id
     */
	@TableField("tenant_id")
	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long tenantId;
    /**
     * 支付文件名
     */
	@TableField("payment_file_name")
	private String paymentFileName;
    /**
     * 批次号
     */
	private String paymentBatchNumber;
    /**
     * 支付流水号
     */
    @NotNull
	private String billcode;
    /**
     * 单据公司
     */
    @NotNull
	@TableField("document_company_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentCompanyId;
    /**
     * 付款公司
     */
	@TableField("payment_company_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long paymentCompanyId;
    /**
     * 付款开户公司
     */
	@TableField("drawee_company_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long draweeCompanyId;
    /**
     * 业务大类
     */
    @NotNull
	@TableField("document_category")
	private String documentCategory;

	/**
	 * 业务大类名称
	 */
	@TableField(exist = false)
    private String documentCategoryName;
    /**
     * 单据类型id
     */
    @NotNull
	@TableField("document_type_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentTypeId;

    //单据类型名称
    @TableField(exist = false)
	private String documentTypeName;

    /**
     * 单据id
     */
    @NotNull
	@TableField("document_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentId;
    /**
     * 单据编号
     */
    @NotNull
	@TableField("document_number")
	private String documentNumber;
    /**
     * 单据日期
     */
    @NotNull
	@TableField("requisition_date")
	private ZonedDateTime requisitionDate;
    /**
     * 申请人id
     */
    @NotNull
	@TableField("employee_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long employeeId;
    /**
     * 申请人
     */
    @NotNull
	@TableField("employee_name")
	private String employeeName;

	@TableField(exist = false)
	private String employeeCode;

    /**
     * 单据明细付款行id
     */
	@NotNull
	@TableField("document_line_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentLineId;
    /**
     * 备注字段
     */
	private String remark;
    /**
     * 付款方式id
     */
	@TableField("payment_type_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long paymentTypeId;
    /**
     * 付款方式代码
     */
	@TableField("payment_type_code")
	private String paymentTypeCode;
    /**
     * 付款方式名称
     */
	@TableField("payment_type_name")
	private String paymentTypeName;
    /**
     * 线上支付标识
     */
	@TableField("ebanking_flag")
	private Boolean ebankingFlag;
    /**
     * 支付日期
     */
	@TableField("pay_date")
	private ZonedDateTime payDate;
    /**
     * 支付请求时间
     */
	@TableField("request_time")
	private ZonedDateTime requestTime;
    /**
     * 付款状态
     */
	@NotNull
	@TableField("payment_status")
	private String paymentStatus;

	//付款状态名称
	@TableField(exist = false)
	private String paymentStatusName;

    /**
     * 退票状态
     */
	@NotNull
	@TableField("refund_status")
	private String refundStatus;
    /**
     * 退款状态
     */
	@NotNull
	@TableField("payment_return_status")
	private String paymentReturnStatus;

	/**
	 * 反冲状态
	 */
	@TableField("reserved_status")
	private String reservedStatus;
    /**
     * 收款方类型
     */
	@NotNull
	@TableField("partner_category")
	private String partnerCategory;

	//收款方类型名称
	@TableField(exist = false)
	private String partnerCategoryName;

    /**
     * 收款方id
     */
	@NotNull
	@TableField("partner_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long partnerId;
    /**
     * 收款方代码
     */
	@NotNull
	@TableField("partner_code")
	private String partnerCode;
    /**
     * 收款方名称
     */
	@NotNull
	@TableField("partner_name")
	private String partnerName;
    /**
     * 币种
     */
	@NotNull
	private String currency;
    /**
     * 汇率
     */
	@NotNull
	@TableField("exchange_rate")
	private Double exchangeRate;
    /**
     * 总金额
     */
	@NotNull
	private BigDecimal amount;
    /**
     * 已核销金额
     */
	@TableField("write_off_amount")
	private BigDecimal writeOffAmount;
    /**
     * 现金事务类型代码
     */
	@NotNull
	@TableField("csh_transaction_type_code")
	private String cshTransactionTypeCode;
    /**
     * 现金事务分类id
     */
	@NotNull
	@TableField("csh_transaction_class_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long cshTransactionClassId;
    /**
     * 现金流量项id
     */
	@TableField("cash_flow_item_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long cashFlowItemId;
    /**
     * 计划付款日期
     */
	@TableField("schedule_date")
	private ZonedDateTime scheduleDate;
    /**
     * 付款出纳
     */
	@NotNull
	@TableField("drawee_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long draweeId;

	//出纳人name
	@TableField(exist = false)
	private String draweeName;
    /**
     * 付款方银行账号
     */
	@NotNull
	@TableField("drawee_account_number")
	private String draweeAccountNumber;
    /**
     * 付款方银行户名
     */
	@NotNull
	@TableField("drawee_account_name")
	private String draweeAccountName;
    /**
     * 付款方开户行行号
     */
	@TableField("drawee_bank_number")
	private String draweeBankNumber;
    /**
     * 付款方开户行所在省
     */
	@TableField("drawee_bank_province_code")
	private String draweeBankProvinceCode;
    /**
     * 付款方开户所在市
     */
	@TableField("drawee_bank_city_code")
	private String draweeBankCityCode;
    /**
     * 收款方银行账号
     */
	@NotNull
	@TableField("payee_account_number")
	private String payeeAccountNumber;
    /**
     * 收款方银行户名
     */
	@NotNull
	@TableField("payee_account_name")
	private String payeeAccountName;
    /**
     * 收款方开户行行号
     */
	@NotNull
	@TableField("payee_bank_number")
	private String payeeBankNumber;
    /**
     * 收款方开户行名称
     */
	@NotNull
	@TableField("payee_bank_name")
	private String payeeBankName;
	/**
	 * 收款方开户地
	 */
	@NotNull
	@TableField("payee_bank_address")
	private String payeeBankAddress;
    /**
     * 接口响应码
     */
	@TableField("response_code")
	private String responseCode;
    /**
     * 接口响应信息
     */
	@TableField("response_message")
	private String responseMessage;
    /**
     * 抽挡状态
     */
	@TableField("read_flag")
	private String readFlag;
    /**
     * 回写状态
     */
	@TableField("return_state")
	private String returnState;
    /**
     * 实际结果码
     */
	@TableField("result_code")
	private String resultCode;
    /**
     * 实际结果信息
     */
	@TableField("result_message")
	private String resultMessage;
    /**
     * 对账码
     */
	@TableField("acc_check_code")
	private String accCheckCode;
    /**
     * 对账日期
     */
	@TableField("acc_check_date")
	private ZonedDateTime accCheckDate;
    /**
     * 凭证状态
     */
	@TableField("account_status")
	private Boolean accountStatus;

	/*通用表id*/
	@TableField("csh_transaction_data_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long cshTransactionDataId;


	/**
	 *  退票日期
	 */
	@TableField("refund_date")
	private ZonedDateTime refundDate;

	@TableField("payment_method_category")
	private String paymentMethodCategory;

	@TableField("operation_type")
	private String operationType;//操作类型

	@TableField("contract_header_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long contractHeaderId;//关联合同头id
	@TableField("application_line_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long applicationLineId;//付款单关联的申请单行ID

	@TableField("ref_cash_detail_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long refCashDetailId;//原支付明细ID

	@TableField("ref_bill_code")
	private String refBillCode; //原支付流水号


	@TableField(exist = false)
	private BigDecimal refundAmountCommit;//已提交退款金额

	@TableField(exist = false)
	private BigDecimal refundAmount;//已退款金额

	@TableField(exist = false)
	private BigDecimal abledRefundAmount;//可退款金额


	@TableField("back_flash_attachment_oid")
	private String backFlashAttachmentOids;//反冲附件oids

	@TableField(exist = false)
	private List<String> backlashAttachmentOID;

	@TableField(exist = false)
	private List<AttachmentCO> backlashAttachments;





	@TableField(exist = false)
	private String createdByName;//反冲提交人名称

	@TableField(value = "cheque_number")
	private String chequeNumber;//支票号

	@TableField(value = "entity_oid")
	private String entityOid;  // 单据OID

	@TableField(value = "entity_type")
	private Integer entityType; // 实体类型

	@TableField(exist = false)
	private String approvedMsg; // 退款复核意见
	@TableField(exist = false)
	private String paymentCompanyName;

    /**
     * 付款行号
     */
	private String paymentLineNumber;
	/**
	 * 账套id
	 */
    @NotNull
	private Long setOfBooksId;
    /**
     * 业务单据备注字段
     */
    private String documentLineDescription;
    /**
     * 是否已支付：1已支付，0未支付
     */
    private Integer payedFlag;
    /**
     * 公私标志（对公：BUSINESS；对私：PRIVATE）
     */
    /*private PropFlagEnum propFlag;*/
    /**
     * 回单编号
     */
    private String returnNumber;
}
