package com.hand.hcf.app.payment.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.domain.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * <p>
 * 通用支付信息表
 * </p>
 *
 * @author baochao.chen@hand-china.com
 * @since 2017-09-29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("csh_transaction_data")
public class CashTransactionData extends Domain {

    /**
     * 租户id
     */
	@TableField("tenant_id")
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull
	private Long tenantId;
    /**
     * 业务大类
     */
	@NotNull
	@TableField("document_category")
	private String documentCategory;
    /**
     * 所属单据头id
     */
	@TableField("document_header_id")
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull
	private Long documentHeaderId;
    /**
     * 单据编号
     */
	@NotNull
	@TableField("document_number")
	private String documentNumber;
    /**
     * 申请人id
     */

	@TableField("employee_id")
	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long employeeId;
    /**
     * 申请人
     */
	@TableField("employee_name")
	private String employeeName;

	@TableField(exist = false)
	private String employeeCode;
    /**
     * 申请日期
     */
	@TableField("requisition_date")
	private ZonedDateTime requisitionDate;
    /**
     * 待付行id
     */
	@TableField("document_line_id")
	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentLineId;
    /**
     * 公司id
     */
	@TableField("company_id")
	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long companyId;
    /**
     * 付款机构
     */
	@TableField("payment_company_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long paymentCompanyId;
    /**
     * 总金额
     */
    @NotNull
	private BigDecimal amount;
    /**
     * 已提交金额
     */
	@TableField(exist = false)
	private BigDecimal commitedAmount;
    /**
     * 已支付金额
     */
	@TableField(exist = false)
	private BigDecimal paidAmount;
    /**
     * 提交核销金额
     */
    @TableField(exist = false)
	private BigDecimal writeOffAmount;
    /**
     * 币种
     */
	@TableField("currency")
	@NotNull
	private String currency;
    /**
     * 汇率
     */
	@TableField("exchange_rate")
	private Double exchangeRate;
    /**
     * 收款方类型
     */
	@TableField("partner_category")
	@NotNull
	private String partnerCategory;
    /**
     * 收款方id
     */
	@TableField("partner_id")
	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long partnerId;
    /**
     * 收款方代码
     */
	@TableField("partner_code")
	private String partnerCode;
    /**
     * 收款方名称
     */
	@TableField("partner_name")
	private String partnerName;
    /**
     * 收款方银行户名
     */
	@TableField("account_name")
	private String accountName;
    /**
     * 收款方银行账号
     */
	@TableField("account_number")
	@NotNull
	private String accountNumber;
    /**
     * 收款方银行代码
     */
	@TableField("bank_code")
	private String bankCode;
    /**
     * 收款方银行名称
     */
	@TableField("bank_name")
	private String bankName;
    /**
     * 收款方分行代码
     */
	@TableField("bank_branch_code")
	private String bankBranchCode;
    /**
     * 收款方分行名称
     */
	@TableField("bank_branch_name")
	private String bankBranchName;
    /**
     * 收款方分行所在省份代码
     */
	@TableField("province_code")
	private String provinceCode;
    /**
     * 收款方分行所在省份名称
     */
	@TableField("province_name")
	private String provinceName;
    /**
     * 收款方分行所在城市代码
     */
	@TableField("city_code")
	private String cityCode;
    /**
     * 收款方分行所在城市名称
     */
	@TableField("city_name")
	private String cityName;
    /**
     * 付款方式类型
     */
	@TableField("payment_method_category")
	@NotNull
	private String paymentMethodCategory;
    /**
     * 计划付款日期
     */
	@TableField("requisition_payment_date")
	private ZonedDateTime requisitionPaymentDate;
    /**
     * 现金事务类型代码
     */
	@TableField("csh_transaction_type_code")
	@NotNull
	private String cshTransactionTypeCode;
    /**
     * 现金事务分类id
     */
	@TableField("csh_transaction_class_id")
	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long cshTransactionClassId;
    /**
     * 现金流量项id
     */
	@TableField("csh_flow_item_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long cshFlowItemId;
    /**
     * 关联合同头id
     */
	@TableField("contract_header_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long contractHeaderId;
    /**
     * 分期id
     */
	@TableField("instalment_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long instalmentId;
    /**
     * 描述
     */
	private String remark;

    /**
     * 是否冻结
     */
	@TableField("frozen_flag")
	private Boolean frozenFlag;
    /**
     * 支付状态
     */
	@TableField(exist=false)
	private String paymentStatus;

	@TableField("attribute1")
	private String attribute1;
	@TableField("attribute2")
	private String attribute2;
	@TableField("attribute3")
	private String attribute3;
	@TableField("attribute4")
	private String attribute4;
	@TableField("attribute5")
	private String attribute5;

	@TableField("document_type_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentTypeId;//单据类型ID
	@TableField("document_type_name")
	private String documentTypeName;//单据类型名称

	@TableField("source_data_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long sourceDataId;//来源通用支付信息表ID
	@TableField("source_header_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long sourceHeaderId;//来源单据头ID
	@TableField("source_line_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long sourceLineId;//来源单据行ID
	@TableField("application_line_id")
	@JsonSerialize(using = ToStringSerializer.class)
	private Long applicationLineId;//付款单关联的申请单行ID

	@TableField(exist = false)
	private BigDecimal returnAmount;

	@TableField(exist = false)
	private String cshTransactionClassName;

	@TableField(value = "entity_oid")
	@NotNull
	private String entityOid;  // 单据OID

	@TableField(value = "entity_type")
	@NotNull
	private Integer entityType; // 实体类型

	@TableField(exist = false)
	private BigDecimal writeOffTotalAmount;// 已核销金额

}
