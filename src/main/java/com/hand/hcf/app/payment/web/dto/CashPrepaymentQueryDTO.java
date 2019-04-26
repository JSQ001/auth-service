package com.hand.hcf.app.payment.web.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @description: 报销单核销借款：查询
 * @version: 1.0
 * @author: wenzhou.tang@hand-china.com
 * @date: 2017/11/1 11:11
 */

@Data
public class CashPrepaymentQueryDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tenantId;//租户id

    @JsonSerialize(using = ToStringSerializer.class)
    private Long companyId;//公司ID

    private String documentCategory;//业务大类

    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentTypeId;//单据类型ID

    private String documentTypeName;//单据类型

    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentHeaderId;//所属单据头ID

    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId;//待付行ID

    private String documentNumber;//单据编号

    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashTransactionDetailId;//支付明细ID

    private String billCode;//支付流水号

    private ZonedDateTime payDate;//支付日期

    private BigDecimal amount;//总金额

    private BigDecimal writeOffAmount;//可核销金额

    private String  cshTransactionClassName;//现金事务类型描述

    private String partnerCategory;//收款方类型

    @JsonSerialize(using = ToStringSerializer.class)
    private Long partnerId;//收款方ID

    private String partnerCode;//收款方代码

    private String partnerName;//收款方名称

    private BigDecimal WriteOffAmountForThisDocument;     //本单据核销金额

    private String currencyCode;                  //币种

}
