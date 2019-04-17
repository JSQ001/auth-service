package com.hand.hcf.app.prepayment.web.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.hand.hcf.app.core.web.dto.DomainObjectDTO;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * 核销明细信息
 * Created by kai.zhang on 2017-10-19.
 */
@Data
public class CashWriteOffDto extends DomainObjectDTO {
    @NotNull
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cshTransactionDetailId;  //支付明细id
    @NotNull
    private String billcode;                //支付流水号
    private String prepaymentRequisitionTypeDesc;          //预付款类型描述
    private String prepaymentRequisitionNumber;            //预付款单据编号
    private ZonedDateTime payDate;              //交易日期(支付时间)
    private Double prepaymentRequisitionAmount;          //借款金额
    @NotNull
    private Double unWriteOffAmount;            //借款余额(未核销金额)
    private Double writeOffAmount;             //本次核销金额
    private Double writeOffAmountBefore;          //此单据，借款支付明细之前核销记录(核销之后，未提交单据，更新核销金额)
    private ZonedDateTime writeOffDate;           //核销日期
    private String periodName;            //期间
    private String status;                //状态:N未生效;P已生效;Y:已核算
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long tenantId;               //租户id
}
