package com.hand.hcf.app.mdata.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CostTrendObjDTO {
    private BigDecimal maxValue;
    private BigDecimal minValue;
    private BigDecimal avgValue;
    private List<CostTrendDTO> list;
}
