package com.hand.hcf.app.payment.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by 刘亮 on 2017/10/9.
 */
@Data
public class InsertDetailDTO {
    private List<Long> dataIds;//通用表id
    private List<Integer> versionNumbers;//通用表版本号
    private List<BigDecimal> currentAmount;//本次支付金额
    private CashPayDTO cashPayDTO;//支付详情dto
}
