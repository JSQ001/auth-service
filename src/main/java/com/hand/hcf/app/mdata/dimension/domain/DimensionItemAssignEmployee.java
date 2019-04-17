package com.hand.hcf.app.mdata.dimension.domain;

import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.domain.Domain;
import lombok.Data;

/**
 * @author
 * 维值关联人员表
 */
@Data
@TableName("sys_dimension_item_ass_emp")
public class DimensionItemAssignEmployee extends Domain {
    /**
     * 维值ID
     */
    private Long dimensionItemId;

    /**
     * 员工表主键ID
     */
    private Long contactId;
}
