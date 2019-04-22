package com.hand.hcf.app.expense.init.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 业务数据导入DTO
 * @version: 1.0
 * @author: liguo.zhao@hand-china.com
 * @date: 2019/4/17
 */
@ApiModel(description = "单据类型关联关系通用对象")
@Data
public class ModuleInitDTO {

    @ApiModelProperty(value = "账套代码")
    private String setOfBooksCode;

    @ApiModelProperty(value = "关联关系列表")
    private List<SourceTypeTargetTypeDTO> sourceTypeTargetTypeDTOList;

}
