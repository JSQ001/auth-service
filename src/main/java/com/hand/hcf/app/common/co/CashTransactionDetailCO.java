package com.hand.hcf.app.common.co;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.web.dto.DomainObjectDTO;
import lombok.Data;

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
public class CashTransactionDetailCO extends DomainObjectDTO {

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
    /**
     * 租户id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long tenantId;
    /**
     * 支付文件名
     */

	private String paymentFileName;
    /**
     * 批次号
     */

	private String customerBatchNo;
    /**
     * 支付流水号
     */
	private String billcode;
    /**
     * 单据公司
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentCompanyId;
    /**
     * 付款公司
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long paymentCompanyId;
    /**
     * 付款开户公司
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long draweeCompanyId;
    /**
     * 业务大类
     */
	private String documentCategory;

	/**
	 * 业务大类名称
	 */
    private String documentCategoryName;
    /**
     * 单据类型id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentTypeId;


	private String documentTypeName; // 单据类型名称

    /**
     * 单据id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentId;
    /**
     * 单据编号
     */
	private String documentNumber;
    /**
     * 单据日期
     */
	private ZonedDateTime requisitionDate;
    /**
     * 申请人id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long employeeId;
    /**
     * 申请人
     */
	private String employeeName;
    /**
     * 单据明细付款行id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long documentLineId;
    /**
     * 备注字段
     */
	private String remark;
    /**
     * 付款方式id
     */
	private Long paymentTypeId;
    /**
     * 付款方式代码
     */
	private String paymentTypeCode;
    /**
     * 付款方式名称
     */
	private String paymentTypeName;
    /**
     * 线上支付标识
     */
	private Boolean ebankingFlag;
    /**
     * 支付日期
     */
	private ZonedDateTime payDate;
    /**
     * 支付请求时间
     */
	private ZonedDateTime requestTime;
    /**
     * 付款状态
     */
	private String paymentStatus;

	//付款状态名称
	private String paymentStatusName;

    /**
     * 退票状态
     */
	private String refundStatus;
    /**
     * 退款状态
     */
	private String paymentReturnStatus;

	/**
	 * 反冲状态
	 */
	private String reservedStatus;
    /**
     * 收款方类型
     */
	private String partnerCategory;

	//收款方类型名称
	private String partnerCategoryName;

    /**
     * 收款方id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long partnerId;
    /**
     * 收款方代码
     */
	private String partnerCode;
    /**
     * 收款方名称
     */
	private String partnerName;
    /**
     * 币种
     */
	private String currency;
    /**
     * 汇率
     */
	private Double exchangeRate;
    /**
     * 总金额
     */
	private BigDecimal amount;
    /**
     * 已核销金额
     */
	private Double writeOffAmount;
    /**
     * 现金事务类型代码
     */
	private String cshTransactionTypeCode;
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
     * 计划付款日期
     */
	private ZonedDateTime scheduleDate;
    /**
     * 付款出纳
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long draweeId;

	//出纳人name
	private String draweeName;
    /**
     * 付款方银行账号
     */
	private String draweeAccountNumber;
    /**
     * 付款方银行户名
     */
	private String draweeAccountName;
    /**
     * 付款方开户行行号
     */
	private String draweeBankNumber;
    /**
     * 付款方开户行所在省
     */
	private String draweeBankProvinceCode;
    /**
     * 付款方开户所在市
     */
	private String draweeBankCityCode;
    /**
     * 收款方银行账号
     */
	private String payeeAccountNumber;
    /**
     * 收款方银行户名
     */
	private String payeeAccountName;
    /**
     * 收款方开户行行号
     */
	private String payeeBankNumber;
    /**
     * 收款方开户行名称
     */
	private String payeeBankName;
	/**
	 * 收款方开户地
	 */
	private String payeeBankAddress;
    /**
     * 接口响应码
     */
	private String responseCode;
    /**
     * 接口响应信息
     */
	private String responseMessage;
    /**
     * 抽挡状态
     */
	private String readFlag;
    /**
     * 回写状态
     */
	private String returnState;
    /**
     * 实际结果码
     */
	private String resultCode;
    /**
     * 实际结果信息
     */
	private String resultMessage;
    /**
     * 对账状态码
     */
	private String accCheckCode;
    /**
     * 对账日期
     */
	private ZonedDateTime accCheckDate;
    /**
     * 凭证状态
     */
	private Boolean accountStatus;

	/*通用表id*/
	@JsonSerialize(using = ToStringSerializer.class)
	private Long cshTransactionDataId;


	/**
	 *  退票日期
	 */
	private ZonedDateTime refundDate;

	private String paymentMethodCategory;

	private String operationType;//操作类型

	@JsonSerialize(using = ToStringSerializer.class)
	private Long contractHeaderId;//关联合同头id
	@JsonSerialize(using = ToStringSerializer.class)
	private Long applicationLineId;//付款单关联的申请单行ID

	private Long refCashDetailId;//原支付明细ID

	private String refBillCode; //原支付流水号


	private BigDecimal refundAmountCommit;//已提交退款金额

	private BigDecimal refundAmount;//已退款金额

	private BigDecimal abledRefundAmount;//可退款金额


	private String backFlashAttachmentOIDs;//反冲附件oids

	private List<String> backlashAttachmentOID;

	private List<AttachmentCO> backlashAttachments;

	private String chequeNumber;//支票号

	private String entityOid;  // 单据OID

	private Integer entityType; // 实体类型


	private Integer versionNumber; // 版本号

	/**
	 * 批次号
	 */
	private String paymentBatchNumber;
	/**
	 * 付款行号
	 */
	private String paymentLineNumber;
	/**
	 * 账套id
	 */
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
	private Integer propFlag;
	/**
	 * 回单编号
	 */
	private String returnNumber;
}
