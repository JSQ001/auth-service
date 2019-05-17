package com.hand.hcf.app.payment.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Created by 刘亮 on 2017/10/16.
 */
@ApiModel(description = "支付dto")
@Data
public class CashPayingDTO {
    @ApiModelProperty(value = "支付详情id")
    private List<Long> detailIds;

    @ApiModelProperty(value = "版本号")
    private List<Integer> versionNumbers;
}
