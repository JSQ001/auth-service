package com.hand.hcf.app.common.co;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 地点级别
 * @author shouting.cheng
 * @date 2019/4/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationLevelCO {
    private Long id;
    private Long tenantId;
    private Long setOfBooksId;
    private String code;
    private String name;
    private Boolean enabled;
    private String remarks;
}
