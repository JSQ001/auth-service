package com.hand.hcf.app.mdata.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class TempCostTrendDTO {
    private ZonedDateTime reportDate;//报账日期
    private BigDecimal functionalAmount;//报销本币金额
}
