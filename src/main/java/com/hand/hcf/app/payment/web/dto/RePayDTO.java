package com.hand.hcf.app.payment.web.dto;

import com.hand.hcf.app.payment.domain.CashTransactionDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by cbc on 2017/12/25.
 */
@ApiModel(description = "支付")
@Data
public class RePayDTO {
    @ApiModelProperty(value = "支付明细")
    private List<CashTransactionDetail> details;

    @ApiModelProperty(value = "支付dto")
    private CashPayDTO payDTO;
}

