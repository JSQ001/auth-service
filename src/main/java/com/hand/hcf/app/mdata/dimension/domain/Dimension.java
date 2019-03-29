package com.hand.hcf.app.mdata.dimension.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.core.annotation.I18nField;
import com.hand.hcf.core.domain.DomainI18nEnable;
import lombok.Data;

@Data
@TableName("sys_dimension")
public class Dimension extends DomainI18nEnable {
    //维度代码
    private String dimensionCode;
    //维度名称
    @I18nField
    private String dimensionName;
    //维度序号
    private Integer dimensionSequence;
    //账套ID
    private Long setOfBooksId;

    @TableField(exist = false)
    private String setOfBooksName;
}
