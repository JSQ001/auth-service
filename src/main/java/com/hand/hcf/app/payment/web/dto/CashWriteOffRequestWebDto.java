package com.hand.hcf.app.payment.web.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 核销申请信息
 * Created by kai.zhang on 2017-10-19.
 */
@Data
public class CashWriteOffRequestWebDto {
    @NotNull
    private String partnerCategory;           //收款方类型code
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long partnerId;                  //支付信息收款方id
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long companyId;                //单据公司
    @NotNull
    private String documentType;                //单据类型
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long documentHeaderId;            //核销单据头id
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long documentLineId;            //核销单据计划付款行id
    @NotNull
    private BigDecimal documentLineAmount;           //核销单据行金额
    @NotNull
    @Valid
    private List<CashWriteOffWebDto> cashWriteOffMsg;            //具体核销信息
}
