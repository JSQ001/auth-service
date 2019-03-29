package com.hand.hcf.app.mdata.dimension.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.domain.Domain;
import lombok.Data;

@Data
@TableName("sys_dimension_group_a_item")
public class DimensionItemGroupAssignItem extends Domain {
    //维值ID
    private Long dimensionItemId;
    //维值组ID
    private Long dimensionItemGroupId;
}
