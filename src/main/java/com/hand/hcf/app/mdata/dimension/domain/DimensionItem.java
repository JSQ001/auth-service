package com.hand.hcf.app.mdata.dimension.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import lombok.Data;

import java.time.ZonedDateTime;

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

    /**
     * 启用日期
     */
    private ZonedDateTime startDate;

    /**
     * 停用日期
     */
    @TableField(value = "end_date",strategy = FieldStrategy.IGNORED)
    private ZonedDateTime endDate;

    /**
     * 分配全部公司标识
     */
    @TableField(value = "all_company_flag")
    private Boolean allCompanyFlag;

}
