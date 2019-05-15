package com.hand.hcf.app.mdata.dimension.domain;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.hand.hcf.app.core.annotation.I18nField;
import com.hand.hcf.app.core.domain.DomainI18nEnable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "维度信息")
@TableName("sys_dimension")
public class Dimension extends DomainI18nEnable {
    //维度代码
    @ApiModelProperty(value = "维度代码")
    private String dimensionCode;
    //维度名称
    @ApiModelProperty(value = "维度名称")
    @I18nField
    private String dimensionName;
    //维度序号
    @ApiModelProperty(value = "维度序号")
    private Integer dimensionSequence;
    //账套ID
    @ApiModelProperty(value = "账套ID")
    private Long setOfBooksId;

    @ApiModelProperty(value = "账套名称")
    @TableField(exist = false)
    private String setOfBooksName;
}
