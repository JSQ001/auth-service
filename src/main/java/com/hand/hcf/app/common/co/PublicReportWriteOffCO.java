package com.hand.hcf.app.common.co;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * @Author: bin.xie
 * @Description:
 * @Date: Created in 16:08 2018/6/27
 * @Modified by
 */
@Data
public class PublicReportWriteOffCO {
    private Long reportHeaderId; //报账单头id
    private String reportNumber; // 报账单编号
    private String reportStatus; // 核销状态
    private BigDecimal writeOffAmount; // 核销金额
    private ZonedDateTime tranDate; // 核销日期
    private Long lineId; // 预付款单行ID
}
