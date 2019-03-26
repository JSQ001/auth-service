package com.hand.hcf.app.mdata.dimension.dto;

import lombok.Data;

/**
 * @Autnor shouting.cheng
 * @date 2018/12/24
 */
@Data
public class DimensionItemExportDTO {
    //维值代码
    private String dimensionItemCode;
    //维值名称
    private String dimensionItemName;
    //可见人员范围
    private String visibleUserScope;
    //是否启用
    private String enabled;
}
