package com.hand.hcf.app.mdata.dimension;

import lombok.Data;

@Data
public class DimensionItemCO {
    private Long id;
    //维值代码
    private String dimensionItemCode;
    //维值名称
    private String dimensionItemName;
    //维度ID
    private Long dimensionId;
    //可见人员范围
    private Integer visibleUserScope;
    //是否启用
    private Boolean enabled;
}
