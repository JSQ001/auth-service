package com.hand.hcf.app.payment.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * 核销历史
 * Created by 刘亮 on 2017/12/22.
 */
@Data
public class WriteOffHistoryDTO {

    //序号
    private Integer sequence;

    //核销日期
    private ZonedDateTime writeOffDate;

    //单据编号
    private String documentCode;

    //核销金额
    private BigDecimal writeOffAmount;

    //状态
    private boolean status;

    //币种
    private String currency;


}
