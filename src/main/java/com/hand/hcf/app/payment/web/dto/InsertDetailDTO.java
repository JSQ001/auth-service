package com.hand.hcf.app.payment.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 刘亮 on 2017/10/9.
 */
@ApiModel(description = "插入支付详情信息")
@Data
public class InsertDetailDTO {
    @ApiModelProperty(value = "通用表id")
    private List<Long> dataIds;//通用表id

    @ApiModelProperty(value = "通用表版本号")
    private List<Integer> versionNumbers;//通用表版本号

    @ApiModelProperty(value = "本次支付金额")
    private List<BigDecimal> currentAmount;//本次支付金额

    @ApiModelProperty(value = "支付详情dto")
    private CashPayDTO cashPayDTO;//支付详情dto
}
