package com.hand.hcf.app.mdata.dimension.dto;

import lombok.Data;

import java.util.List;

/**
 * @description
 * @Version: 1.0
 * @author: ShilinMao
 * @date: 2019/4/9 23:10
 */
@Data
public class QueryDimensionIdsForAppDTO {
    private List<Long> dimensionIds;
    private Boolean enabled;
    private Long companyId;
    private Long unitId;
    private Long userId;
}
