package com.hand.hcf.app.expense.init.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 源类型与要关联的类型
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/4/17
 */
@ApiModel(description = "单据类型关联关系")
@Data
public class SourceTypeTargetTypeDTO {

    @ApiModelProperty(value = "账套代码")
    private String setOfBooksCode;

    @ApiModelProperty(value = "单据代码")
    private String sourceTypeCode;

    @ApiModelProperty(value = "关联类型代码")
    private String targetTypeCode;
}
