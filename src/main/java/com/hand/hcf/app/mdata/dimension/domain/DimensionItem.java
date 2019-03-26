package com.hand.hcf.app.mdata.dimension.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

@Data
@TableName("sys_dimension_item")
public class DimensionItem extends DomainI18nEnable {
    //维值代码
    private String dimensionItemCode;
    //维值名称
    @I18nField
    private String dimensionItemName;
    //维度ID
    private Long dimensionId;
    //可见人员范围
    private Integer visibleUserScope;
}