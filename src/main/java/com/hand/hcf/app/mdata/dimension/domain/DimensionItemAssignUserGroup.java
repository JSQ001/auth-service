package com.hand.hcf.app.mdata.dimension.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

@Data
@TableName("sys_dimension_item_ass_u_g")
public class DimensionItemAssignUserGroup extends Domain {
    //维值ID
    private Long dimensionItemId;

    //人员组ID
    private Long userGroupId;
}
