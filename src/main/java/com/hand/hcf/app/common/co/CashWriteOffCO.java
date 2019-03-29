package com.hand.hcf.app.common.co;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.core.web.dto.DomainObjectDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * 核销明细信息
 * Created by kai.zhang on 2017-10-19.
 */
@Data
public class CashWriteOffCO extends DomainObjectDTO {
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionDetailId;  //支付明细id
    @NotNull
    private String billcode;                //支付流水号
    private String prepaymentRequisitionTypeDesc;          //预付款类型描述
    private String prepaymentRequisitionNumber;            //预付款单据编号
    private ZonedDateTime payDate;              //交易日期(支付时间)
    private BigDecimal prepaymentRequisitionAmount;          //借款金额
    @NotNull
    private BigDecimal unWriteOffAmount;            //借款余额(未核销金额)
    private BigDecimal writeOffAmount;             //本次核销金额
    private BigDecimal writeOffAmountBefore;          //此单据，借款支付明细之前核销记录(核销之后，未提交单据，更新核销金额)
    private ZonedDateTime writeOffDate;           //核销日期
    private String periodName;            //期间
    private String status;                //状态:N未生效;P已生效;Y:已核算
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long tenantId;               //租户id
    private String operationType;        //操作类型(核销:WRITE_OFF；核销反冲:WRITE_OFF_RESERVED)
    private String currencyCode;         //币种
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentHeaderId;      //单据头ID
    @JsonSerialize(using = ToStringSerializer.class)
    private Long documentLineId;        //单据行ID
}
