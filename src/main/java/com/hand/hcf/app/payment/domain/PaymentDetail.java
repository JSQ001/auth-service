package com.hand.hcf.app.payment.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 14:38 2018/3/7
 * @Modified by
 */
@ApiModel(description = "付款公司配置实体类")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetail extends DomainObjectDTO {

    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;

    @ApiModelProperty(value = "租户id")
    private String billCode; //支付流水号
    @ApiModelProperty(value = "租户id")
    private String customerBatchNo; //支付批次号
    @ApiModelProperty(value = "租户id")
    private String paymentFileName;//支付文件名
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long setOfBooksId;
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentCompanyId; //单据公司ID
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentCompanyId;//付款人公司ID
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long draweeCompanyId;//付款开户公司ID
    @ApiModelProperty(value = "租户id")
    private String draweeAccountNumber;//付款方银行账号
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long employeeId;//员工ID
    @ApiModelProperty(value = "租户id")
    private String documentCategory;//业务大类
    @ApiModelProperty(value = "租户id")
    private String documentNumber;//付款单据编号
    @ApiModelProperty(value = "租户id")
    private String remark;//备注
    @ApiModelProperty(value = "租户id")
    private String paymentMethodCategory; //付款方式大类
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long paymentTypeId; //付款方式ID
    @ApiModelProperty(value = "租户id")
    private ZonedDateTime payDate;//支付日期
    @ApiModelProperty(value = "租户id")
    private String payPeriod;//支付期间
    @ApiModelProperty(value = "租户id")
    private String paymentStatus;//支付状态
    @ApiModelProperty(value = "租户id")
    private String refundStatus;//退票状态
    @ApiModelProperty(value = "租户id")
    private String paymentReturnStatus;//退款状态
    @ApiModelProperty(value = "租户id")
    private String partnerCategory;//收款方类型
    @ApiModelProperty(value = "租户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId;//收款方ID
    @ApiModelProperty(value = "租户id")
    private String partnerCode; //收款方代码
    @ApiModelProperty(value = "租户id")
    private String currency;//币种
    @ApiModelProperty(value = "租户id")
    private Double doucmentExchangeRate; //付款单据汇率
    @ApiModelProperty(value = "租户id")
    private Double exchangeRate;//汇率
    private BigDecimal amount;//金额
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionClassId; //现金事务分类ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashFlowItemId; //现金流量项ID
    private ZonedDateTime accountDate;//账务日期
    private String accountPeriod; //账务期间

    // 20180525新增字段
    private String draweeAccountName;    //付款方银行户名
    private ZonedDateTime documentDate;      //付款单据日期
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId;         //付款单据付款行ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long contractHeaderId;       //关联合同ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sourceDataId;           //来源通用支付数据ID
    private String sourceBillCode;       //原支付流水号
    private String operationType;	    //操作类型
    private String reservedStatus;	     //反冲状态
    private String partnerAccountNumber;     //收款方银行账号
    private String partnerAccountName;       //收款方户名
    private String attribute1;          //备用字段1
    private String attribute2;          //备用字段2
    private String attribute3;          //备用字段3
    private String attribute4;          //备用字段4
    private String attribute5;          //备用字段5
}
