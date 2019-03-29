package com.hand.hcf.app.mdata.dimension.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

@Data
@TableName("sys_dimension_item_group")
public class DimensionItemGroup extends DomainI18nEnable {
    //维值组代码
    private String dimensionItemGroupCode;
    //维值组名称
    @I18nField
    private String dimensionItemGroupName;
    //维度ID
    private Long dimensionId;

    //是否存在子维值
    @TableField(exist = false)
    private Boolean hasChildren;
}
