package com.hand.hcf.app.payment.web.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.*;
import lombok.Data;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 核销申请信息
 * Created by kai.zhang on 2017-10-19.
 */

@ApiModel(description = "核销申请信息类")
@Data
public class CashWriteOffRequestWebDto {

    @ApiModelProperty(value = "收款方类型code")
    @NotNull
    private String partnerCategory;           //收款方类型code

    @ApiModelProperty(value = "支付信息收款方id")
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long partnerId;                  //支付信息收款方id

    @ApiModelProperty(value = "单据公司id")
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long companyId;                //单据公司

    @ApiModelProperty(value = "单据类型")
    @NotNull
    private String documentType;                //单据类型

    @ApiModelProperty(value = "核销单据头id")
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long documentHeaderId;            //核销单据头id

    @ApiModelProperty(value = "核销单据计划付款行id")
    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull
    private Long documentLineId;            //核销单据计划付款行id

    @ApiModelProperty(value = "核销单据行金额")
    @NotNull
    private BigDecimal documentLineAmount;           //核销单据行金额

    @ApiModelProperty(value = "具体核销信息")
    @NotNull
    @Valid
    private List<CashWriteOffWebDto> cashWriteOffMsg;            //具体核销信息
}
