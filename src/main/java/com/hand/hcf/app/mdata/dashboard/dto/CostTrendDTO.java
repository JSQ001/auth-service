package com.hand.hcf.app.mdata.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CostTrendDTO {
    private String date;
    private BigDecimal value;

}
