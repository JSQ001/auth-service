package com.hand.hcf.app.prepayment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

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
public class CashTransactionDataDTO extends DomainObjectDTO {

    /**
     * 租户id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull
	private Long tenantId;
    /**
     * 业务大类
     */
	@NotNull
	private String documentCategory;
    /**
     * 所属单据头id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull
	private Long documentHeaderId;
    /**
     * 单据编号
     */
	@NotNull
	private String documentNumber;
    /**
     * 申请人id
     */


	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long employeeId;
    /**
     * 申请人
     */
	@NotNull
	private String employeeName;
    /**
     * 申请日期
     */
	@NotNull
//	@JsonDeserialize(using = ZonedDateTimeDeserializer.class)
	private String requisitionDate;
    /**
     * 待付行id
     */
	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentLineId;
    /**
     * 公司id
     */

	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long companyId;
    /**
     * 付款机构
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long paymentCompanyId;
    /**
     * 总金额
     */
    @NotNull
	private Double amount;
    /**
     * 已提交金额
     */
	private Double commitedAmount;
    /**
     * 已支付金额
     */
//	@NotNull
	private Double paidAmount;
    /**
     * 已核销金额
     */
	@NotNull
	private Double writeOffAmount;
    /**
     * 币种
     */

	@NotNull
	private String currency;
    /**
     * 汇率
     */
//	@NotNull
	private Double exchangeRate;
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
     * 收款方名称
     */
	@NotNull
	private String partnerName;
    /**
     * 收款方银行户名
     */
	@NotNull
	private String accountName;
    /**
     * 收款方银行账号
     */
	@NotNull
	private String accountNumber;
    /**
     * 收款方银行代码
     */

	private String bankCode;
    /**
     * 收款方银行名称
     */

	private String bankName;
    /**
     * 收款方分行代码
     */

	@NotNull
	private String bankBranchCode;
    /**
     * 收款方分行名称
     */

	@NotNull
	private String bankBranchName;
    /**
     * 收款方分行所在省份代码
     */

	private String provinceCode;
    /**
     * 收款方分行所在省份名称
     */

	private String provinceName;
    /**
     * 收款方分行所在城市代码
     */

	private String cityCode;
    /**
     * 收款方分行所在城市名称
     */

	private String cityName;
    /**
     * 付款方式类型
     */

	@NotNull
	private String paymentMethodCategory;
    /**
     * 计划付款日期
     */

	private String requisitionPaymentDate;
    /**
     * 现金事务类型代码
     */

	@NotNull
	private String cshTransactionTypeCode;
    /**
     * 现金事务分类id
     */

	@NotNull
	@JsonSerialize(using = ToStringSerializer.class)
	private Long cshTransactionClassId;
    /**
     * 现金流量项id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long cshFlowItemId;
    /**
     * 关联合同头id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long contractHeaderId;
    /**
     * 分期id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long instalmentId;
    /**
     * 描述
     */
	private String remark;

    /**
     * 是否冻结
     */
	private Boolean frozenFlag;
    /**
     * 支付状态
     */
	private String paymentStatus;

	private String attribute1;
	private String attribute2;
	private String attribute3;
	private String attribute4;
	private String attribute5;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentTypeId;//单据类型ID
	private String documentTypeName;//单据类型名称

	@JsonSerialize(using = ToStringSerializer.class)
	private Long sourceDataId;//来源通用支付信息表ID
	@JsonSerialize(using = ToStringSerializer.class)
	private Long sourceHeaderId;//来源单据头ID
	@JsonSerialize(using = ToStringSerializer.class)
	private Long sourceLineId;//来源单据行ID

	//关联的申请单行id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long applicationLineId;



	private String entityOid;  // 单据OID


	private Integer entityType; // 实体类型
}
