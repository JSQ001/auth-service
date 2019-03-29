package com.hand.hcf.app.common.co;

import lombok.Data;

@Data
public class DimensionCO {
    private Long id;
    //维度代码
    private String dimensionCode;
    //维度名称
    private String dimensionName;
    //维度序号
    private Integer dimensionSequence;
    //账套ID
    private Long setOfBooksId;
    //是否启用
    private Boolean enabled;
    //是否已分配
    private Boolean assigned;
}
