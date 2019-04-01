package com.hand.hcf.app.prepayment.web.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

/**
 * 核销调用支付接口，更新支付模块核销信息
 * 支付模块计算方法： 核销金额 = 支付原核销金额 + 本次核销金额 - 前核销金额
 * 核销反冲： 使本次核销金额为零，前核销记录为反冲金额，也就可以反冲了
 * Created by kai.zhang on 2017-10-19.
 */
@Data
public class CashWriteOffDetailInformPayDto{
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashTransactionDetailId;  //支付明细id
    private Double writeOffAmountAfter;             //本次核销金额
    private Double writeOffAmountBefore;          //此单据，借款支付明细之前核销记录(核销之后，未提交单据，更新核销金额)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastUpdatedBy;
}
